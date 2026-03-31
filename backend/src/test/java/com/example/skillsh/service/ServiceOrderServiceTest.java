package com.example.skillsh.service;

import com.example.skillsh.domain.entity.ServiceOrder;
import com.example.skillsh.repository.ServiceOrderRepository;
import com.example.skillsh.services.payment.ServiceOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderServiceTest {

    @Mock
    private ServiceOrderRepository orderRepository;

    @InjectMocks
    private ServiceOrderService serviceOrderService;

    // --- ТЕСТОВЕ ЗА updateOrderStatus(Long orderId, String newStatus) ---

    @Test
    void testUpdateOrderStatusById_Success() {
        // Arrange (Подготовка)
        Long orderId = 1L;
        String newStatus = "PAID";

        ServiceOrder existingOrder = new ServiceOrder();
        existingOrder.setId(orderId);
        existingOrder.setStatus("PENDING"); // Старият статус е различен

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        // Act (Изпълнение)
        serviceOrderService.updateOrderStatus(orderId, newStatus);

        // Assert (Проверка)
        assertEquals("PAID", existingOrder.getStatus());
        verify(orderRepository, times(1)).save(existingOrder);
    }

    @Test
    void testUpdateOrderStatusById_WhenStatusIsSame_ShouldSkipUpdate() {
        // Arrange
        Long orderId = 2L;
        String currentStatus = "PAID";

        ServiceOrder existingOrder = new ServiceOrder();
        existingOrder.setId(orderId);
        existingOrder.setStatus(currentStatus); // Статусът вече е PAID

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        // Act
        serviceOrderService.updateOrderStatus(orderId, currentStatus);

        // Assert
        // Уверяваме се, че статусът не е променен и методът save() НЕ е извикан,
        // защото статусът вече е същият (пестим излишни заявки към базата).
        assertEquals("PAID", existingOrder.getStatus());
        verify(orderRepository, never()).save(any(ServiceOrder.class));
    }

    @Test
    void testUpdateOrderStatusById_ThrowsExceptionWhenNotFound() {
        // Arrange
        Long orderId = 99L;

        // Указваме, че поръчка с такова ID няма в базата
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceOrderService.updateOrderStatus(orderId, "PAID");
        });

        assertEquals("ServiceOrder not found for ID: 99", exception.getMessage());
        verify(orderRepository, never()).save(any(ServiceOrder.class));
    }
    // --- ТЕСТОВЕ ЗА updateOrderStatus(String paymentIntentId, String newStatus) ---

    @Test
    void testUpdateOrderStatusByPaymentIntentId_Success() {
        // Arrange
        String paymentIntentId = "pi_12345ABCD";
        String newStatus = "PAID";

        ServiceOrder existingOrder = new ServiceOrder();
        existingOrder.setStatus("PENDING");

        when(orderRepository.findByStripePaymentIntentId(paymentIntentId)).thenReturn(Optional.of(existingOrder));

        // Act
        serviceOrderService.updateOrderStatus(paymentIntentId, newStatus);

        // Assert
        assertEquals("PAID", existingOrder.getStatus());
        verify(orderRepository, times(1)).save(existingOrder);
    }

    @Test
    void testUpdateOrderStatusByPaymentIntentId_ThrowsExceptionWhenNotFound() {
        // Arrange
        String paymentIntentId = "pi_invalid";
        when(orderRepository.findByStripePaymentIntentId(paymentIntentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceOrderService.updateOrderStatus(paymentIntentId, "PAID");
        });

        assertEquals("ServiceOrder not found for Payment Intent ID: pi_invalid", exception.getMessage());
        verify(orderRepository, never()).save(any(ServiceOrder.class));
    }
}


