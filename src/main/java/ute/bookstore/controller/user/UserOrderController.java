package ute.bookstore.controller.user;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import ute.bookstore.entity.Book;
import ute.bookstore.entity.Order;

import ute.bookstore.enums.PaymentMethod;
import ute.bookstore.service.IAddressService;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.IOrderService;
import ute.bookstore.service.IShopService;
import ute.bookstore.service.IUserService;


@Controller
@RequestMapping("/user/orders")
public class UserOrderController {
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


	
	
}
