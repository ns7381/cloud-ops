package com.cloud.ops.core.topology;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.core.model.topology.TopologyEntity;
import com.cloud.ops.core.topology.repository.TopologyRepository;
import com.cloud.ops.dao.modal.SortConstant;
import com.cloud.ops.tosca.Tosca;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;


@Service
@Transactional
public class TopologyService {
    @Autowired
    private TopologyRepository dao;

    public TopologyEntity get(String id) {
        TopologyEntity topology = dao.findOne(id);
        setTopologyContext(topology);
        return topology;
    }

    private void setTopologyContext(TopologyEntity topology) {
        if (StringUtils.isNotBlank(topology.getYamlFilePath())) {
            topology.setTopology(Tosca.read(topology.getYamlFilePath()));
        }
    }

    public TopologyEntity create(TopologyEntity shell) {
        dao.save(shell);
        return shell;
    }

    public List<TopologyEntity> findAll() {
        return dao.findAll(SortConstant.CREATED_AT);
    }

    public void delete(String id) {
        dao.delete(id);
    }

    public TopologyEntity update(String id, TopologyEntity topology) {
        Assert.notNull(id, "id can not be null");
        TopologyEntity db = this.get(id);
        BeanUtils.copyNotNullProperties(topology, db);
        dao.save(db);
        return db;
    }

    public List<TopologyEntity> getListWithContext() {
        List<TopologyEntity> topologies = dao.findAll();
        for (TopologyEntity topology : topologies) {
            setTopologyContext(topology);
        }
        return topologies;
    }


}
