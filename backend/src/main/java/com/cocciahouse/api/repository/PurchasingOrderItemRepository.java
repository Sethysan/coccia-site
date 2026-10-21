package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchasingOrderItemRepository
        extends JpaRepository<PurchasingOrderItem, Long> {

    List<PurchasingOrderItem> findByOrderId(Long orderId);
}