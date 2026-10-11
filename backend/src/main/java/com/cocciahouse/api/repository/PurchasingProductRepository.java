
package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchasingProductRepository
        extends JpaRepository<PurchasingProduct, Long> {

    List<PurchasingProduct> findByDepartmentIdOrderByNameAsc(
            Long departmentId
    );

    List<PurchasingProduct> findByVendorIdOrderByNameAsc(
            Long vendorId
    );
}
