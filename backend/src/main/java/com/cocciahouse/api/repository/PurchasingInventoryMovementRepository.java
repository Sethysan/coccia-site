
package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingInventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchasingInventoryMovementRepository
        extends JpaRepository<PurchasingInventoryMovement, Long> {

    // Retrieve a product's inventory history, newest first.
    List<PurchasingInventoryMovement>
    findByProductIdOrderByCreatedAtDescIdDesc(Long productId);

    // Check whether a receipt item has already updated inventory.
    boolean existsByReceiptItemId(Long receiptItemId);
}
