
package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingInventoryBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PurchasingInventoryBalanceRepository
        extends JpaRepository<PurchasingInventoryBalance, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT b
        FROM PurchasingInventoryBalance b
        WHERE b.productId = :productId
        """)
    Optional<PurchasingInventoryBalance> lockByProductId(
            @Param("productId") Long productId
    );
}
