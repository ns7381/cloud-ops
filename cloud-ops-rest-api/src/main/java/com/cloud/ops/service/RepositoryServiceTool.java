package com.cloud.ops.service;

import com.cloud.ops.dao.RepositoryDao;
import com.cloud.ops.entity.Resource.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RepositoryServiceTool {
	
	@Autowired
	private RepositoryDao dao;

    /**
     * 保存
     * @return
     */
    public Repository save(Repository entity) {
        dao.save(entity);
        return entity;
    }
}
