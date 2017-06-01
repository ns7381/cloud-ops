/**
 * 
 */
package com.cloud.ops.core.model.application;

import com.cloud.ops.dao.modal.IdEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="application_env_meta")
@Getter
@Setter
public class EnvironmentMetadata extends IdEntity {
	
    private String name;
    private String value;
    private String envId;
}
