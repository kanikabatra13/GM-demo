package org.demo.gmdemo.service;

import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.OrgProductSubscription;
import org.demo.gmdemo.dto.ProductStatus;
import org.demo.gmdemo.dto.ProductType;
import org.demo.gmdemo.dto.VehicleProductAssignment;
import org.demo.gmdemo.repo.OrgProductSubscriptionRepository;
import org.demo.gmdemo.repo.VehicleProductAssignmentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoRenewalScheduler {

    private final OrgProductSubscriptionRepository subscriptionRepo;
    private final VehicleProductAssignmentRepository assignmentRepo;

    // Run every day at midnight (or use fixedRate = 86400000L)
    @Scheduled(cron = "0 0 0 * * *")
    public void renewExpiringSubscriptions() {
        Instant now = Instant.now();
        Instant threshold = now.plus(5, ChronoUnit.DAYS);

        List<OrgProductSubscription> expiring = subscriptionRepo.findByTypeAndStatus(ProductType.RENEWABLE, ProductStatus.ACTIVE)
                .stream()
                .filter(sub -> sub.getExpiresOn() != null && !sub.getExpiresOn().isAfter(threshold))
                .toList();

        for (OrgProductSubscription sub : expiring) {
            renew(sub);
        }

        System.out.printf("🔁 Auto-renewed %d subscriptions.%n", expiring.size());
    }

    private void renew(OrgProductSubscription sub) {
        Instant oldExpiry = sub.getExpiresOn();
        int termDays = 365; // or get from ProductDefinition

        Instant newExpiry = oldExpiry.plus(termDays, ChronoUnit.DAYS);
        sub.setExpiresOn(newExpiry);
        subscriptionRepo.save(sub);

        // Also update all vehicle assignments
        List<VehicleProductAssignment> assignments =
                assignmentRepo.findByOrgProductSubscriptionIdAndStatus(sub.getId(), ProductStatus.ACTIVE);

        for (VehicleProductAssignment a : assignments) {
            a.setExpiresOn(newExpiry);
        }

        assignmentRepo.saveAll(assignments);

        System.out.printf("✅ Renewed subscription %s to %s%n", sub.getId(), newExpiry);
    }
}

