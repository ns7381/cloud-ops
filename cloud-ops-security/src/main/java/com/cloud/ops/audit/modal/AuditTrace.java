package com.cloud.ops.audit.modal;

import com.cloud.ops.dao.modal.BaseObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Nathan on 2017/4/22.
 */
@Entity
@Table(name = "audit_trace")
@Setter
@Getter
public class AuditTrace extends BaseObject{
    private String username;
    private String method;
    private String path;
    private String host;
    private String responseStatus;
}
