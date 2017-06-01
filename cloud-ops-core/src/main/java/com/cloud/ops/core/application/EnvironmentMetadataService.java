/**
 * 
 */
package com.cloud.ops.core.application;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.core.application.repository.EnvironmentMetadataRepository;
import com.cloud.ops.core.model.application.EnvironmentMetadata;
import com.cloud.ops.dao.modal.SortConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class EnvironmentMetadataService {
	
	@Autowired
	private EnvironmentMetadataRepository dao;
	
	public EnvironmentMetadata get(String id){
		return dao.findOne(id);
	}

    public List<EnvironmentMetadata> getAll() {
		return dao.findAll(SortConstant.CREATED_AT);
	}

    public List<EnvironmentMetadata> findByEnvId(String envId) {
        return dao.findByEnvId(envId);
    }

    public EnvironmentMetadata create(EnvironmentMetadata EnvironmentMetadata) {
        dao.save(EnvironmentMetadata);
        return EnvironmentMetadata;
    }

    public EnvironmentMetadata update(EnvironmentMetadata EnvironmentMetadata) {
        Assert.notNull(EnvironmentMetadata.getId(), "id can not be null");
        EnvironmentMetadata db = this.get(EnvironmentMetadata.getId());
        BeanUtils.copyNotNullProperties(EnvironmentMetadata, db);
        dao.save(db);
        return db;
    }

    public void delete(String id) {
        Assert.hasLength(id, "id can not be null");
        dao.delete(id);
    }
}
