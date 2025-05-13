package org.demo.gmdemo.repo;

import org.demo.gmdemo.dto.AuditLogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLogEntry, String> {

    List<AuditLogEntry> findByOrgId(String orgId);

    List<AuditLogEntry> findBySubscriptionId(String subscriptionId);

    List<AuditLogEntry> findByVehicleId(String vehicleId);

    List<AuditLogEntry> findByOrgIdAndAction(String orgId, org.demo.gmdemo.dto.AuditAction action);
}

