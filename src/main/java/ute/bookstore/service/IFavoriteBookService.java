package ute.bookstore.service;

import ute.bookstore.entity.User;

public interface IFavoriteBookService {
	
	boolean addBookToFavorites(Long bookId, User user);
	boolean removeBookFromFavorites(Long bookId, User user) ;

}
