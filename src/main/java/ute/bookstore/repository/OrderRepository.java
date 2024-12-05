package ute.bookstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ute.bookstore.entity.Order;
import ute.bookstore.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByShopId(Long shopId, Pageable pageable);
    
    Page<Order> findByShopIdAndStatus(Long shopId, OrderStatus status, Pageable pageable);
}