package org.demo.gmdemo.repo;

import org.demo.gmdemo.dto.OrgProductSubscription;
import org.demo.gmdemo.dto.ProductStatus;
import org.demo.gmdemo.dto.ProductType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrgProductSubscriptionRepository extends MongoRepository<OrgProductSubscription, String> {
    List<OrgProductSubscription> findByOrganizationIdAndStatus(String organizationId, ProductStatus status);
    Optional<OrgProductSubscription> findByOrganizationIdAndProductIdAndStatus(String orgId, String productId, ProductStatus status);

    List<OrgProductSubscription> findByTypeAndStatus(ProductType type, ProductStatus status);

    List<OrgProductSubscription> findByOrganizationId(String organizationId);


}
