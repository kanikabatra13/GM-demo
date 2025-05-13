package org.demo.gmdemo.model;

import lombok.Data;
import lombok.Generated;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class ProductAssignmentRequest {
    private String productId;
    private String organizationId;
    private List<String> vehicleIds;
}

