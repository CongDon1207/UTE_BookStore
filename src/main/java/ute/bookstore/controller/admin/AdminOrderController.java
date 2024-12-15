// AdminOrderController.java
package ute.bookstore.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ute.bookstore.service.admin.AdminOrderService;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    
    @Autowired
    private AdminOrderService orderService;
    
    @GetMapping
    public String showOrders(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) OrderStatus status,
        @RequestParam(required = false) Long shopId,
        @RequestParam(defaultValue = "0") int page,
        Model model) {

        // Thống kê đơn hàng theo tất cả trạng thái
        Map<String, Long> stats = new HashMap<>();
        for (OrderStatus orderStatus : OrderStatus.values()) {
            String key = orderStatus.name().toLowerCase() + "Orders"; // newOrders, confirmedOrders, etc.
            stats.put(key, orderService.countOrdersByStatus(orderStatus));
        }

        Page<Order> orders = orderService.getOrders(search, status, shopId, page);

        model.addAttribute("stats", stats);
        model.addAttribute("orders", orders);
        model.addAttribute("shops", orderService.getAllShops());
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentShop", shopId);
        model.addAttribute("search", search);

        return "admin/order/index";
    }
    
    @GetMapping("/{id}")
    public String getOrderDetail(@PathVariable Long id, Model model) {
        try {
            Order order = orderService.getOrderById(id);
            model.addAttribute("order", order);
            return "admin/order/modals/order-detail-modal :: orderDetailContent";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Không tìm thấy đơn hàng");
            return "admin/order/modals/error :: errorContent"; 
        }
    }
    
    @PostMapping("/{id}/update-status")
    @ResponseBody
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        OrderStatus newStatus = OrderStatus.valueOf(payload.get("status"));
        Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Cập nhật trạng thái thành công",
            "newStatus", updatedOrder.getStatus()
        ));
    }
    
    @PostMapping("/{id}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        Order cancelledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Hủy đơn hàng thành công"
        ));
    }
}