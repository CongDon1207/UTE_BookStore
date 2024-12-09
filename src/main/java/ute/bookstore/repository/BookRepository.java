package ute.bookstore.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category;
import ute.bookstore.entity.Shop;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Page<Book> findByShopsInAndTitleContainingAndCategoryAndIsAvailable(
	        List<Shop> shops, String searchTerm, Category category, Boolean availability, Pageable pageable
	    );
	Page<Book> findAll(Pageable pageable); // Thêm hỗ trợ phân trang
	List<Book> findByShopsIn(Collection<Shop> shops);
	List<Book> findTop6ByOrderByIdDesc();
	
	Page<Book> findByCategoryId(Long categoryId, Pageable pageable);
	
	List<Book> findTop20ByOrderByIdDesc();
	

	
}
