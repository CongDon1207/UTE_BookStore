package ute.bookstore.repository;
import java.util.List;

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
}