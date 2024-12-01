package ute.bookstore.controller.seller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category; 
import ute.bookstore.entity.Shop;
import ute.bookstore.service.ICategoryService;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.IShopService;

import java.io.IOException;
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

   @GetMapping
   public String getProductManagement(
       @RequestParam(defaultValue = "") String search,
       @RequestParam(required = false) Category category, 
       @RequestParam(required = false) Boolean availability,
       @RequestParam(defaultValue = "0") int page,
       Model model,
       @AuthenticationPrincipal UserDetails userDetails
   ) {
       Shop shop = shopService.getShopByUser(userDetails.getUsername());
       
       Page<Book> books = bookService.getShopBooks(
           shop.getId(),
           search, 
           category,
           availability,
           page,
           10
       );
       
       List<Category> categories = categoryService.getAllCategories();
       
       model.addAttribute("books", books);
       model.addAttribute("categories", categories); 
       model.addAttribute("currentPage", page);
       model.addAttribute("totalPages", books.getTotalPages());
       
       return "seller/product-management";
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
