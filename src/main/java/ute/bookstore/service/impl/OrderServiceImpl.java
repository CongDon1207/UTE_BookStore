package ute.bookstore.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ute.bookstore.repository.OrderRepository;
import ute.bookstore.entity.Order;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.exception.ResourceNotFoundException;
import ute.bookstore.service.INotificationService;
import ute.bookstore.service.IOrderService;


import java.time.LocalDateTime;

@Service
@Transactional
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final INotificationService notificationService;

    public OrderServiceImpl(OrderRepository orderRepository, INotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Page<Order> getOrdersByShopAndStatus(Long shopId, String status, Pageable pageable) {
        if ("ALL".equals(status)) {
            return orderRepository.findByShopId(shopId, pageable);
        }
        return orderRepository.findByShopIdAndStatus(shopId, OrderStatus.valueOf(status), pageable);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        
        // Validate status transition
        validateStatusTransition(order.getStatus(), status);
        
        // Update order
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        orderRepository.save(order);
        
        // Send notification to customer
        notificationService.sendOrderStatusNotification(order);
    }

    
     
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define allowed transitions
        switch (currentStatus) {
            case NEW:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from NEW to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.SHIPPING && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from CONFIRMED to " + newStatus);
                }
                break;
            case SHIPPING:
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from SHIPPING to " + newStatus);
                }
                break;
            case DELIVERED:
                if (newStatus != OrderStatus.REFUNDED) {
                    throw new IllegalStateException("Invalid status transition from DELIVERED to " + newStatus);
                }
                break;
            case CANCELLED:
            case REFUNDED:
                throw new IllegalStateException("Cannot change status of " + currentStatus + " order");
            default:
                throw new IllegalStateException("Unknown order status: " + currentStatus);
        }
    }
}