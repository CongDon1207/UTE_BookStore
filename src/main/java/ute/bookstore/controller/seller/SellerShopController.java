package ute.bookstore.controller.seller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.service.ICloudinaryService;
import ute.bookstore.service.IShopService;
import ute.bookstore.service.IUserService;



@Controller
@RequestMapping("/seller/shop")
public class SellerShopController {
   @Autowired private IShopService shopService;
   @Autowired private IUserService userService;
   private static final String DEFAULT_EMAIL = "vendor@gmail.com";
   private static final String REDIRECT_SUCCESS = "redirect:/seller/shop?success";
   private static final String REDIRECT_ERROR = "redirect:/seller/shop?error=";
   
   private static final Long TEMP_USER_ID = 1L;
   
   
   @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
		Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
		model.addAttribute("shop", shop != null ? shop : new Shop());
		model.addAttribute("user", userService.getUserById(TEMP_USER_ID));
	}
   
   @GetMapping
   public String showShopManagement(Model model) {
       try {
           Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
           model.addAttribute("shop", shop);
           return "seller/shop-management";
       } catch (Exception e) {
           return REDIRECT_ERROR + e.getMessage();
       }
   }

   @PostMapping
   public String updateShopInfo(@ModelAttribute Shop shopUpdate,
                              @RequestParam(required = false) MultipartFile logoFile) {
       try {
           System.out.println("Update shop: " + shopUpdate);
           System.out.println("Logo: " + (logoFile != null ? logoFile.getOriginalFilename() : "No file"));
           
           shopService.updateShop(shopUpdate, logoFile, DEFAULT_EMAIL);
           return REDIRECT_SUCCESS;
       } catch (Exception e) {
           e.printStackTrace();
           return REDIRECT_ERROR + e.getMessage();
       }
   }
}
