
package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingVendorProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchasingVendorProductRepository
        extends JpaRepository<PurchasingVendorProduct, Long> {

    List<PurchasingVendorProduct> findByProductIdOrderByIdAsc(
            Long productId
    );

    List<PurchasingVendorProduct> findByVendorIdOrderByIdAsc(
            Long vendorId
    );
}
