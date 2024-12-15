package ute.bookstore.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ute.bookstore.entity.*;
import ute.bookstore.repository.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBookManagementService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;

    // Quản lý danh mục sách
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setLevel(categoryDetails.getLevel());
        category.setParent(categoryDetails.getParent());
        
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Quản lý sản phẩm
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Transactional
    public Book updateBookStatus(Long id, Boolean isAvailable) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        book.setIsAvailable(isAvailable);
        return bookRepository.save(book);
    }

    public Page<Book> getBooksByCategory(Long categoryId, Pageable pageable) {
        return bookRepository.findByCategoryId(categoryId, pageable);
    }

    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        book.setTitle(bookDetails.getTitle());
        book.setDescription(bookDetails.getDescription());
        book.setPrice(bookDetails.getPrice());
        book.setQuantity(bookDetails.getQuantity());
        book.setCategory(bookDetails.getCategory());
        book.setImages(bookDetails.getImages());
        
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    
    public Page<Book> getFilteredBooks(Long categoryId, Boolean isAvailable, Double minPrice, Double maxPrice, Pageable pageable) {
        // Trường hợp có đầy đủ các tiêu chí lọc
        if (categoryId != null && isAvailable != null && minPrice != null && maxPrice != null) {
            return bookRepository.findByCategoryIdAndIsAvailableAndPriceBetween(
                categoryId, isAvailable, minPrice, maxPrice, pageable);
        }
        
        // Trường hợp có category và trạng thái
        if (categoryId != null && isAvailable != null) {
            return bookRepository.findByCategoryIdAndIsAvailable(categoryId, isAvailable, pageable);
        }
        
        // Trường hợp có category và khoảng giá
        if (categoryId != null && minPrice != null && maxPrice != null) {
            return bookRepository.findByCategoryIdAndPriceBetween(categoryId, minPrice, maxPrice, pageable);
        }
        
        // Trường hợp có trạng thái và khoảng giá
        if (isAvailable != null && minPrice != null && maxPrice != null) {
            return bookRepository.findByIsAvailableAndPriceBetween(isAvailable, minPrice, maxPrice, pageable);
        }
        
        // Trường hợp chỉ có category
        if (categoryId != null) {
            return bookRepository.findByCategoryId(categoryId, pageable);
        }
        
        // Trường hợp chỉ có trạng thái
        if (isAvailable != null) {
            return bookRepository.findByIsAvailable(isAvailable, pageable);
        }
        
        // Trường hợp chỉ có khoảng giá
        if (minPrice != null && maxPrice != null) {
            return bookRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        }
        
        // Trường hợp không có tiêu chí lọc nào
        return getAllBooks(pageable);
    }
}
