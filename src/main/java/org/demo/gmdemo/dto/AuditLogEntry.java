package org.demo.gmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "audit_logs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogEntry {
    @Id
    private String id;

    private Instant timestamp;
    private String actor; // "system", "scheduler", or userId if available
    private String orgId;
    private String vehicleId;
    private String subscriptionId;
    private String productId;

    private AuditAction action;
    private String description;
    private Map<String, Object> metadata; // optional structured info
}
