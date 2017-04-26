package com.cloud.ops.audit.rest;

import com.cloud.ops.audit.AuditTraceRepository;
import com.cloud.ops.audit.modal.AuditTrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.List;

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

    @GetMapping(value = "/page/{pageNo}/{pageSize}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<AuditTrace> findPage(@QuerydslPredicate(root = AuditTrace.class) Predicate predicate,
                                     Pageable pageable, @RequestParam MultiValueMap<String, String> parameters/*,
                                     @PathVariable int pageNo, @PathVariable int pageSize*/) {

        return repository.findAll(predicate, pageable);
    }

}
