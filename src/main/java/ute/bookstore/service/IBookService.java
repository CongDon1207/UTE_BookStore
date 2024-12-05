package ute.bookstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category;
import java.io.IOException;
import java.util.List;


public interface IBookService {
	Book createBook(Book book, MultipartFile[] images) throws IOException;
	
    Page<Book> getShopBooks(Long shopId, String searchTerm, Category category, Boolean availability, int page, int size);
    
    Book getBookById(Long id);

	Book updateBook(Long id, Book book, MultipartFile[] images) throws IOException;

	void deleteBook(Long id);
	
	List<Book> getAllBooks();
	Page<Book> getAllBooks(Pageable pageable); // Thêm tham số Pageable
	List<Book> getFeaturedBooks();
	 Page<Book> getBooks(Long categoryId, String sortBy, String sortDir, int page, int size);
}
