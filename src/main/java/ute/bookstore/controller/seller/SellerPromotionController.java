package ute.bookstore.controller.seller;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ute.bookstore.entity.Promotion;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.entity.Book;
import ute.bookstore.enums.DiscountType;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.IPromotionService;
import ute.bookstore.service.IShopService;

@Controller
@RequestMapping("/seller/promotions")
public class SellerPromotionController {
   @Autowired private IBookService bookService;
   @Autowired private IPromotionService promotionService;
   @Autowired private IShopService shopService;
   @ModelAttribute
   public void addAttributes(Model model, HttpServletRequest request, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser != null) {
           Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
           model.addAttribute("shop", shop != null ? shop : new Shop());
           model.addAttribute("user", currentUser);
       }
       model.addAttribute("requestURI", request.getRequestURI());
   }

   @GetMapping("/discount")
   public String getDiscountPage(Model model, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) return "redirect:/auth/login";

       Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
       model.addAttribute("books", bookService.getAllBooks());
       model.addAttribute("discounts", promotionService.findShopDiscounts(shop));
       return "seller/product-discount";
   }

   @PostMapping("/discount/add")
   public String addDiscount(@RequestParam Long bookId, 
                           @RequestParam DiscountType discountType,
                           @RequestParam Double discount, 
                           @RequestParam LocalDateTime startDate, 
                           @RequestParam LocalDateTime endDate,
                           HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) return "redirect:/auth/login";

       Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
       Book book = bookService.getBookById(bookId);

       Promotion promotion = new Promotion();
       promotion.setBook(book);
       promotion.setDiscountType(discountType);
       promotion.setDiscount(discount);
       promotion.setStartDate(startDate);
       promotion.setEndDate(endDate);
       promotion.setIsActive(true);
       promotion.setShop(shop);

       promotionService.savePromotion(promotion);
       return "redirect:/seller/promotions/discount";
   }

   @PostMapping("/discount/{id}/edit")
   public String updateDiscount(@PathVariable Long id,
                              @RequestParam Long bookId,
                              @ModelAttribute Promotion promotion,
                              HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) return "redirect:/auth/login";

       Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
       Book book = bookService.getBookById(bookId);
       Promotion existingPromotion = promotionService.getPromotionById(id);
       
       existingPromotion.setBook(book);
       existingPromotion.setDiscountType(DiscountType.valueOf(promotion.getDiscountType()));
       existingPromotion.setDiscount(promotion.getDiscount());
       existingPromotion.setStartDate(promotion.getStartDate());
       existingPromotion.setEndDate(promotion.getEndDate());
       existingPromotion.setShop(shop);
       existingPromotion.setIsActive(true);

       promotionService.updatePromotion(id, existingPromotion);
       return "redirect:/seller/promotions/discount";
   }

   @PostMapping("/discount/{id}/delete")
   public String deleteDiscount(@PathVariable Long id, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) return "redirect:/auth/login";
       promotionService.deletePromotion(id);
       return "redirect:/seller/promotions/discount";
   }

   @GetMapping("/voucher") 
   public String getVoucherPage(Model model, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) return "redirect:/auth/login";

       Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
       model.addAttribute("vouchers", promotionService.getShopVouchers(shop));
       return "seller/product-voucher";
   }

   @PostMapping("/voucher/add")
   public String addVoucher(@RequestParam String code,
                           @RequestParam Double discount,
                           @RequestParam Double minOrderAmount,
                           @RequestParam Integer maxUses,
                           @RequestParam LocalDateTime startDate,
                           @RequestParam LocalDateTime endDate,
                           HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) return "redirect:/auth/login";

       Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
       
       Promotion voucher = new Promotion();
       voucher.setCode(code);
       voucher.setDiscount(discount);
       voucher.setMinOrderAmount(minOrderAmount);
       voucher.setMaxUses(maxUses);
       voucher.setStartDate(startDate);
       voucher.setEndDate(endDate);
       voucher.setIsActive(true);
       voucher.setShop(shop);

       promotionService.savePromotion(voucher);
       return "redirect:/seller/promotions/voucher";
   }

   @PostMapping("/voucher/{id}/edit")
   public String updateVoucher(@PathVariable Long id, 
                             @ModelAttribute Promotion voucher,
                             HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) return "redirect:/auth/login";

       Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
       voucher.setId(id);
       voucher.setShop(shop);
       voucher.setIsActive(true);

       promotionService.updatePromotion(id, voucher);
       return "redirect:/seller/promotions/voucher";
   }

   @PostMapping("/voucher/{id}/delete")
   public String deleteVoucher(@PathVariable Long id, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) return "redirect:/auth/login";
       promotionService.deletePromotion(id);
       return "redirect:/seller/promotions/voucher"; 
   }
}
