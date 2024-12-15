package ute.bookstore.controller.user;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import ute.bookstore.entity.Book;
import ute.bookstore.entity.Order;
import ute.bookstore.entity.User;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.enums.PaymentMethod;

import ute.bookstore.service.IAddressService;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.IOrderService;
import ute.bookstore.service.IReviewService;
import ute.bookstore.service.IShopService;
import ute.bookstore.service.IUserService;


@Controller
@RequestMapping("/user/orders")
public class UserOrderController {
	private long userID = 3L;
	@Autowired
    private IOrderService orderService;
	
	@Autowired
    private IBookService bookService;
	
	@Autowired
    private IUserService userService;
	
	@Autowired
    private IAddressService addressService;
	
	@Autowired
    private IShopService shopService;
	
	@Autowired
    private IReviewService reviewService;

	@PostMapping("/create")
	public String createOrder(
	    @RequestParam Long userId,
	    @RequestParam Long shopId,
	    @RequestParam Long addressId,
	    @RequestParam String bookIds, // Nhận danh sách ID dưới dạng chuỗi
	    @RequestParam List<Integer> quantities,
	    @RequestParam String paymentMethod,
	    @RequestParam Double totalAmount,
	    RedirectAttributes redirectAttributes
	) {
	    try {
	        // Chuyển đổi chuỗi bookIds thành List<Long>
	        List<Long> bookIdList = Arrays.stream(bookIds.split(","))
	                                      .map(Long::valueOf)
	                                      .collect(Collectors.toList());

	        // Lấy danh sách sách từ danh sách ID
	        List<Book> books = bookIdList.stream()
	                                     .map(bookService::getBookById) // Gọi BookService để lấy Book
	                                     .collect(Collectors.toList());

	        // Tạo đơn hàng
	        Order createdOrder = orderService.createOrder(
	            userService.getUserById(userId), 
	            shopService.getShopById(shopId), 
	            addressService.getAddressById(addressId), 
	            books, 
	            quantities, 
	            PaymentMethod.valueOf(paymentMethod), 
	            totalAmount
	        );

	        // Thông báo thành công
	        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được tạo thành công");
	        return "redirect:/user/purchaseHistory";
	    } catch (Exception e) {
	        // Thông báo lỗi
	        redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi tạo đơn hàng: " + e.getMessage());
	        return "redirect:/user/purchaseHistory";
	    }
	}
	
	@PostMapping("/cancel/{orderId}")
	public String cancelOrder(
			@PathVariable("orderId") Long orderId, 
		    RedirectAttributes redirectAttributes
		) {
		    try {
		        // Lấy thông tin đơn hàng
		        Order order = orderService.getOrderById(orderId);

		        // Cập nhật lại số lượng sách trong kho
		        order.getItems().forEach(item -> {
		            Book book = item.getBook();
		            book.setQuantity(book.getQuantity() + item.getQuantity()); // Cộng lại số lượng
		            bookService.updateBook(book); // Cập nhật vào database
		        });

		        // Xóa đơn hàng
		        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);

		        // Thông báo thành công
		        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được hủy thành công!");
		        return "redirect:/user/purchaseHistory";
		    } catch (Exception e) {
		        // Xử lý lỗi
		        redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi hủy đơn hàng: " + e.getMessage());
		        return "redirect:/user/purchaseHistory";
		    }
		}

	@PostMapping("/reviews")
	public String saveReviews() {
		return "redirect:/user/purchaseHistory";
	    

	}
	
	@PostMapping("/{orderId}/review")
	public ResponseEntity<String> submitReview(
	        @PathVariable Long orderId,
	        @RequestParam Long bookId,
	        @RequestParam int rating,
	        @RequestParam(required = false) String comment
	        // Lấy người dùng hiện tại
	) {
		  try {
			  User user = userService.getUserById(userID);
	            // Tìm sách theo ID
	            Book book = bookService.getBookById(bookId);
	            if (book == null) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sách không tồn tại!");
	            }

	            // Lưu đánh giá
	            reviewService.saveReview(user, book, rating, comment);

	            return ResponseEntity.ok("Đánh giá thành công!");
	        } catch (IllegalStateException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	}

}
