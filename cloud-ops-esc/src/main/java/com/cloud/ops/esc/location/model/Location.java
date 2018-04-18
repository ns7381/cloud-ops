/**
 * 
 */
package com.cloud.ops.esc.location.model;

import com.cloud.ops.dao.modal.IdEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="location")
@Getter
@Setter
public class Location extends IdEntity {
	
    private String name;
    private String type;
    private String yamlFilePath;
}
