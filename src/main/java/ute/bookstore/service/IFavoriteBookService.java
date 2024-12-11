package ute.bookstore.service;

public interface IFavoriteBookService {
	
	boolean addBookToFavorites(Long bookId);
	boolean removeBookFromFavorites(Long bookId) ;

}
