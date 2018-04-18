package com.cloud.ops.esc.wf;

import ch.ethz.ssh2.Connection;
import com.cloud.ops.common.cmd.RemoteExecute;
import com.cloud.ops.common.cmd.RemoteExecuteResult;
import com.cloud.ops.common.exception.OpsException;
import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.esc.wf.model.WorkFlowEntity;
import com.cloud.ops.esc.wf.model.WorkFlowStepEntity;
import com.cloud.ops.tosca.model.Topology;
import com.cloud.ops.tosca.model.definition.ListPropertyValue;
import com.cloud.ops.tosca.model.definition.PropertyValue;
import com.cloud.ops.tosca.model.template.NodeTemplate;
import com.cloud.ops.tosca.model.workflow.WorkFlow;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.cloud.ops.esc.wf.model.WorkFlowStatus.FAIL;
import static com.cloud.ops.esc.wf.model.WorkFlowStatus.SUCCESS;
import static com.cloud.ops.tosca.model.normative.NormativeArtifactConstants.ARTIFACT_PATH;
import static com.cloud.ops.tosca.model.normative.NormativeNodeConstants.COMPUTE_TYPE;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/24
 */
@Service
public class WorkFlowExecutor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private WorkFlowService service;
    @Autowired
    private WorkFlowStepService stepService;

    public void executeWorkFlow(Topology topology, WorkFlowEntity entity, Map<String, Object> inputs) {
        WorkFlow workFlow = topology.getWorkFlow(entity.getNodeName(), entity.getName());
        service.save(entity);
        int[] idx = {0};
        List<NodeTemplate> hosts = topology.getNodeTemplatesOfType(COMPUTE_TYPE);
        List<WorkFlowStepEntity> steps = Lists.newArrayList();
        workFlow.getSteps().forEach(step -> {
            Host hostInfo = getHostInfo(hosts, step.getHost());
            hostInfo.getIps().forEach(ip -> {
                WorkFlowStepEntity stepEntity = new WorkFlowStepEntity();
                BeanUtils.copyNotNullProperties(step, stepEntity);
                stepEntity.setIndex(++idx[0]);
                stepEntity.setWorkFlowId(entity.getId());
                stepEntity.setHostIp(ip);
                stepEntity.setUsername(hostInfo.getUser());
                stepEntity.setPassword(hostInfo.getPassword());
                stepService.save(stepEntity);
                steps.add(stepEntity);
            });
        });
        entity.setSteps(steps);
        new Thread(() -> doExecute(entity, inputs)).start();
    }

    private void doExecute(WorkFlowEntity entity, Map<String, Object> inputs) {
        entity.getSteps().forEach((WorkFlowStepEntity step) -> {
            step.setStartAt(new Date());
            RemoteExecuteResult result = null;
            Connection conn = null;
            try {
                conn = RemoteExecute.getConnection(step.getHostIp(), step.getUsername(), step.getPassword());
                RemoteExecute.execute(conn, "mkdir -p " + ARTIFACT_PATH);
                for (String v : step.getFiles()) {
                    RemoteExecute.uploadFileToServer(conn, (String) inputs.get(v), v, ARTIFACT_PATH);
                }
                result = RemoteExecute.execute(conn, step.getCommand());
                if (!(result.getExitCode() == null || result.getExitCode() == 0)) {
                    throw new IOException(result.getMessage());
                }
                step.setStatus(SUCCESS);
            } catch (Exception e) {
                logger.error("local location work flow error", e);
                step.setDescription(e.getMessage());
                step.setStatus(FAIL);
                entity.setEndAt(new Date());
                entity.setStatus(FAIL);
                service.save(entity);
            } finally {
                if (conn != null) conn.close();
                if (result != null) {
                    String des = String.valueOf(result.getMessage());
                    if (des.length() > 65534) {
                        step.setDescription(des.substring(0, 65534));
                    } else {
                        step.setDescription(des);
                    }
                }
                step.setEndAt(new Date());
                stepService.save(step);
            }
        });
        entity.setEndAt(new Date());
        entity.setStatus(SUCCESS);
        service.save(entity);
    }

    private Host getHostInfo(List<NodeTemplate> hosts, String nodeName) {
        Host hostInfo = new Host();
        hosts.forEach(host -> {
            long count = host.getRequirements()
                    .stream()
                    .filter(v -> v.getType().equals("host") && v.getValue().equals(host.getName()))
                    .count();
            if (host.getName().equals(nodeName) || count > 0) {
                hostInfo.setIps(((ListPropertyValue) host.getAttributes().get("hosts")).getValue());
                hostInfo.setUser((String) ((PropertyValue) host.getAttributes().get("user")).getValue());
                hostInfo.setPassword((String) ((PropertyValue) host.getAttributes().get("password")).getValue());
            }
        });
        return hostInfo;
    }

    @Getter
    @Setter
    class Host {
        List<String> ips;
        String user;
        String password;
    }
}
