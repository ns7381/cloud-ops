package com.cloud.ops.audit;

import com.cloud.ops.audit.modal.AuditTrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

/**
 * Created by Nathan on 2017/4/27.
 */
@Component
public class AuditTraceCleanTask {
    @Autowired
    private AuditTraceRepository repository;

    @Scheduled(cron="0 0 0 1,15 * ?")
    public void cleanAuditTrace() {
        for (AuditTrace auditTrace : repository.findAll()) {
            Calendar lastDate = Calendar.getInstance();
            lastDate.roll(Calendar.DATE, -14);
            if (auditTrace.getCreatedAt().before(lastDate.getTime())) {
                repository.delete(auditTrace);
            }
        }
    }
}
