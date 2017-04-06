package com.cloud.ops.entity.deployment;

import com.cloud.ops.entity.BaseObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="topology")
@Getter
@Setter
public class Topology extends BaseObject {
    String yamlFilePath;
}
