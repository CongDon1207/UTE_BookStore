package ute.bookstore.controller.seller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.service.IShopService;



@Controller
@RequestMapping("/seller/shop")
public class SellerShopController {
    @Autowired private IShopService shopService;

    
    @GetMapping
    public String showShopManagement(Model model, Principal principal) {
       String email = getDefaultOrCurrentUser(principal);
       Shop shop = shopService.getShopByUserId(email);
       model.addAttribute("shop", shop);
       return "seller/shop-management";
    }

    private String getDefaultOrCurrentUser(Principal principal) {
       return principal != null ? principal.getName() : "vendor@gmail.com";
    }

    @PostMapping("")
    public String updateShopInfo(@ModelAttribute Shop shop,
            @RequestParam(required = false) MultipartFile logoFile,
            Principal principal) {
        try {
            shopService.updateShop(shop, logoFile, principal.getName());
            return "redirect:/seller/shop/management?success"; 
        } catch (Exception e) {
            return "redirect:/seller/shop/management?error";
        }
    }
}
