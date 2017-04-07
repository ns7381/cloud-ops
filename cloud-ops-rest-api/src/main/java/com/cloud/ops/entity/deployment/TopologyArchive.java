package com.cloud.ops.entity.deployment;

import com.cloud.ops.entity.BaseObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/1/13.
 */
@Getter
@Setter
@Entity
@Table(name="topology_archive")
public class TopologyArchive extends BaseObject {
    private String topologyId;
    private String filePath;
}
