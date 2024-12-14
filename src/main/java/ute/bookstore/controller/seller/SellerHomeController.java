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

import ute.bookstore.entity.Order;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.service.ICloudinaryService;
import ute.bookstore.service.ISellerHomeService;
import ute.bookstore.service.IShopService;
import ute.bookstore.service.IUserService;
import ute.bookstore.service.impl.SellerHomeServiceImpl;
import ute.bookstore.service.impl.ShopServiceImpl;

@Controller
@RequestMapping("/seller")
public class SellerHomeController {
	private long userID = 1L;
    @Autowired
    private ISellerHomeService sellerHomeService;
    @Autowired private IShopService shopService;
    
    @Autowired
	private IUserService userService;
    private static final String DEFAULT_EMAIL = "vendor@gmail.com";
    
    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
	}
    
    @GetMapping({"","/home"})
    public String home(Model model, HttpServletRequest request) {
        Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
        
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
    
 // Lấy trang cập nhật thông tin
 	@GetMapping("/updateInfo")
 	public String getUpdateInfoPage(@RequestParam(value = "id", required = false) Long id, Model model) {
 		// Sử dụng ID từ request hoặc mặc định là người dùng đang đăng nhập
 		if (id == null) {
 			id = 1L; // ID mặc định cho mục đích thử nghiệm, thay bằng logic lấy ID người dùng đang
 						// đăng nhập
 		}
 		User user = userService.getUserById(userID);
 		model.addAttribute("user", user);
 		return "seller/info-edit"; // Thymeleaf tự động thêm .html
 	}

 	// Xử lý cập nhật thông tin
 	@PostMapping("/update-info")
 	public String updateUserInfo(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
 		try {
 			userService.updateUserInfo(user);
 			redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
 		} catch (RuntimeException e) {
 			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 		}
 		return "redirect:/seller/updateInfo?id=" + user.getId();
 	}
 	
 	// Hiển thị trang đổi mật khẩu
	@GetMapping("/updatePassword")
	public String getUpdatePasswordPage() {
		return "seller/password-edit";
	}

	// Xử lý form đổi mật khẩu
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam("current-password") String currentPassword,
			@RequestParam("new-password") String newPassword, @RequestParam("confirm-password") String confirmPassword,
			RedirectAttributes redirectAttributes) {
		try {
			// Lấy người dùng từ session (hoặc security context)
			User currentUser = userService.getUserById(userID);

			// Gọi service để đổi mật khẩu
			userService.changePassword(currentUser, currentPassword, newPassword, confirmPassword);
			redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/seller/updatePassword";
	}
    
    public static void main(String args[]) {
    	try {
    		ISellerHomeService sellerHomeService = new SellerHomeServiceImpl();
    		IShopService shopService = new ShopServiceImpl();
    		Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
    		
    		System.out.println("Revenue Data: " + sellerHomeService.getTopSellingBooks(shop)); // Thêm log để kiểm tra
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}