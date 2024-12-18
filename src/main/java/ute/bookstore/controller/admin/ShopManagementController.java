package ute.bookstore.controller.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import ute.bookstore.dto.admin.ShopRevenueStats;
import ute.bookstore.entity.Shop;
import ute.bookstore.service.admin.AdminShopService;
import ute.bookstore.service.admin.impl.IAdminShopService;

@Controller
@RequestMapping("/admin/shop-management")
@RequiredArgsConstructor
public class ShopManagementController {
	private final IAdminShopService adminShopService;

	@GetMapping
	public String getShopManagementPage(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {

		Page<Shop> shops = adminShopService.getAllShopsForAdmin(page, size, search);
		model.addAttribute("shops", shops);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", shops.getTotalPages());
		model.addAttribute("search", search);
		return "admin/shop/index";
	}

	@GetMapping("/{id}/detail")
	public ModelAndView getShopDetail(@PathVariable Long id) {
		Shop shop = adminShopService.getShopDetail(id);
		ModelAndView mav = new ModelAndView("admin/shop/modals/detail-modal :: modalContent");
		mav.addObject("shop", shop);
		return mav;
	}

	@GetMapping("/pending")
	public String getPendingShops(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<Shop> pendingShops = adminShopService.getPendingShops(page, size);
		model.addAttribute("shops", pendingShops);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pendingShops.getTotalPages());
		return "admin/shop/pending";
	}

	@PostMapping("/toggle-status/{id}")
	@ResponseBody
	public ResponseEntity<?> toggleShopStatus(@PathVariable Long id) {
		boolean newStatus = adminShopService.toggleShopStatus(id);
		return ResponseEntity.ok(Map.of("status", newStatus));
	}

	@PostMapping("/{id}/approve")
	@ResponseBody
	public ResponseEntity<?> approveShop(@PathVariable Long id) {
	    try {
	        Shop shop = adminShopService.approveShop(id);
	        return ResponseEntity.ok(Map.of(
	            "success", true,
	            "message", "Đã phê duyệt shop thành công"
	        ));
	    } catch (Exception e) {
	        return ResponseEntity.ok(Map.of(
	            "success", false,
	            "message", e.getMessage()
	        ));
	    }
	}
	
	@GetMapping("/rejected")
	public String getRejectedShops(Model model, 
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {
	    
	    Page<Shop> rejectedShops = adminShopService.getRejectedShops(page, size);
	    model.addAttribute("shops", rejectedShops);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", rejectedShops.getTotalPages());
	    return "admin/shop/rejected";
	}

	@PostMapping("/{id}/reject")
	@ResponseBody
	public ResponseEntity<?> rejectShop(@PathVariable Long id, @RequestParam String reason) {
	    try {
	        Shop shop = adminShopService.rejectShop(id, reason);
	        return ResponseEntity.ok(Map.of(
	            "success", true,
	            "message", "Đã từ chối shop thành công"
	        ));
	    } catch (Exception e) {
	        return ResponseEntity.ok(Map.of(
	            "success", false,
	            "message", e.getMessage()
	        ));
	    }
	}

	@PostMapping("/{id}/commission")
	@ResponseBody
	public ResponseEntity<Shop> updateCommission(@PathVariable Long id, @RequestParam Double rate) {
		return ResponseEntity.ok(adminShopService.updateCommissionRate(id, rate));
	}
	
	@GetMapping("/{id}/stats")
	@ResponseBody
	public ResponseEntity<ShopRevenueStats> getShopStats(@PathVariable Long id) {
	    Shop shop = adminShopService.getShopById(id);
	    ShopRevenueStats stats = adminShopService.getShopStats(id);
	    return ResponseEntity.ok(stats);
	}
}