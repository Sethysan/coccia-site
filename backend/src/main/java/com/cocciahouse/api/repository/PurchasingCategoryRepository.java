
package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchasingCategoryRepository
        extends JpaRepository<PurchasingCategory, Long> {

    List<PurchasingCategory> findByDepartmentIdOrderByNameAsc(
            Long departmentId
    );
}
