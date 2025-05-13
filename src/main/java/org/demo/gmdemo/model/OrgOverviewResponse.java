package org.demo.gmdemo.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrgOverviewResponse {
    private String organizationId;
    private String name;
    private List<OrgSubscriptionSummary> subscriptions;
}

