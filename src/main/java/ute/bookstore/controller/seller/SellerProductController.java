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
import ute.bookstore.entity.Promotion;
import ute.bookstore.entity.Shop;
import ute.bookstore.service.ICategoryService;
import ute.bookstore.service.IPromotionService;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.IShopService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/seller/products")
public class SellerProductController {
	@Autowired
	private IBookService bookService;

	@Autowired
	private ICategoryService categoryService;
	
	@Autowired
	private IPromotionService promotionService;
	
	@Autowired
	private IShopService shopService;
	
	private static final String DEFAULT_EMAIL = "vendor@gmail.com";

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
	}

	@GetMapping
	public String getProductManagement(@RequestParam(defaultValue = "") String search,
			@RequestParam(required = false) Category category, @RequestParam(required = false) Boolean availability,
			@RequestParam(defaultValue = "0") int page, Model model, @AuthenticationPrincipal UserDetails userDetails) {
		// Test shop data
		Shop shop = new Shop();
		shop.setId(1L);
		shop.setName("Test Book Shop");

		// Test book data
		List<Book> bookList = bookService.getAllBooks();

		// Create pageable test data
		int size = 10;
		Pageable pageable = PageRequest.of(page, size);
		Page<Book> books = new PageImpl<>(bookList, pageable, bookList.size());

		// Test categories
		List<Category> categories = categoryService.getAllCategories();

		model.addAttribute("books", books);
		model.addAttribute("categories", categories);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", books.getTotalPages());
		model.addAttribute("hasNext", page < books.getTotalPages() - 1);
		model.addAttribute("hasPrevious", page > 0);

		return "seller/product-management";
	}

	

	@GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "seller/add-product";
    }
    
	@PostMapping("/add")
	public String createProduct(
	    @RequestParam("title") String title,
	    @RequestParam("category.id") Long categoryId,
	    @RequestParam("price") Double price,
	    @RequestParam("quantity") Integer quantity,
	    @RequestParam("description") String description, 
	    @RequestParam("imageFiles") MultipartFile[] imageFiles
	) throws IOException {
	    Book book = new Book();
	    book.setTitle(title);
	    book.setDescription(description);
	    book.setPrice(price);
	    book.setQuantity(quantity);
	    
	 // Lấy shop hiện tại
	    Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
	    
	    Category category = categoryService.getCategoryById(categoryId);
	    book.setCategory(category);
	    
	    bookService.createBook(book, imageFiles, shop);
	    return "redirect:/seller/products";
	}
	
	@GetMapping("/{id}/edit")
	public String editProduct(@PathVariable Long id, Model model) {
	    Book book = bookService.getBookById(id);
	    model.addAttribute("book", book);
	    model.addAttribute("categories", categoryService.getAllCategories());
	    return "seller/edit-product";
	}
	
	@PostMapping("/{id}/edit")
	public String updateProduct(
	    @PathVariable Long id,
	    @ModelAttribute Book updatedBook,
	    @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles
	) throws IOException {
	    Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
	    if (imageFiles != null && imageFiles.length > 0 && !imageFiles[0].isEmpty()) {
	        bookService.updateBook(id, updatedBook, imageFiles, shop);
	    } else {
	        bookService.updateBook(id, updatedBook, null, shop);
	    }
	    return "redirect:/seller/products";
	}
	
	
	@PostMapping("/{id}/delete")
	public String deleteProduct(@PathVariable Long id) {
	   bookService.deleteBook(id);
	   return "redirect:/seller/products";
	}
}
