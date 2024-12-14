package ute.bookstore.controller.seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import ute.bookstore.entity.Order;
import ute.bookstore.entity.Shop;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.service.IOrderService;
import ute.bookstore.service.IShopService;
import ute.bookstore.service.IUserService;


@Controller
@RequestMapping("/seller/orders")
public class SellerOrderController {

    private final IOrderService orderService; 
    @Autowired private IShopService shopService;
    @Autowired private IUserService userService;
    public SellerOrderController(IOrderService orderService) {
    	this.orderService = orderService;
    	}
    
    private static final Long TEMP_USER_ID = 1L;
    private static final String DEFAULT_EMAIL = "vendor@gmail.com";
    
    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
		Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
		model.addAttribute("shop", shop != null ? shop : new Shop());
		model.addAttribute("user", userService.getUserById(TEMP_USER_ID));
	}
    
    @GetMapping
    public String getOrderList(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "ALL") String status) {
        Long shopId = 1L; // Tạm thời hardcode, sau này lấy từ user đăng nhập
        Page<Order> orders = orderService.getOrdersByShopAndStatus(shopId, status, PageRequest.of(page, 10));
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("selectedStatus", status);
        
        return "seller/order-management";
    }

    @GetMapping("/detail/{id}")
    public String getOrderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "seller/order-detail";
    }

    @PostMapping("/{id}/status") 
    @ResponseBody
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,
                                             @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
