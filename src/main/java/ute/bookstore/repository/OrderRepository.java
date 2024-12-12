package ute.bookstore.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ute.bookstore.entity.Order;
import ute.bookstore.entity.Shop;
import ute.bookstore.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByShopId(Long shopId, Pageable pageable);
    
    Page<Order> findByShopIdAndStatus(Long shopId, OrderStatus status, Pageable pageable);
    
    Long countByShop(Shop shop);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.shop = :shop AND o.createdAt BETWEEN :startDate AND :endDate")
    Double sumTotalAmountByShopAndCreatedAtBetween(
        @Param("shop") Shop shop,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    
    
    List<Order> findTop5ByShopOrderByCreatedAtDesc(Shop shop);

    @Query("SELECT EXTRACT(MONTH FROM o.createdAt) as month, SUM(o.totalAmount) as amount " +
    	       "FROM Order o WHERE o.shop = :shop AND EXTRACT(YEAR FROM o.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE) " +
    	       "GROUP BY EXTRACT(MONTH FROM o.createdAt) ORDER BY month")
    	List<Object[]> findMonthlyRevenueByShop(@Param("shop") Shop shop);
}
