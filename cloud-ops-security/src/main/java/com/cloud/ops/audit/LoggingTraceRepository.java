package com.cloud.ops.audit;

import com.cloud.ops.audit.modal.AuditTrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.Trace;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 2017/4/22.
 */
@Component
public class LoggingTraceRepository implements TraceRepository {

    @Autowired
    private AuditTraceRepository repository;
    @Override
    public List<Trace> findAll() {
        return null;
    }

    @Override
    public void add(Map<String, Object> map) {
        AuditTrace auditTrace = new AuditTrace();
        auditTrace.setMethod((String) map.get("method"));
        if ("GET".equals(auditTrace.getMethod())) {
            return;
        }
        auditTrace.setPath((String) map.get("path"));
        Map headers = (Map) map.get("headers");
        Map request = (Map) headers.get("request");
        Map response = (Map) headers.get("response");
        auditTrace.setHost((String) request.get("host"));
        auditTrace.setResponseStatus((String) response.get("status"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            auditTrace.setUsername(((User) auth.getPrincipal()).getUsername());
        }
        repository.save(auditTrace);
    }
}
