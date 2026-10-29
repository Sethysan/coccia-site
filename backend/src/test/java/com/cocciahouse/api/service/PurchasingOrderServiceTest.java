
package com.cocciahouse.api.service;

import com.cocciahouse.api.model.PurchasingOrder;
import com.cocciahouse.api.model.PurchasingOrderItem;
import com.cocciahouse.api.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchasingOrderServiceTest {

    @Mock
    private PurchasingOrderRepository orderRepository;

    @Mock
    private PurchasingOrderItemRepository itemRepository;

    @Mock
    private PurchasingVendorRepository vendorRepository;

    @Mock
    private PurchasingVendorProductRepository vendorProductRepository;

    @Mock
    private PurchasingDepartmentRepository departmentRepository;

    @InjectMocks
    private PurchasingOrderService service;

    @Test
    void deleteDraftRemovesOrderAndItems() {
        Long orderId = 1L;

        PurchasingOrder order = new PurchasingOrder();
        order.setStatus("DRAFT");

        PurchasingOrderItem item = new PurchasingOrderItem();

        when(orderRepository.lockById(orderId))
                .thenReturn(Optional.of(order));

        when(itemRepository.findByOrderId(orderId))
                .thenReturn(List.of(item));

        service.deleteDraft(orderId);

        var inOrder = inOrder(itemRepository, orderRepository);

        inOrder.verify(itemRepository).deleteAll(List.of(item));
        inOrder.verify(itemRepository).flush();
        inOrder.verify(orderRepository).delete(order);
    }

    @Test
    void deleteDraftRejectsConfirmedOrder() {
        Long orderId = 2L;

        PurchasingOrder order = new PurchasingOrder();
        order.setStatus("CONFIRMED");

        when(orderRepository.lockById(orderId))
                .thenReturn(Optional.of(order));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.deleteDraft(orderId)
        );

        assertEquals(HttpStatus.CONFLICT,
                exception.getStatusCode());

        verifyNoInteractions(itemRepository);
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void deleteDraftReturnsNotFoundForMissingOrder() {
        Long orderId = 999L;

        when(orderRepository.lockById(orderId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.deleteDraft(orderId)
        );

        assertEquals(HttpStatus.NOT_FOUND,
                exception.getStatusCode());

        verifyNoInteractions(itemRepository);
        verify(orderRepository, never()).delete(any());
    }
}
