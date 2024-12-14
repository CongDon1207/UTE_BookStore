package ute.bookstore.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ute.bookstore.entity.*;
import ute.bookstore.repository.*;
import ute.bookstore.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminOrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ShopRepository shopRepository;
    
    public Page<Order> getOrders(String search, OrderStatus status, Long shopId, int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        
        if (search != null && !search.isEmpty()) {
            if (status != null && shopId != null) {
                return orderRepository.findByUserFullNameContainingAndStatusAndShopId(
                    search, status, shopId, pageRequest);
            } else if (status != null) {
                return orderRepository.findByUserFullNameContainingAndStatus(
                    search, status, pageRequest);
            } else if (shopId != null) {
                return orderRepository.findByUserFullNameContainingAndShopId(
                    search, shopId, pageRequest);
            } else {
                return orderRepository.findByUserFullNameContaining(search, pageRequest);
            }
        } else {
            if (status != null && shopId != null) {
                return orderRepository.findByStatusAndShopId(status, shopId, pageRequest);
            } else if (status != null) {
                return orderRepository.findByStatus(status, pageRequest);
            } else if (shopId != null) {
                return orderRepository.findByShopId(shopId, pageRequest);
            } else {
                return orderRepository.findAll(pageRequest);
            }
        }
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }
    
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    public Long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
}
