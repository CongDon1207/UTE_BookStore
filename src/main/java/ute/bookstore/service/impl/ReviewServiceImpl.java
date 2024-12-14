package ute.bookstore.service.impl;



import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import ute.bookstore.entity.Book;

import ute.bookstore.entity.Review;
import ute.bookstore.entity.User;



import ute.bookstore.repository.ReviewRepository;

import ute.bookstore.service.IReviewService;

@Service
public class ReviewServiceImpl implements IReviewService {
	 @Autowired
	    private ReviewRepository reviewRepository;

	    /**
	     * Lưu một đánh giá mới.
	     */
	    public Review saveReview(User user, Book book, Integer rating, String comment) {
	        // Kiểm tra xem người dùng đã đánh giá sản phẩm này chưa
	        if (reviewRepository.existsByUserAndBook(user, book)) {
	            throw new IllegalStateException("Bạn đã đánh giá sách này rồi!");
	        }

	        // Tạo đánh giá mới
	        Review review = new Review();
	        review.setUser(user);
	        review.setBook(book);
	        review.setRating(rating);
	        review.setComment(comment);
	        review.setCreatedAt(LocalDateTime.now());  // Thiết lập thời gian hiện tại

	        return reviewRepository.save(review);
	    }

	    /**
	     * Lấy danh sách đánh giá cho một sản phẩm.
	     */
	    public List<Review> getReviewsByBook(Book book) {
	        return reviewRepository.findByBook(book);
	    }

	



}
