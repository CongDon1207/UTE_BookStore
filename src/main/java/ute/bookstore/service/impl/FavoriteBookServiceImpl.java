package ute.bookstore.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.FavoriteBook;
import ute.bookstore.entity.User;
import ute.bookstore.repository.BookRepository;
import ute.bookstore.repository.FavoriteBookRepository;
import ute.bookstore.repository.UserRepository;

import ute.bookstore.service.IFavoriteBookService;

@Service
public class FavoriteBookServiceImpl implements IFavoriteBookService  {
	
	@Autowired
    private FavoriteBookRepository favoriteBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public boolean addBookToFavorites(Long bookId, User user) {
       

        // Kiểm tra sách
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isEmpty()) {
            return false;
        }

        Book book = bookOptional.get();

        // Kiểm tra sách đã tồn tại trong danh sách yêu thích chưa
        if (favoriteBookRepository.existsByUserAndBook(user, book)) {
            return false; // Sách đã tồn tại
        }

        // Thêm sách vào danh sách yêu thích
        FavoriteBook favoriteBook = new FavoriteBook();
        favoriteBook.setUser(user);
        favoriteBook.setBook(book);
        favoriteBookRepository.save(favoriteBook);

        return true;
    }

    public boolean removeBookFromFavorites(Long bookId,User user) {
    	 FavoriteBook favoriteBook = favoriteBookRepository.findById(bookId).orElse(null);
    	    if (favoriteBook == null) {
    	        return false; // Không tìm thấy sách
    	    }
    	    
    	    favoriteBookRepository.delete(favoriteBook); // Xóa sách
    	    return true;
    }

    
	
}
