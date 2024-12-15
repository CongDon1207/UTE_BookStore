package ute.bookstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category;
import ute.bookstore.entity.Shop;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface IBookService {
	// IBookService
	Book createBook(Book book, MultipartFile[] images, Shop shop) throws IOException;
    
    Book getBookById(Long id);

	Book updateBook(Long id, Book updatedBook, MultipartFile[] newImages, Shop shop) throws IOException;

	void deleteBook(Long id);
	Page<Book> getBooksByShop(Shop shop, int page, int size);
	Page<Book> searchBooks(Shop shop, String title, Category category, Boolean isAvailable, Pageable pageable);
	
	List<Book> getAllBooks();
	Page<Book> getAllBooks(Pageable pageable); // Thêm tham số Pageable
	List<Book> getFeaturedBooks();
	Page<Book> getBooks(Long categoryId, String sortBy, String sortDir, int page, int size);
	 
	List<Book> getTop20NewBooks();
	
	List<Map<String, Object>> getTop10BestSellingBooks();

	List<Map<String, Object>> getTop20BestSellingBooksWithImages();
	List<Map<String, Object>> getTop20BooksWithRatings();
	 List<Map<String, Object>> getTop20MostFavoritedBooks() ;
	 
	 void updateBook(Book book);
	 boolean checkAvailableQuantity(Book book, int requestedQuantity);


}
