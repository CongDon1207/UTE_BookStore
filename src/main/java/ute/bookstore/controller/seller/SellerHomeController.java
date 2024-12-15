package ute.bookstore.controller.seller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.service.ISellerHomeService;
import ute.bookstore.service.IShopService;
import ute.bookstore.service.IUserService;

@Controller
@RequestMapping("/seller")
public class SellerHomeController {
   @Autowired
   private ISellerHomeService sellerHomeService;
   @Autowired 
   private IShopService shopService;
   @Autowired
   private IUserService userService;
   
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
   
   @GetMapping({"","/home"})
   public String home(Model model, HttpServletRequest request, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) {
           return "redirect:/auth/login";
       }

       Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
       if (shop == null) {
           return "redirect:/auth/login";
       }

       shopService.updateShopRating(shop);
       
       model.addAttribute("totalOrders", sellerHomeService.getTotalOrders(shop));
       model.addAttribute("monthlyRevenue", sellerHomeService.getMonthlyRevenue(shop));
       model.addAttribute("totalProducts", sellerHomeService.getTotalProducts(shop));
       model.addAttribute("averageRating", sellerHomeService.getAverageRating(shop));
       model.addAttribute("recentOrders", sellerHomeService.getRecentOrders(shop));
       model.addAttribute("revenueData", sellerHomeService.getRevenueData(shop));
       model.addAttribute("requestURI", request.getRequestURI());
       model.addAttribute("topSellingBooks", sellerHomeService.getTopSellingBooks(shop));
       model.addAttribute("booksByCategory", sellerHomeService.getBooksByCategory(shop));
       
       return "seller/dashboard";
   }

   @GetMapping("/updateInfo")
   public String getUpdateInfoPage(Model model, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) {
           return "redirect:/auth/login";
       }
       
       model.addAttribute("user", currentUser);
       return "seller/info-edit";
   }

   @PostMapping("/update-info")
   public String updateUserInfo(@ModelAttribute("user") User user, 
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
       try {
           userService.updateUserInfo(user);
           session.setAttribute("currentUser", user);
           redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
       } catch (RuntimeException e) {
           redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
       }
       return "redirect:/seller/updateInfo";
   }

   @GetMapping("/updatePassword") 
   public String getUpdatePasswordPage(HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) {
           return "redirect:/auth/login";
       }
       return "seller/password-edit";
   }

   @PostMapping("/updatePassword")
   public String updatePassword(@RequestParam("current-password") String currentPassword,
                              @RequestParam("new-password") String newPassword, 
                              @RequestParam("confirm-password") String confirmPassword,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
       try {
           User currentUser = (User) session.getAttribute("currentUser");
           if (currentUser == null) {
               return "redirect:/auth/login";
           }

           userService.changePassword(currentUser, currentPassword, newPassword, confirmPassword);
           redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("error", e.getMessage());
       }
       return "redirect:/seller/updatePassword";
   }
}