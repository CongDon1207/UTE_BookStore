package ute.bookstore.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ute.bookstore.entity.Promotion;
import ute.bookstore.entity.Shop;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
	@Query("SELECT p FROM Promotion p WHERE p.shop = :shop AND p.code IS NOT NULL ORDER BY p.startDate DESC")
	List<Promotion> findShopVouchers(@Param("shop") Shop shop);

	// Method cho discount
	@Query("SELECT p FROM Promotion p WHERE p.shop = :shop AND p.code IS NULL ORDER BY p.startDate DESC")
	List<Promotion> findShopDiscounts(@Param("shop") Shop shop);

	// admin
	boolean existsByCode(String code);

	@Query("SELECT p FROM Promotion p WHERE " + "(:code IS NULL OR LOWER(p.code) LIKE LOWER(CONCAT('%', :code, '%'))) "
			+ "ORDER BY p.startDate DESC")
	Page<Promotion> findAllByFilters(@Param("code") String code, Pageable pageable);

	@Query("SELECT p FROM Promotion p WHERE " + "p.startDate <= :now AND p.endDate >= :now AND p.isActive = true")
	List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);

	@Query("SELECT p FROM Promotion p WHERE p.endDate < :now")
	List<Promotion> findExpiredPromotions(@Param("now") LocalDateTime now);

	@Query("SELECT p FROM Promotion p WHERE " + "p.startDate > :now AND p.isActive = true")
	List<Promotion> findUpcomingPromotions(@Param("now") LocalDateTime now);

	@Query("SELECT p FROM Promotion p WHERE " + "p.isActive = true AND "
			+ "p.startDate <= :now AND p.endDate >= :now AND "
			+ "(:minOrderAmount IS NULL OR p.minOrderAmount <= :minOrderAmount)")
	List<Promotion> findApplicablePromotions(@Param("now") LocalDateTime now,
			@Param("minOrderAmount") Double minOrderAmount);
}