package com.cloud.ops.service;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.dao.modal.SortConstant;
import com.cloud.ops.repository.TopologyRepository;
import com.cloud.ops.entity.topology.Topology;
import com.cloud.ops.toscamodel.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import java.io.FileNotFoundException;
import java.util.List;


@Service
@Transactional
public class TopologyService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TopologyRepository dao;

    public Topology get(String id) {
        Topology topology = dao.findOne(id);
        setTopologyContext(topology);
        return topology;
    }

    private void setTopologyContext(Topology topology) {
        if (StringUtils.isNotBlank(topology.getYamlFilePath())) {
            IToscaEnvironment toscaEnvironment = Tosca.newEnvironment();
            try {
                toscaEnvironment.readFile(topology.getYamlFilePath(), false);
                topology.setTopologyContext(toscaEnvironment.getTopologyContext());
            } catch (FileNotFoundException e) {
                logger.error("yaml file not find. ", e);
            }
        }
    }

    public Topology create(Topology shell) {
        dao.save(shell);
        return shell;
    }

    public List<Topology> findAll() {
        return dao.findAll(SortConstant.CREATED_AT);
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

    public List<Topology> getListWithComputes() {
        List<Topology> topologies = dao.findAll();
        for (Topology topology : topologies) {
            setTopologyContext(topology);
        }
        return topologies;
    }


}
