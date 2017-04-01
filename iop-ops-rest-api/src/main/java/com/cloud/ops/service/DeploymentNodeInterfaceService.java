package com.cloud.ops.service;

import com.cloud.ops.dao.DeploymentNodeInterfaceDao;
import com.cloud.ops.entity.deployment.*;

import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DeploymentNodeInterfaceService {
	
	@Autowired
	private DeploymentNodeInterfaceDao dao;
    @Autowired
	private DeploymentNodeService deploymentNodeService;
    @Autowired
	private DeploymentNodeInterfaceInputService deploymentNodeInterfaceInputService;
    @Autowired
	private DeploymentNodePropertyService deploymentNodePropertyService;

	public DeploymentNodeInterface get(String id) {
		return dao.findOne(id);
	}

	public DeploymentNodeInterface create(DeploymentNodeInterface deploymentNodeArtifact){
		dao.save(deploymentNodeArtifact);
		return deploymentNodeArtifact;
	}

    @Transactional(readOnly=true)
    public List<DeploymentNodeInterface> getList(Map<String, Object> params) {
        if (params.containsKey("deploymentTopologyId")) {
            List<DeploymentNodeInterface> rst = new ArrayList<>();
            String topologyId = (String) params.get("deploymentTopologyId");
            List<DeploymentNode> nodes = deploymentNodeService.getByTopologyId(topologyId);
            Map<String, String> propertyKV = new HashMap<>();
            for (DeploymentNode node : nodes) {
                List<DeploymentNodeProperty> nodeProperties = deploymentNodePropertyService.getByNodeId(node.getId());
                for (DeploymentNodeProperty nodeProperty : nodeProperties) {
                    propertyKV.put(node.getName() + ", " + nodeProperty.getName(), nodeProperty.getValue());
                }
            }
            for (DeploymentNode node : nodes) {
                List<DeploymentNodeInterface> interfaces = dao.findByDeploymentNodeId(node.getId());
                for (DeploymentNodeInterface anInterface : interfaces) {
                    List<DeploymentNodeInterfaceInput> inputs = deploymentNodeInterfaceInputService.getByInterfaceId(anInterface.getId());
                    for (DeploymentNodeInterfaceInput input : inputs) {
                        if (input.getType().equals(DeploymentNodeInterfaceInputType.PROPERTY)) {
                            input.setValue(propertyKV.get(input.getValue()));
                        }
                    }
                    anInterface.setInterfaceInputs(inputs);
                }
                rst.addAll(interfaces);
            }
            return rst;
        }
        return dao.findByDeploymentNodeId((String) params.get("deploymentTopologyId"));
    }

    public DeploymentNodeInterface getWithInputs(String id) {
        DeploymentNodeInterface anInterface = dao.findOne(id);
        List<DeploymentNodeInterfaceInput> inputs = deploymentNodeInterfaceInputService.getByInterfaceId(anInterface.getId());
        anInterface.setInterfaceInputs(inputs);
        return anInterface;
    }
    private List<DeploymentNodeInterface> getWithInputsByNodeId(String nodeId) {
        List<DeploymentNodeInterface> interfaces = dao.findByDeploymentNodeId(nodeId);
        for (DeploymentNodeInterface anInterface : interfaces) {
            List<DeploymentNodeInterfaceInput> inputs = deploymentNodeInterfaceInputService.getByInterfaceId(anInterface.getId());
            anInterface.setInterfaceInputs(inputs);
        }
        return interfaces;
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public DeploymentNodeInterface update(DeploymentNodeInterface deploymentNodeArtifact){
        Assert.notNull(deploymentNodeArtifact.getId());
        DeploymentNodeInterface db = this.get(deploymentNodeArtifact.getId());
        BeanUtils.copyNotNullProperties(deploymentNodeArtifact, db);
        dao.save(db);
        return db;
	}

}
