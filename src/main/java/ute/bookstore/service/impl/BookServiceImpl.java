package ute.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BookServiceImpl implements IBookService{
	   private final JdbcTemplate jdbcTemplate;

	    @Autowired
	    public BookServiceImpl(JdbcTemplate jdbcTemplate) {
	        this.jdbcTemplate = jdbcTemplate;
	    }

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
    
    public List<Book> getTop20NewBooks() {
        // Giả sử chúng ta chọn sách nổi bật bằng cách lọc dựa trên một tiêu chí cụ thể
        return bookRepository.findTop20ByOrderByIdDesc(); // Ví dụ: Lấy 20 sách mới nhất
    }
    
    
    public List<Map<String, Object>> getTop20BestSellingBooksWithImages() {
        String sql = "SELECT b.id, " +
                     "       b.title, " +
                     "       b.price, " +
                     "       b.category_id, " +
                     "       IFNULL(SUM(oi.quantity), 0) AS totalSold, " +  // Sử dụng IFNULL để thay NULL bằng 0
                     "       (SELECT bi.image_url " +
                     "        FROM book_images bi " +
                     "        WHERE bi.book_id = b.id " +
                     "        LIMIT 1) AS imageUrl " +  // Subquery cho image_url
                     "FROM books b " +
                     "LEFT JOIN order_items oi ON b.id = oi.book_id " +  // Dùng LEFT JOIN để lấy tất cả sách
                     "JOIN orders o ON o.id = oi.order_id " +  // JOIN với bảng orders để lọc theo status
                     "WHERE o.status = 'DELIVERED' " +  // Lọc các đơn hàng đã được giao
                     "GROUP BY b.id, b.title, b.price, b.category_id " +
                     "ORDER BY totalSold DESC " +
                     "LIMIT 20";

        // Sử dụng jdbcTemplate.query để thực thi SQL
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> book = new HashMap<>();
            book.put("id", rs.getLong("id"));
            book.put("title", rs.getString("title"));
            book.put("price", rs.getBigDecimal("price"));
            book.put("category_id", rs.getLong("category_id"));
            book.put("totalSold", rs.getLong("totalSold"));
            book.put("imageUrl", rs.getString("imageUrl"));
            return book;
        });
    }
    
    public List<Map<String, Object>> getTop20BooksWithRatings() {
        String sql = "SELECT b.id, " +
                     "       b.title, " +
                     "       b.price, " +
                     "       b.category_id, " +
                     "       COUNT(r.id) AS reviewCount, " +  // Đếm số lượt đánh giá
                     "       AVG(r.rating) AS averageRating, " +  // Tính số sao trung bình
                     "       (SELECT bi.image_url " +
                     "        FROM book_images bi " +
                     "        WHERE bi.book_id = b.id " +
                     "        LIMIT 1) AS imageUrl " +  // Lấy hình ảnh đầu tiên của sách
                     "FROM books b " +
                     "LEFT JOIN reviews r ON b.id = r.book_id " +  // JOIN với bảng reviews để lấy đánh giá
                     "GROUP BY b.id, b.title, b.price, b.category_id " +
                     "HAVING reviewCount > 0 " +  // Lọc chỉ các sách có đánh giá
                     "ORDER BY averageRating DESC, reviewCount DESC " +  // Sắp xếp theo số sao trung bình và lượt đánh giá
                     "LIMIT 20";

        // Sử dụng jdbcTemplate.query để thực thi SQL
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> book = new HashMap<>();
            book.put("id", rs.getLong("id"));
            book.put("title", rs.getString("title"));
            book.put("price", rs.getBigDecimal("price"));
            book.put("category_id", rs.getLong("category_id"));
            book.put("reviewCount", rs.getLong("reviewCount"));
            book.put("averageRating", rs.getDouble("averageRating"));
            book.put("imageUrl", rs.getString("imageUrl"));
            return book;
        });
    }
    
    public List<Map<String, Object>> getTop20MostFavoritedBooks() {
        String sql = "SELECT b.id, " +
                     "       b.title, " +
                     "       b.price, " +
                     "       b.category_id, " +
                     "       COUNT(f.user_id) AS favoritedCount, " +  // Đếm số lượt yêu thích
                     "       (SELECT bi.image_url " +
                     "        FROM book_images bi " +
                     "        WHERE bi.book_id = b.id " +
                     "        LIMIT 1) AS imageUrl " +  // Lấy hình ảnh đầu tiên của sách
                     "FROM books b " +
                     "LEFT JOIN favorite_books f ON b.id = f.book_id " +  // JOIN với bảng favorites để lấy lượt yêu thích
                     "GROUP BY b.id, b.title, b.price, b.category_id " +
                     "ORDER BY favoritedCount DESC " +  // Sắp xếp theo số lượt yêu thích
                     "LIMIT 20";

        // Sử dụng jdbcTemplate.query để thực thi SQL
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> book = new HashMap<>();
            book.put("id", rs.getLong("id"));
            book.put("title", rs.getString("title"));
            book.put("price", rs.getBigDecimal("price"));
            book.put("category_id", rs.getLong("category_id"));
            book.put("favoritedCount", rs.getLong("favoritedCount"));
            book.put("imageUrl", rs.getString("imageUrl"));
            return book;
        });
    }



    
}
