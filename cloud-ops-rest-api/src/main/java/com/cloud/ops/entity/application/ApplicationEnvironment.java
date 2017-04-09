package com.cloud.ops.entity.application;

import com.cloud.ops.entity.BaseObject;
import lombok.Getter;
import lombok.Setter;

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
}
