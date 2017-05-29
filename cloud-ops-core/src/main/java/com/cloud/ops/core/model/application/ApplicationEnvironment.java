package com.cloud.ops.core.model.application;

import com.cloud.ops.dao.modal.BaseObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/1/13.
 */

@Entity
@Table(name="application_environment")
@Getter
@Setter
public class ApplicationEnvironment extends BaseObject {
    private String type;
    @Column(name = "username")
    @CreatedBy
    private String username;
}
