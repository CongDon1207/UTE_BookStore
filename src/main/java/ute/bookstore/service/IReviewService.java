package ute.bookstore.service;


import ute.bookstore.entity.Book;
import ute.bookstore.entity.Review;
import ute.bookstore.entity.User;


public interface IReviewService {
	
	Review saveReview(User user, Book book, Integer rating, String comment);
}
