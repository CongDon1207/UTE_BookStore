package ute.bookstore.controller.seller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ute.bookstore.entity.Promotion;
import ute.bookstore.entity.Book;
import ute.bookstore.enums.DiscountType;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.IPromotionService;

@Controller
@RequestMapping("/seller/promotions")
public class SellerPromotionController {
   @Autowired
   private IBookService bookService;
   
   @Autowired 
   private IPromotionService promotionService;

   @GetMapping("/discount") 
   public String getDiscountPage(Model model) {
       List<Book> books = bookService.getAllBooks();
       List<Promotion> discounts = promotionService.getAllPromotions();
       System.out.println("Books: " + books);
       System.out.println("Discounts: " + discounts);
       
       model.addAttribute("books", books);
       model.addAttribute("discounts", discounts);
       return "seller/product-discount";
   }

   @PostMapping("/discount/add")
   public String addDiscount(@RequestParam Long bookId, 
                            @RequestParam DiscountType discountType,
                            @RequestParam Double discount,
                            @RequestParam LocalDateTime startDate,
                            @RequestParam LocalDateTime endDate) {
       
       Book book = bookService.getBookById(bookId);
       Promotion promotion = new Promotion();
       promotion.setBook(book); // Set book trực tiếp
       promotion.setDiscountType(discountType);
       promotion.setDiscount(discount);
       promotion.setStartDate(startDate);
       promotion.setEndDate(endDate);
       promotion.setIsActive(true);
       
       promotionService.savePromotion(promotion);
       return "redirect:/seller/promotions/discount";
   }

   @PostMapping("/discount/{id}/edit")
   public String updateDiscount(@PathVariable Long id, @ModelAttribute Promotion promotion) {
       promotionService.updatePromotion(id, promotion);
       return "redirect:/seller/promotions/discount"; 
   }
   
   @PostMapping("/discount/{id}/delete")
   public String deleteDiscount(@PathVariable Long id) {
       promotionService.deletePromotion(id);
       return "redirect:/seller/promotions/discount";
   }
   
   
}
