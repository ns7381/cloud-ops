package com.cloud.ops.core.topology;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.core.model.topology.Topology;
import com.cloud.ops.core.topology.repository.TopologyRepository;
import com.cloud.ops.dao.modal.SortConstant;
import com.cloud.ops.toscamodel.Tosca;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
            topology.setTopologyContext(Tosca.newEnvironment(topology.getYamlFilePath()).getTopologyContext());
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
        Assert.notNull(id, "id can not be null");
        Topology db = this.get(id);
        BeanUtils.copyNotNullProperties(topology, db);
        dao.save(db);
        return db;
    }

    public List<Topology> getListWithContext() {
        List<Topology> topologies = dao.findAll();
        for (Topology topology : topologies) {
            setTopologyContext(topology);
        }
        return topologies;
    }


}
