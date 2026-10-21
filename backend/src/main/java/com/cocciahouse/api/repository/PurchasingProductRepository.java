
package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchasingProductRepository
        extends JpaRepository<PurchasingProduct, Long> {

    List<PurchasingProduct> findByDepartmentIdOrderByNameAsc(
            Long departmentId
    );

    @Query("""
            SELECT DISTINCT p
            FROM PurchasingProduct p
            JOIN PurchasingVendorProduct vp
                ON vp.product = p
            WHERE vp.vendor.id = :vendorId
            ORDER BY p.name ASC
            """)
    List<PurchasingProduct> findByVendorIdOrderByNameAsc(
            @Param("vendorId") Long vendorId
    );
}
