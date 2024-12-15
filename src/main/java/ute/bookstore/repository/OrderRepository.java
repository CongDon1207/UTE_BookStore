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
	 // Thêm phân trang cho tìm kiếm đơn hàng theo trạng thái và người dùng
    Page<Order> findByStatusAndUserId(OrderStatus status, Long userId, Pageable pageable);

	Long countByShop(Shop shop);

	@Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.shop = :shop AND o.createdAt BETWEEN :startDate AND :endDate")
	Double sumTotalAmountByShopAndCreatedAtBetween(@Param("shop") Shop shop,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
	
	

	List<Order> findTop5ByShopOrderByCreatedAtDesc(Shop shop);
	

	@Query("SELECT EXTRACT(MONTH FROM o.createdAt) as month, SUM(o.totalAmount) as amount "
			+ "FROM Order o WHERE o.shop = :shop AND EXTRACT(YEAR FROM o.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE) "
			+ "GROUP BY EXTRACT(MONTH FROM o.createdAt) ORDER BY month")
	List<Object[]> findMonthlyRevenueByShop(@Param("shop") Shop shop);
	
	

	@Query("SELECT b.title as bookName, COUNT(oi.book) as soldQuantity " + "FROM OrderItem oi JOIN oi.book b "
			+ "WHERE oi.order.shop = :shop " + "GROUP BY b.title ORDER BY soldQuantity DESC LIMIT 10")
	List<Object[]> findTopSellingBooks(@Param("shop") Shop shop);
	

	@Query("SELECT c.name as category, COUNT(b) as bookCount " + "FROM Book b JOIN b.category c "
			+ "WHERE :shop MEMBER OF b.shops " + "GROUP BY c.name")
	List<Object[]> findBookDistributionByCategory(@Param("shop") Shop shop);
	
	
	
	
	
	
	
	
	
	// admin
	// Tìm kiếm theo tên người dùng
    Page<Order> findByUserFullNameContaining(String fullName, Pageable pageable);
    
    // Tìm kiếm theo trạng thái
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    // Kết hợp các điều kiện tìm kiếm
    Page<Order> findByUserFullNameContainingAndStatus(String fullName, OrderStatus status, Pageable pageable);
    
    Page<Order> findByUserFullNameContainingAndShopId(String fullName, Long shopId, Pageable pageable);
    
    Page<Order> findByStatusAndShopId(OrderStatus status, Long shopId, Pageable pageable);
    
    Page<Order> findByUserFullNameContainingAndStatusAndShopId(String fullName, OrderStatus status, Long shopId, Pageable pageable);
    
    // Thống kê đơn hàng theo trạng thái
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();
    
    // Thống kê đơn hàng theo shop
    @Query("SELECT o.shop.id, o.shop.name, COUNT(o) FROM Order o GROUP BY o.shop.id, o.shop.name")
    List<Object[]> countByShop();
    
    // Tìm đơn hàng trong khoảng thời gian
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Page<Order> findOrdersBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    // Tổng doanh thu theo shop
    @Query("SELECT o.shop.id, o.shop.name, SUM(o.totalAmount) FROM Order o " +
           "WHERE o.status = 'DELIVERED' GROUP BY o.shop.id, o.shop.name")
    List<Object[]> calculateRevenueByShop();
    
    // Tổng doanh thu trong khoảng thời gian
    @Query("SELECT SUM(o.totalAmount) FROM Order o " +
           "WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    Double calculateRevenueBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Đếm số đơn hàng theo trạng thái cho một shop cụ thể
    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.shop.id = :shopId GROUP BY o.status")
    List<Object[]> countByStatusForShop(@Param("shopId") Long shopId);
    
    // Tìm các đơn hàng mới nhất
    List<Order> findTop10ByOrderByCreatedAtDesc();
    
    
    Long countByStatus(OrderStatus status);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o " +
            "WHERE o.shop.id = :shopId AND o.status = :status " +
            "AND (:from IS NULL OR o.createdAt >= :from) " +
            "AND o.createdAt <= :to")
     Double calculateRevenue(Long shopId, OrderStatus status, 
                           LocalDateTime from, LocalDateTime to);

     @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE o.shop.id = :shopId " +
            "AND (:from IS NULL OR o.createdAt >= :from) " +
            "AND o.createdAt <= :to")
     Long countOrders(Long shopId, LocalDateTime from, LocalDateTime to);

     @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE o.shop.id = :shopId AND o.status = :status " +  
            "AND (:from IS NULL OR o.createdAt >= :from) " +
            "AND o.createdAt <= :to")
     Long countOrdersByStatus(Long shopId, OrderStatus status,
                            LocalDateTime from, LocalDateTime to);
     
     
}
