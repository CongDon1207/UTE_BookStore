package ute.bookstore.controller.seller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category; 
import ute.bookstore.entity.Shop;
import ute.bookstore.service.ICategoryService;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.IShopService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/seller/products")
public class SellerProductController {
   @Autowired 
   private IBookService bookService;
   
   @Autowired
   private IShopService shopService;
   
   @Autowired
   private ICategoryService categoryService;
   
   @ModelAttribute
   public void addAttributes(Model model, HttpServletRequest request) {
       model.addAttribute("requestURI", request.getRequestURI());
   }
   
   @GetMapping
   public String getProductManagement(
		    @RequestParam(defaultValue = "") String search,
		    @RequestParam(required = false) Category category, 
		    @RequestParam(required = false) Boolean availability,
		    @RequestParam(defaultValue = "0") int page,
		    Model model,
		    @AuthenticationPrincipal UserDetails userDetails
		) {
		    // Test shop data
		    Shop shop = new Shop();
		    shop.setId(1L);
		    shop.setName("Test Book Shop");
		    
		    // Test book data
		    List<Book> bookList = Arrays.asList(
		        createTestBook(1L, "Clean Code", "Programming", 299000.0, true),
		        createTestBook(2L, "Design Patterns", "Programming", 359000.0, true),
		        createTestBook(3L, "Doraemon Tập 1", "Manga", 25000.0, true),
		        createTestBook(4L, "Sách Giáo Khoa Toán 12", "Textbook", 15000.0, false)
		    );
		    
		    // Create pageable test data
		    int size = 10;
		    Pageable pageable = PageRequest.of(page, size);
		    Page<Book> books = new PageImpl<>(bookList, pageable, bookList.size());

		    // Test categories 
		    List<Category> categories = Arrays.asList(
		        createTestCategory(1L, "Programming", null),
		        createTestCategory(2L, "Manga", null),
		        createTestCategory(3L, "Textbook", null)
		    );

		    model.addAttribute("books", books);
		    model.addAttribute("categories", categories);
		    model.addAttribute("currentPage", page);
		    model.addAttribute("totalPages", books.getTotalPages());
		    model.addAttribute("hasNext", page < books.getTotalPages() - 1);
		    model.addAttribute("hasPrevious", page > 0);

		    return "seller/product-management";
		}

		private Book createTestBook(Long id, String title, String categoryName, Double price, Boolean available) {
		    Book book = new Book();
		    book.setId(id);
		    book.setTitle(title);
		    book.setPrice(price);
		    book.setIsAvailable(available);
		    book.setQuantity(100);
		    
		    Category category = new Category();
		    category.setName(categoryName);
		    book.setCategory(category);
		    
		    return book;
		}

		private Category createTestCategory(Long id, String name, Category parent) {
		    Category category = new Category();
		    category.setId(id);
		    category.setName(name);
		    category.setParent(parent);
		    return category;
		}

   @PostMapping
   public String createProduct(
       @ModelAttribute Book book,
       @RequestParam("images") MultipartFile[] images,
       @AuthenticationPrincipal UserDetails userDetails
   ) {
       try {
           Shop shop = shopService.getShopByUser(userDetails.getUsername());
           book.setShops(List.of(shop)); // Thay vì add trực tiếp 
           bookService.createBook(book, images);
           return "redirect:/seller/products?success=created";
       } catch (IOException e) {
           return "redirect:/seller/products?error=image-upload";
       }
   }

   @PutMapping("/{id}") 
   public String updateProduct(
       @PathVariable Long id,
       @ModelAttribute Book book,
       @RequestParam(required = false) MultipartFile[] images
   ) {
       try {
           bookService.updateBook(id, book, images);
           return "redirect:/seller/products?success=updated";
       } catch (IOException e) {
           return "redirect:/seller/products?error=image-upload";
       }
   }

   @DeleteMapping("/{id}")
   public String deleteProduct(@PathVariable Long id) {
       bookService.deleteBook(id);
       return "redirect:/seller/products?success=deleted";
   }
}
