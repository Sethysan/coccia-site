
package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchasingReceiptRepository
        extends JpaRepository<PurchasingReceipt, Long> {

    List<PurchasingReceipt> findByOrderIdOrderByReceivedAtDesc(Long orderId);
}
