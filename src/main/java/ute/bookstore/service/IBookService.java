package ute.bookstore.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category;
import java.io.IOException;


public interface IBookService {
	Page<Book> getShopBooks(Long shopId, String searchTerm, Category category, Boolean availability, int page,
			int size);

	Book createBook(Book book, MultipartFile[] images) throws IOException;

	Book updateBook(Long id, Book book, MultipartFile[] images) throws IOException;

	void deleteBook(Long id);
}
