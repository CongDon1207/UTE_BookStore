package ute.bookstore.controller.seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import ute.bookstore.entity.Order;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.service.IOrderService;


@Controller
@RequestMapping("/seller/orders")
public class SellerOrderController {

    private final IOrderService orderService; 
    public SellerOrderController(IOrderService orderService) {
    	this.orderService = orderService;
    	}
    
    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
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
