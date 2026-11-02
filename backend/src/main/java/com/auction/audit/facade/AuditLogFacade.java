package com.auction.audit.facade;

public interface AuditLogFacade {
    void log(String action, Long userId, String entityName, Long entityId, String details);
}

