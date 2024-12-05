package ute.bookstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ute.bookstore.entity.Order;
import ute.bookstore.enums.OrderStatus;

public interface IOrderService {
	Page<Order> getOrdersByShopAndStatus(Long shopId, String status, Pageable pageable);
	
	Order getOrderById(Long id);
	
	 void updateOrderStatus(Long id, OrderStatus status);
}
