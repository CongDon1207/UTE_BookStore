package ute.bookstore.repository;

import java.util.Collection;
import java.util.List;


import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category;

import ute.bookstore.entity.Shop;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
	
	
	@Query("SELECT DISTINCT b FROM Book b JOIN b.shops s " +
		       "WHERE s IN :shops " +
		       "AND (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%',:title,'%'))) " +
		       "AND (:category IS NULL OR b.category = :category) " + 
		       "AND (:isAvailable IS NULL OR b.isAvailable = :isAvailable)")
		Page<Book> searchBooks(
		    @Param("shops") Collection<Shop> shops,
		    @Param("title") String title,
		    @Param("category") Category category,
		    @Param("isAvailable") Boolean isAvailable,
		    Pageable pageable
		);
	
	Page<Book> findAll(Pageable pageable); // Thêm hỗ trợ phân trang
	List<Book> findByShopsIn(Collection<Shop> shops);
	List<Book> findTop6ByOrderByIdDesc();
	
	Page<Book> findByCategoryId(Long categoryId, Pageable pageable);
	
	List<Book> findTop20ByOrderByIdDesc();
	
	
	
	
	
}
