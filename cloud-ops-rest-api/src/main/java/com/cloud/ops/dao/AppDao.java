/**
 * 
 */
package com.cloud.ops.dao;

import com.cloud.ops.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface AppDao extends JpaRepository<Application, Serializable> {
	
}
