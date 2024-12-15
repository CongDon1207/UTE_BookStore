package ute.bookstore.controller.seller;



import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.enums.UserRole;
import ute.bookstore.service.IShopService;
import ute.bookstore.service.IUserService;

@ControllerAdvice(basePackages = "ute.bookstore.controller.seller")
public class  SellerControllerAdvice {
    @Autowired
    private IShopService shopService;
    @Autowired
    private IUserService userService;
    private static final String DEFAULT_EMAIL = "vendor@gmail.com";
    private long userID = 1L;
    private static final Long TEMP_USER_ID = 1L;
    
    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
		Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
		model.addAttribute("shop", shop != null ? shop : new Shop());
		model.addAttribute("user", userService.getUserById(TEMP_USER_ID));
	}
    
    @ModelAttribute("shop")
    public Shop getShopInfo(Principal principal) {
        if (principal != null) {
            return shopService.getShopByUserEmail(DEFAULT_EMAIL);
        }
        return null;
    }

    @ModelAttribute("user") 
    public User getUserInfo(Principal principal) {
        if (principal != null) {
            return userService.getUserById(userID);
        }
        return null;
    }
}