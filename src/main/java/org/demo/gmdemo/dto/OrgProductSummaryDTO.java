package org.demo.gmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgProductSummaryDTO {
    private String productId;
    private String productName;
    private long vehicleCount;
}
