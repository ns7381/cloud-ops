package com.cloud.ops.service;

import com.cloud.ops.repository.TopologyRepository;
import com.cloud.ops.entity.topology.Topology;
import com.cloud.ops.toscamodel.INodeTemplate;
import com.cloud.ops.toscamodel.INodeType;
import com.cloud.ops.toscamodel.IToscaEnvironment;
import com.cloud.ops.toscamodel.Tosca;
import com.cloud.ops.utils.BeanUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.FileNotFoundException;
import java.io.FileReader;
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

	public Topology create(Topology shell){
		dao.save(shell);
		return shell;
	}

    public List<Topology> findAll() {
        return dao.findAll();
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public Topology update(String id, Topology topology){
        Assert.notNull(id, "id is required");
        Topology db = this.get(id);
        BeanUtils.copyNotNullProperties(topology, db);
        dao.save(db);
        return db;
	}

    public List<Map<String, Object>> getHosts(String id) {
        Topology topology = dao.findOne(id);
        List<Map<String, Object>> rst = Lists.newArrayList();
        if (StringUtils.isNotBlank(topology.getYamlFilePath())) {
            IToscaEnvironment toscaEnvironment = Tosca.newEnvironment();
            try {
                toscaEnvironment.readFile(new FileReader(topology.getYamlFilePath()), false);
                INodeType rootNode = (INodeType) toscaEnvironment.getNamedEntity("tosca.nodes.Compute");
                Iterable<INodeTemplate> rootNodeTemplate = toscaEnvironment.getNodeTemplatesOfType(rootNode);
                for (INodeTemplate nodeType : rootNodeTemplate){
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("name", nodeType.toString());
                    map.put("type", nodeType.baseType().toString());
                    rst.add(map);
                }
            } catch (FileNotFoundException e) {
                logger.error("yaml file not find. ", e);
            }
        }
        return rst;
    }
}
