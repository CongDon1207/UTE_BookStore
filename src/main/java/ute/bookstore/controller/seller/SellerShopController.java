package ute.bookstore.controller.seller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.service.IShopService;



@Controller
@RequestMapping("/seller/shop")
public class SellerShopController {
   @Autowired private IShopService shopService;
   private static final String REDIRECT_SUCCESS = "redirect:/seller/shop?success";
   private static final String REDIRECT_ERROR = "redirect:/seller/shop?error=";

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

   @GetMapping
   public String showShopManagement(Model model, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) {
           return "redirect:/auth/login";
       }

       try {
           Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
           shopService.updateShopRating(shop);
           model.addAttribute("shop", shop);
           return "seller/shop-management";
       } catch (Exception e) {
           return REDIRECT_ERROR + e.getMessage();
       }
   }

   @PostMapping
   public String updateShopInfo(@ModelAttribute Shop shopUpdate,
                               @RequestParam(required = false) MultipartFile logoFile,
                               HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) {
           return "redirect:/auth/login";
       }

       try {
           shopService.updateShop(shopUpdate, logoFile, currentUser.getEmail());
           return REDIRECT_SUCCESS;
       } catch (Exception e) {
           e.printStackTrace();
           return REDIRECT_ERROR + e.getMessage();
       }
   }
}
