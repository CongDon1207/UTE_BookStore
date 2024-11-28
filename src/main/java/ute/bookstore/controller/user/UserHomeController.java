package ute.bookstore.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserHomeController {
	
	@GetMapping({"","/home"})
	public String home() {
		return "user/home.html";
	}
	@GetMapping("/cart")
	public String getCartPage() {
		return "user/cart/show";
	}
	@GetMapping("/shopping")
	public String getShoppingPage() {
		return "user/user-shopping";
	}
	@GetMapping("/updateInfo")
	public String getUpdateInfoPage() {
		return "user/user-edit";
	}
	@GetMapping("/manageDeliveryAddresses")
	public String getDeliveryAddressesPage() {
		return "user/user-manage-address";
	}
	
	@GetMapping("/updatePassword")
	public String getUpdatePasswordPage() {
		return "user/user-edit-password";
	}
	@GetMapping("/purchaseHistory")
	public String getPurchaseHistoryPage() {
		return "user/user-purchase-history";
	}
}
