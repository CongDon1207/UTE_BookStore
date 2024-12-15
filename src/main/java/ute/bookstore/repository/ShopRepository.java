package ute.bookstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.enums.ApprovalStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
	Optional<Shop> findByUserEmail(String email);
    boolean existsByUserEmailAndId(String email, Long id);
    
    @Query("SELECT s FROM Shop s LEFT JOIN FETCH s.books LEFT JOIN FETCH s.orders WHERE s.id = :id")
    Optional<Shop> findByIdWithDetails(@Param("id") Long id);
    
    Shop findByUser(User user);
    
    
    //admin
    Page<Shop> findByNameContainingOrPhoneContainingOrUser_EmailContaining(
            String name, String phone, String email, Pageable pageable);
    
    Page<Shop> findByApprovalStatus(ApprovalStatus status, Pageable pageable);
    
    Long countByIsActiveTrue();
    Long countByApprovalStatus(ApprovalStatus status);
    
    List<Shop> findAllByOrderByIdDesc(Pageable pageable);

    Page<Shop> findByApprovalStatusAndNameContainingOrPhoneContainingOrUser_EmailContaining(
        ApprovalStatus status, 
        String name, 
        String phone, 
        String email, 
        Pageable pageable
    );
}
