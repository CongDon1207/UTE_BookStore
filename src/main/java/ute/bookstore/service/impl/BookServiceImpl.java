package ute.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    
    public Page<Book> getShopBooks(
    		   Long shopId,
    		   String searchTerm, 
    		   Category category,
    		   Boolean availability,
    		   int page,
    		   int size
    		) {
    		   Shop shop = shopService.getShopById(shopId);
    		   List<Shop> shops = Collections.singletonList(shop);
    		   return bookRepository.findByShopsInAndTitleContainingAndCategoryAndIsAvailable(
    		       shops,
    		       searchTerm,
    		       category, 
    		       availability,
    		       PageRequest.of(page, size)
    		   );
    		}
    
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
}
