package ute.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category;
import ute.bookstore.entity.Shop;
import ute.bookstore.exception.ResourceNotFoundException;
import ute.bookstore.repository.BookRepository;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.ICloudinaryService;
import ute.bookstore.service.IShopService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements IBookService{
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private IShopService shopService;
    
    @Autowired
    private ICloudinaryService cloudinaryService;
    
    @Override
    public Book createBook(Book book, MultipartFile[] images) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = cloudinaryService.uploadImage(image);
            imageUrls.add(imageUrl);
        }
        book.setImages(imageUrls);
        book.setIsAvailable(book.getQuantity() > 0);
        return bookRepository.save(book);
    }
    
    @Override
    public Page<Book> getShopBooks(Long shopId, String searchTerm, Category category, Boolean availability, int page, int size) {
        Shop shop = shopService.getShopById(shopId);
        return bookRepository.findByShopsInAndTitleContainingAndCategoryAndIsAvailable(
            List.of(shop), searchTerm, category, availability, PageRequest.of(page, size)
        );
    }
    
    public Book updateBook(Long id, Book updatedBook, MultipartFile[] newImages) throws IOException {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
            
        book.setTitle(updatedBook.getTitle());
        book.setDescription(updatedBook.getDescription());
        book.setPrice(updatedBook.getPrice());
        book.setQuantity(updatedBook.getQuantity());
        book.setCategory(updatedBook.getCategory());
        book.setIsAvailable(updatedBook.getQuantity() > 0);
        
        if (newImages != null && newImages.length > 0) {
            for (String oldImageUrl : book.getImages()) {
                cloudinaryService.deleteImage(oldImageUrl);
            }
            
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : newImages) {
                String imageUrl = cloudinaryService.uploadImage(image);
                imageUrls.add(imageUrl);
            }
            book.setImages(imageUrls);
        }
        
        return bookRepository.save(book);
    }
    
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
            
        for (String imageUrl : book.getImages()) {
            cloudinaryService.deleteImage(imageUrl);
        }
        
        bookRepository.delete(book);
    }
    
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }
    
    public List<Book> getFeaturedBooks() {
        // Giả sử chúng ta chọn sách nổi bật bằng cách lọc dựa trên một tiêu chí cụ thể
        return bookRepository.findTop6ByOrderByIdDesc(); // Ví dụ: Lấy 10 sách mới nhất
    }
    
    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable); // Gọi repository để lấy dữ liệu phân trang
    }
    @Override
    public Page<Book> getBooks(Long categoryId, String sortBy, String sortDir, int page, int size) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        if (categoryId != null) {
            return bookRepository.findByCategoryId(categoryId, pageable);
        } else {
            return bookRepository.findAll(pageable);
        }
    }


    
    
}
