package org.demo.gmdemo.service;

import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.AuditAction;
import org.demo.gmdemo.dto.AuditLogEntry;
import org.demo.gmdemo.repo.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditLogger {

    private final AuditLogRepository auditLogRepo;

    public void log(AuditAction action, String orgId, String vehicleId, String subscriptionId,
                    String productId, String description, Map<String, Object> metadata) {
        auditLogRepo.save(AuditLogEntry.builder()
                .timestamp(Instant.now())
                .actor("system") // can be replaced by user/session later
                .action(action)
                .orgId(orgId)
                .vehicleId(vehicleId)
                .subscriptionId(subscriptionId)
                .productId(productId)
                .description(description)
                .metadata(metadata != null ? metadata : Map.of())
                .build());
    }

    public void logSimple(AuditAction action, String orgId, String subscriptionId, String description) {
        log(action, orgId, null, subscriptionId, null, description, null);
    }
}

