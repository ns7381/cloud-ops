package com.cloud.ops.service;

import com.cloud.ops.entity.application.DeploymentNode;
import com.cloud.ops.repository.TopologyRepository;
import com.cloud.ops.entity.topology.Topology;
import com.cloud.ops.toscamodel.*;
import com.cloud.ops.toscamodel.basictypes.impl.TypeList;
import com.cloud.ops.toscamodel.basictypes.impl.TypeString;
import com.cloud.ops.utils.BeanUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class TopologyService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TopologyRepository dao;

    public Topology get(String id) {
        Topology topology = dao.findOne(id);
        if (StringUtils.isNotBlank(topology.getYamlFilePath())) {
            IToscaEnvironment toscaEnvironment = Tosca.newEnvironment();
            try {
                toscaEnvironment.readFile(new FileReader(topology.getYamlFilePath()), false);
                topology.setToscaEnvironment(toscaEnvironment);
            } catch (FileNotFoundException e) {
                logger.error("yaml file not find. ", e);
            }
        }
        return topology;
    }

    public Topology create(Topology shell) {
        dao.save(shell);
        return shell;
    }

    public List<Topology> findAll() {
        return dao.findAll();
    }

    public void delete(String id) {
        dao.delete(id);
    }

    public Topology update(String id, Topology topology) {
        Assert.notNull(id, "id is required");
        Topology db = this.get(id);
        BeanUtils.copyNotNullProperties(topology, db);
        dao.save(db);
        return db;
    }

    private List<DeploymentNode> getComputeNodeTemplates(Topology topology) {
        List<DeploymentNode> rst = Lists.newArrayList();
        if (StringUtils.isNotBlank(topology.getYamlFilePath())) {
            IToscaEnvironment toscaEnvironment = Tosca.newEnvironment();
            try {
                toscaEnvironment.readFile(new FileReader(topology.getYamlFilePath()), false);
                INodeType rootNode = (INodeType) toscaEnvironment.getNamedEntity("tosca.nodes.Compute");
                Iterable<INodeTemplate> rootNodeTemplate = toscaEnvironment.getNodeTemplatesOfType(rootNode);
                for (INodeTemplate nodeType : rootNodeTemplate) {
                    rst.add(DeploymentNode.builder().name(nodeType.toString()).type(nodeType.baseType().toString()).build());
                }
            } catch (FileNotFoundException e) {
                logger.error("yaml file not find. ", e);
            }
        }
        return rst;
    }

    public List<Topology> getListWithComputes() {
        List<Topology> topologies = dao.findAll();
        for (Topology topology : topologies) {
            topology.setNodes(getComputeNodeTemplates(topology));
        }
        return topologies;
    }

    public void updateAttribute(String yamlFilePath, String nodeId, Map<String, Object> attributes) {
        IToscaEnvironment toscaEnvironment = Tosca.newEnvironment();
        try {
            toscaEnvironment.readFile(new FileReader(yamlFilePath), false);
            INodeType nodeTypes = (INodeType) toscaEnvironment.getNamedEntity("tosca.nodes.Root");
            Iterable<INodeTemplate> nodes = toscaEnvironment.getNodeTemplatesOfType(nodeTypes);
            for (INodeTemplate node : nodes) {
                if (nodeId.equals(node.toString())) {
                    for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
                        Object value = attribute.getValue();
                        if (value instanceof String) {
                            node.declaredAttributes().put(attribute.getKey(), TypeString.instance().instantiate(value));
                        } else if (value instanceof List) {
                            node.declaredAttributes().put(attribute.getKey(), TypeList.instance(TypeString.instance()).instantiate(value));
                        }
                    }
                }
            }
            toscaEnvironment.writeFile(new FileWriter(yamlFilePath));
        } catch (FileNotFoundException e) {
            logger.error("yaml file not find. ", e);
        } catch (IOException e) {
            logger.error("yaml write error", e);
        }
    }
}
