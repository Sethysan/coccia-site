package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface PurchasingOrderRepository
        extends JpaRepository<PurchasingOrder, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM PurchasingOrder o WHERE o.id = :orderId")
    Optional<PurchasingOrder> lockById(@Param("orderId") Long orderId);

}