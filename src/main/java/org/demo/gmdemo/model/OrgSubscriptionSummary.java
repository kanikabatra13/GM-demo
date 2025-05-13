package org.demo.gmdemo.model;

import lombok.Builder;
import lombok.Data;
import org.demo.gmdemo.dto.ProductStatus;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class OrgSubscriptionSummary {
    private String productId;
    private String productName;
    private ProductStatus status;
    private Map<ProductStatus, List<String>> vehicleAssignments;
}

