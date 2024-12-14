package ute.bookstore.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ute.bookstore.entity.Address;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Order;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.enums.PaymentMethod;

public interface IOrderService {
	Page<Order> getOrdersByShopAndStatus(Long shopId, String status, Pageable pageable);
	
	Order getOrderById(Long id);
	
	 void updateOrderStatus(Long id, OrderStatus status);
	 
	 Order createOrder(User userId, Shop shopId, Address addressId, List<Book> books, List<Integer> quantities, PaymentMethod paymentMethod, Double totalAmount);
	 Page<Order> getOrdersByStatus(OrderStatus status, Long userId, Pageable pageable);
}
