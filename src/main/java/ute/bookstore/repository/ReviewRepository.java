package ute.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ute.bookstore.entity.Book;
import ute.bookstore.entity.Review;
import ute.bookstore.entity.User;



public interface ReviewRepository extends JpaRepository<Review, Long> {
	 // Kiểm tra nếu người dùng đã đánh giá sách này chưa
    boolean existsByUserAndBook(User user, Book book);
    
 // Lấy tất cả đánh giá cho một sách cụ thể
    List<Review> findByBook(Book book);

}
