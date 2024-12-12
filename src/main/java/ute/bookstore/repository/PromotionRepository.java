package ute.bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ute.bookstore.entity.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
   
}