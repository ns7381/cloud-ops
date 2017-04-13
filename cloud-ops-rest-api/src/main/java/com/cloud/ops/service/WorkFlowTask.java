package com.cloud.ops.service;

import com.cloud.ops.configuration.ws.CustomWebSocketHandler;
import com.cloud.ops.configuration.ws.WebSocketConstants;
import com.cloud.ops.entity.workflow.WorkFlow;
import com.cloud.ops.entity.workflow.WorkFlowStatus;
import com.cloud.ops.entity.workflow.WorkFlowStep;
import com.cloud.ops.utils.RemoteExecuteCommand;
import com.cloud.ops.utils.RemoteExecuteResult;
import com.cloud.ops.utils.SCPUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/5.
 */
public class WorkFlowTask extends Thread {
    private static final String ARTIFACT_PATH = "/opt/iop-ops/artifact";
    private static final String INTERFACE_PATH = "/opt/iop-ops/interface";
    private WorkFlow entity;
    ApplicationContext context;
    List<WorkFlowStep> steps;

    public WorkFlowTask(ApplicationContext context, WorkFlow entity, List<WorkFlowStep> steps) {
        this.context = context;
        this.entity = entity;
        this.steps = steps;
    }

    @Override
    public void run() {
        run(entity);
    }

    public void run(WorkFlow workFlow) {
        WorkFlowService service = context.getBean(WorkFlowService.class);
        WorkFlowStepService stepService = context.getBean(WorkFlowStepService.class);
        CustomWebSocketHandler webSocketHandler = context.getBean(CustomWebSocketHandler.class);
        ResourcePackageService resourcePackageService = context.getBean(ResourcePackageService.class);
        WorkFlowStep currentStep = null;
        try {
            //Traversal interface template to execute
            for (WorkFlowStep step : steps) {
                currentStep = step;
                workFlow.setStep(step.getName());
                String message;
                String ip = step.getLocations().get(0).get("ip");
                String user = step.getLocations().get(0).get("user");
                String password = step.getLocations().get(0).get("password");
                RemoteExecuteCommand remoteExecuteCommand = new RemoteExecuteCommand(ip, user, password);
                remoteExecuteCommand.execute("mkdir -p " + ARTIFACT_PATH);
                for (Map.Entry<String, String> archive : step.getArchives().entrySet()) {
                    SCPUtils.uploadFileToServer(ip, user, password,
                            archive.getValue(), new File(step.getArchives().get(archive.getKey())).getName(), ARTIFACT_PATH,
                            archive.getKey().equals("script") ? "0744" : "0644");
                }
                StringBuilder shellContent = new StringBuilder();
                for (Map.Entry<String, String> ENV : step.getEnv().entrySet()) {
                    shellContent.append("export ").append(ENV.getKey()).append("=").append(ENV.getValue()).append(";");
                }
                shellContent.append("sh " + ARTIFACT_PATH + "/").append(new File(step.getArchives().get("script")).getName());
                RemoteExecuteResult executeResult = remoteExecuteCommand.execute(shellContent.toString());
                message = executeResult.getMessage();
                message = message.replaceAll("\\s+\\d+K\\s+\\d+\\.?\\d*(M|K)?\\n\\r", "");
                if(message.length() > 65534){
                    message = message.substring(message.length()-65534);
                }
                step.setDescription(message);
                if (executeResult.getExitCode() == null) {
                    step.setStatus(WorkFlowStatus.SUCCESS);
                } else if (executeResult.getExitCode() == 0) {
                    step.setStatus(WorkFlowStatus.SUCCESS);
                } else {
                    step.setStatus(WorkFlowStatus.FAIL);
                    throw new RuntimeException(step.getHostIp() + "上执行[" + step.getName() + "]操作失败: " + message);
                }
                stepService.save(step);
            }
            workFlow.setStatus(WorkFlowStatus.SUCCESS);
            service.save(workFlow);
            webSocketHandler.sendMsg(WebSocketConstants.WORKFLOW_STATUS, workFlow);
            if (StringUtils.isNotBlank(workFlow.getPackageId())) {
                resourcePackageService.updateDeployStatus(workFlow.getPackageId());
            }

        } catch (IOException e) {
            currentStep.setDescription(e.getMessage());
            currentStep.setStatus(WorkFlowStatus.FAIL);
            stepService.save(currentStep);
            workFlow.setStatus(WorkFlowStatus.FAIL);
            service.save(workFlow);
            webSocketHandler.sendMsg(WebSocketConstants.WORKFLOW_STATUS, workFlow);
            e.printStackTrace();
        }
    }
}
