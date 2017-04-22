package com.cloud.ops.audit.rest;

import com.cloud.ops.audit.AuditTraceRepository;
import com.cloud.ops.audit.LoggingTraceRepository;
import com.cloud.ops.audit.modal.AuditTrace;
import com.cloud.ops.security.modal.Role;
import com.cloud.ops.security.modal.User;
import com.cloud.ops.security.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/audits")
public class AuditTraceController {
    @Autowired
    private AuditTraceRepository repository;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<AuditTrace> findAll() {
        return repository.findAll();
    }

}
