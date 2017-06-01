package com.cloud.ops.esc.wf;

import com.cloud.ops.common.cmd.RemoteExecuteCommand;
import com.cloud.ops.common.cmd.RemoteExecuteResult;
import com.cloud.ops.common.cmd.SCPUtils;
import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.esc.Location;
import com.cloud.ops.toscamodel.impl.Artifact;
import com.cloud.ops.toscamodel.impl.TopologyContext;
import com.cloud.ops.toscamodel.wf.WorkFlowStatus;
import com.cloud.ops.toscamodel.wf.WorkFlowStep;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ningsheng on 2017/5/27.
 */
public class LocalLocationWorkFlowExecutor extends Thread {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String ARTIFACT_PATH = "/opt/iop-ops/artifact";
    private static final String INTERFACE_PATH = "/opt/iop-ops/interface";
    private TopologyContext topologyContext;
    private ApplicationContext context;
    private Location location;

    public LocalLocationWorkFlowExecutor(ApplicationContext context, TopologyContext topologyContext, Location location) {
        this.context = context;
        this.topologyContext = topologyContext;
        this.location = location;
    }

    @Override
    public void run() {
        WorkFlowService service = context.getBean(WorkFlowService.class);
        WorkFlowStepService stepService = context.getBean(WorkFlowStepService.class);

        topologyContext.getWorkFlowMap().forEach((name, wf) -> {
            WorkFlowStep currentStep = null;
            wf.setStartAt(new Date());
            service.save(wf);
            //save step information in database at first
            List<WorkFlowStep> steps = Lists.newArrayList();
            wf.getSteps().forEach(step -> {
                Map<String, Object> hostInfo = topologyContext.getNodeTemplateMap().get(step.getHost()).getAttributes();
                List<String> hostIps = (List<String>) hostInfo.get("hosts");
                hostIps.forEach(hostIp -> {
                    WorkFlowStep stepDb = new WorkFlowStep();
                    BeanUtils.copyNotNullProperties(step, stepDb);
                    stepDb.setHostIp(hostIp.trim());
                    stepDb.setWorkFlowId(wf.getId());
                    stepService.save(stepDb);
                    steps.add(stepDb);
                });
            });
            //execute workflow
            for (WorkFlowStep step : steps) {
                step.setStartAt(new Date());
                currentStep = step;
                Map<String, Object> hostInfo = topologyContext.getNodeTemplateMap().get(step.getHost()).getAttributes();
                String user = (String) hostInfo.get("user");
                String password = (String) hostInfo.get("password");
                String hostIp = step.getHostIp();
                String message;
                RemoteExecuteCommand ssh = new RemoteExecuteCommand(hostIp, user, password);
                try {
                    ssh.execute("mkdir -p " + ARTIFACT_PATH);
                    for (Artifact artifact : step.getArtifacts()) {
                        SCPUtils.uploadFileToServer(hostIp, user, password, (String) location.getMetaProperties().get(artifact.getFile()),
                                artifact.getFile(), ARTIFACT_PATH, "0644");
                    }
                    SCPUtils.uploadFileToServer(hostIp, user, password, (String) location.getMetaProperties().get(step.getShellScript()),
                            step.getShellScript(), ARTIFACT_PATH, "0744");

                    StringBuilder shellContent = new StringBuilder();
                    for (Map.Entry<String, String> ENV : step.getEnv().entrySet()) {
                        shellContent.append("export ").append(ENV.getKey()).append("=").append(ENV.getValue()).append(";");
                    }
                    shellContent.append("sh " + ARTIFACT_PATH + "/").append(step.getShellScript());
                    RemoteExecuteResult executeResult = ssh.execute(shellContent.toString());
                    message = executeResult.getMessage();
                    if (message != null && message.length() > 65534) {
                        message = message.substring(message.length() - 65534);
                    }
                    step.setEndAt(new Date());
                    step.setDescription(message);
                    if (executeResult.getExitCode() == null) {
                        step.setStatus(WorkFlowStatus.SUCCESS);
                    } else if (executeResult.getExitCode() == 0) {
                        step.setStatus(WorkFlowStatus.SUCCESS);
                    } else {
                        step.setStatus(WorkFlowStatus.FAIL);
                        stepService.save(step);
                        throw new RuntimeException(step.getHost() + "上执行[" + step.getName() + "]操作失败: " + message);
                    }
                    stepService.save(step);
                } catch (IOException e) {
                    currentStep.setDescription(e.getMessage());
                    currentStep.setStatus(WorkFlowStatus.FAIL);
                    stepService.save(currentStep);
                    wf.setEndAt(new Date());
                    wf.setStatus(WorkFlowStatus.FAIL);
                    service.save(wf);
                    e.printStackTrace();
                    logger.error(e.getMessage(), e);
                }
            }
            wf.setEndAt(new Date());
            wf.setStatus(WorkFlowStatus.SUCCESS);
            service.save(wf);
        });
    }
}
