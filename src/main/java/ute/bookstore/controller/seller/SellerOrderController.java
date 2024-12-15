package ute.bookstore.controller.seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ute.bookstore.entity.Order;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.service.IOrderService;
import ute.bookstore.service.IShopService;


@Controller
@RequestMapping("/seller/orders")
public class SellerOrderController {

   private final IOrderService orderService;
   @Autowired private IShopService shopService;
   public SellerOrderController(IOrderService orderService) {
       this.orderService = orderService;
   }

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

   @GetMapping
   public String getOrderList(Model model,
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "ALL") String status,
           HttpSession session) {
       
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) {
           return "redirect:/auth/login";
       }

       Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
       if (shop == null) {
           return "redirect:/auth/login";
       }

       Page<Order> orders = orderService.getOrdersByShopAndStatus(shop.getId(), status, PageRequest.of(page, 10));

       model.addAttribute("orders", orders);
       model.addAttribute("currentPage", page);
       model.addAttribute("totalPages", orders.getTotalPages());
       model.addAttribute("selectedStatus", status);

       return "seller/order-management";
   }

   @GetMapping("/detail/{id}")
   public String getOrderDetail(@PathVariable Long id, Model model, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) {
           return "redirect:/auth/login";
       }
       
       Order order = orderService.getOrderById(id);
       model.addAttribute("order", order);
       return "seller/order-detail";
   }

   @PostMapping("/{id}/status")
   @ResponseBody
   public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,
           @RequestParam OrderStatus status,
           HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) {
           return ResponseEntity.badRequest().body("User not authenticated");
       }
       
       orderService.updateOrderStatus(id, status);
       return ResponseEntity.ok().build();
   }
}