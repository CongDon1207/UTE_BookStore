package ute.bookstore.controller.seller;

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
       model.addAttribute("books", bookService.getAllBooks());
       model.addAttribute("discounts", promotionService.getAllPromotions());
       return "seller/product-discount";
   }

   @PostMapping("/discount/add")
   public String addDiscount(@ModelAttribute Promotion promotion, @RequestParam Double value) {
       promotion.setDiscount(value);
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
