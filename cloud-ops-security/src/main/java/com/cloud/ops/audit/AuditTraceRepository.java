package com.cloud.ops.audit;

import com.cloud.ops.audit.modal.AuditTrace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

/**
 * Created by Nathan on 2017/4/22.
 */
public interface AuditTraceRepository extends JpaRepository<AuditTrace, Serializable>, JpaSpecificationExecutor<AuditTrace> {
}
