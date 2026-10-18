
package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingVendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasingVendorRepository
        extends JpaRepository<PurchasingVendor, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
