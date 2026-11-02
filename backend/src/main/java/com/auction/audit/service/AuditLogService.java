package com.auction.audit.service;

import com.auction.audit.entity.AuditLog;
import com.auction.audit.facade.AuditLogFacade;
import com.auction.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService implements AuditLogFacade {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, Long userId, String entityName, Long entityId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setUserId(userId);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }
}

