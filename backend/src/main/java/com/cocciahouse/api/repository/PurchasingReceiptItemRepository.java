
package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PurchasingReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PurchasingReceiptItemRepository
        extends JpaRepository<PurchasingReceiptItem, Long> {

    List<PurchasingReceiptItem> findByReceiptId(Long receiptId);

    @Query("""
        SELECT COALESCE(SUM(item.receivedQuantity), 0)
        FROM PurchasingReceiptItem item
        WHERE item.orderItem.id = :orderItemId
        """)
    BigDecimal sumReceivedQuantityByOrderItemId(
            @Param("orderItemId") Long orderItemId
    );
}
