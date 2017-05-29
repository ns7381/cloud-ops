/**
 * 
 */
package com.cloud.ops.core.application;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.core.application.repository.ApplicationEnvironmentRepository;
import com.cloud.ops.core.model.application.ApplicationEnvironment;
import com.cloud.ops.dao.modal.SortConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class ApplicationEnvironmentService {
	
	@Autowired
	private ApplicationEnvironmentRepository applicationEnvironmentRepository;
	
	public ApplicationEnvironment get(String id){
		return applicationEnvironmentRepository.findOne(id);
	}

    @PostFilter("hasAuthority('ADMIN') or principal.username.equals(filterObject.username)")
    public List<ApplicationEnvironment> getAll() {
		return applicationEnvironmentRepository.findAll(SortConstant.CREATED_AT);
	}

    public ApplicationEnvironment add(ApplicationEnvironment applicationEnvironment) {
        applicationEnvironmentRepository.save(applicationEnvironment);
        return applicationEnvironment;
    }

    public ApplicationEnvironment edit(ApplicationEnvironment applicationEnvironment) {
        Assert.notNull(applicationEnvironment.getId(), "id can not be null");
        ApplicationEnvironment db = this.get(applicationEnvironment.getId());
        BeanUtils.copyNotNullProperties(applicationEnvironment, db);
        applicationEnvironmentRepository.save(db);
        return db;
    }

    public void delete(String id) {
        Assert.hasLength(id, "id can not be null");
        applicationEnvironmentRepository.delete(id);
    }
}
