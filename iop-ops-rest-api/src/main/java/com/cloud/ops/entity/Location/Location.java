package com.cloud.ops.entity.Location;

import com.cloud.ops.entity.BaseObject;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/1/13.
 */

@Entity
@Table(name="location")
public class Location extends BaseObject {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
