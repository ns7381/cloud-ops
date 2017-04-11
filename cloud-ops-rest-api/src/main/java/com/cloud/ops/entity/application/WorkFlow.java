package com.cloud.ops.entity.application;

import com.cloud.ops.entity.BaseObject;
import com.cloud.ops.entity.IdEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/9.
 */
@Entity
@Table(name="work_flow")
@Getter
@Setter
public class WorkFlow extends IdEntity {
    private String objectId;
    private String step;
    private String message;
    private Date startAt;
    private Date endAt;
}
