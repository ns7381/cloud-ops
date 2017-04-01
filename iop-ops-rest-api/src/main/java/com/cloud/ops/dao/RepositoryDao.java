package com.cloud.ops.dao;

import com.cloud.ops.entity.Resource.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/13.
 */
@org.springframework.stereotype.Repository
public interface RepositoryDao extends JpaRepository<Repository, Serializable> {
}
