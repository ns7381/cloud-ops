package com.cloud.ops.tosca.model.workflow;

import com.cloud.ops.tosca.model.Topology;
import com.cloud.ops.tosca.model.definition.IValue;
import com.cloud.ops.tosca.model.definition.PropertyValue;
import com.cloud.ops.tosca.model.template.Interface;
import com.cloud.ops.tosca.model.template.NodeTemplate;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.cloud.ops.tosca.model.normative.NormativeArtifactConstants.ARTIFACT_PATH;
import static com.cloud.ops.tosca.model.normative.ToscaFunctionConstants.*;

/**
 * Created by Nathan on 2017/5/23.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkFlowBuilder {

    public static WorkFlow buildWorkFlow(Topology topology,String nodeName, String workflowName) {
        WorkFlow workFlow = new WorkFlow();
        workFlow.setName(workflowName);
        topology.getNodeTemplates().entrySet()
                .stream()
                .filter(e -> e.getValue().getInterfaces().get(workflowName) != null && e.getValue().getName().equals(nodeName))
                .forEach(e -> workFlow.setSteps(processInterface(topology, e.getKey(), workflowName)));
        return workFlow;
    }

    private static List<WorkFlowStep> processInterface(Topology topology, String nodeName, String interfaceName) {
        List<WorkFlowStep> workFlowSteps = Lists.newArrayList();
        WorkFlowStep step = new WorkFlowStep();
        step.setName(interfaceName);
        Map<String, NodeTemplate> nodeMap = topology.getNodeTemplates();
        NodeTemplate node = nodeMap.get(nodeName);
        Interface doInterface = node.getInterfaces().get(interfaceName);
        //1 process dependency
        doInterface.getDependencies().stream()
                .filter(v -> v.getValue().getFunction().equals(DO_INTERFACE))
                .forEach(dependency -> {
                    List<String> params = dependency.getValue().getParameters();
                    String targetNode = "SELF".equals(params.get(0)) ? nodeName : params.get(0);
                    workFlowSteps.addAll(processInterface(topology, targetNode, params.get(1)));
                });

        //2 process host requirement
        node.getRequirements().stream()
                .filter(v -> "host".equals(v.getType()))
                .forEach(v -> step.setHost(v.getValue()));

        //3 process inputs
        processInputs(nodeName, doInterface, nodeMap, step);

        workFlowSteps.add(step);
        return workFlowSteps;
    }

    private static void processInputs(String nodeName, Interface doInterface, Map<String, NodeTemplate> nodeMap,
                                      WorkFlowStep workFlowStep) {
        StringBuilder command = new StringBuilder();
        doInterface.getInputs().forEach((k, v) -> {
            if (GET_ATTRIBUTE.equals(v.getFunction())) {
                List<String> params = v.getParameters();
                String targetNode = params.get(0).equals("SELF") ? nodeName : params.get(0);
                IValue value = nodeMap.get(targetNode).getAttributes().get(params.get(1));
                if (value instanceof PropertyValue) {
                    command.append("export ").append(k).append("=").append(((PropertyValue) value).getValue()).append(";");
                }
            } else if (GET_ARTIFACT.equals(v.getFunction())) {
                List<String> params = v.getParameters();
                String targetNode = params.get(0).equals("SELF") ? nodeName : params.get(0);
                String file = nodeMap.get(targetNode).getArtifacts().get(params.get(1)).getFile();
                workFlowStep.getFiles().add(file);
                command.append("export ").append(k).append("=").append(ARTIFACT_PATH).append("/").append(file).append(";");
            }
        });
        workFlowStep.getFiles().add(doInterface.getImplementation());
        String shell = ARTIFACT_PATH + "/" + doInterface.getImplementation();
        command.append("chmod 744 ").append(shell).append(";").append("sh ").append(shell);
        workFlowStep.setCommand(String.valueOf(command));
    }

}
