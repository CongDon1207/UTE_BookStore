package ute.bookstore.controller.user;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import ute.bookstore.entity.Address;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category;
import ute.bookstore.entity.Notification;
import ute.bookstore.entity.User;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.service.IAddressService;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.ICategoryService;
import ute.bookstore.service.INotificationService;
import ute.bookstore.service.IOrderService;
import ute.bookstore.service.IUserService;

@Controller
@RequestMapping("/user")
public class UserHomeController {
	
	@Autowired
	private INotificationService notificationService;
	@Autowired
	private ICategoryService categoryService;
	@Autowired
	private IBookService bookService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAddressService addressService;
	
	@Autowired
	private IOrderService orderService;
	
	@Autowired
    private PasswordEncoder passwordEncoder;

	@GetMapping({ "", "/home" })
	public String homePage(Model model) {
		// Lấy danh sách sách từ database
		List<Book> featuredBooks = bookService.getFeaturedBooks();
		model.addAttribute("featuredBooks", featuredBooks);
		return "user/user-home";
	}

	@GetMapping("/notifications")
	public String getNotificationPage(Model model, HttpSession session) {
		User iuser = (User) session.getAttribute("currentUser");
		User user = userService.getUserById(iuser.getId());
		List<Notification> notifications = user.getNotifications();

		// Đảm bảo danh sách không null và sắp xếp thông báo từ mới đến cũ
		if (notifications != null) {
			notifications.sort((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()));
		}

		model.addAttribute("user", user);
		model.addAttribute("notifications", notifications);
		return "user/user-notifications.html";
	}

	@RequestMapping("/notification/read/{id}")
	public String getReadNotificationPage(@PathVariable("id") Long id, Model model,HttpSession session) {
		Notification notification = notificationService.findById(id);
		if (notification != null) {
			notification.setIsRead(true);
			notificationService.save(notification);

		} else {

		}
		User iuser = (User) session.getAttribute("currentUser");
		User user = userService.getUserById(iuser.getId());
		List<Notification> notifications = user.getNotifications();

		// Đảm bảo danh sách không null và sắp xếp thông báo từ mới đến cũ
		if (notifications != null) {
			notifications.sort((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()));
		}

		model.addAttribute("user", user);
		model.addAttribute("notifications", notifications);
		return "user/user-notifications.html";
	}

	@GetMapping("/shopping")
	public String getShoppingPage(Model model) {
		// Lấy danh sách sách từ database
		List<Book> newBooks = bookService.getTop20NewBooks();
		model.addAttribute("newBooks", newBooks);

		// Lấy danh sách top 20 sách bán chạy cùng hình ảnh
		List<Map<String, Object>> bestSellingBooks = bookService.getTop20BestSellingBooksWithImages();
		model.addAttribute("bestSellingBooks", bestSellingBooks);

		// Lấy danh sách top 20 sách dánh giá cao
		List<Map<String, Object>> bestRatingingBooks = bookService.getTop20BooksWithRatings();
		model.addAttribute("bestRatingBooks", bestRatingingBooks);

		// Lấy danh sách top 20 sách được yêu thích nhiều nhất
		List<Map<String, Object>> mostFavoritedBooks = bookService.getTop20MostFavoritedBooks();
		model.addAttribute("mostFavoritedBooks", mostFavoritedBooks);

		return "user/user-shopping.html";
	}

	// Lấy trang cập nhật thông tin
	@GetMapping("/updateInfo")
	public String getUpdateInfoPage(@RequestParam(value = "id", required = false) Long id, Model model,HttpSession session) {
		
		User iuser = (User) session.getAttribute("currentUser");
		User user = userService.getUserById(iuser.getId());
		model.addAttribute("user", user);
		return "user/user-edit"; // Thymeleaf tự động thêm .html
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
		return "redirect:/user/updateInfo?id=" + user.getId();
	}

	@GetMapping("/manageDeliveryAddresses")
	public String getDeliveryAddressesPage(Model model, HttpSession session ) {
		// Lấy thông tin user hiện tại
		User user = (User) session.getAttribute("currentUser");
		// Lấy danh sách địa chỉ của user
		List<Address> addresses = addressService.getAddressesByUserId(user.getId());

		int hasDefaultAddress = addresses != null && addresses.stream().anyMatch(Address::getIsDefault) ? 1 : 0;

		model.addAttribute("user", user);
		model.addAttribute("addresses", addresses);
		model.addAttribute("hasDefaultAddress", hasDefaultAddress);
		// Thêm đối tượng Address rỗng cho form
		model.addAttribute("newAddress", new Address());
		return "user/user-manage-address.html";

	}

	// Hiển thị trang đổi mật khẩu
	@GetMapping("/updatePassword")
	public String getUpdatePasswordPage() {
		return "user/user-edit-password";
	}

	// Xử lý form đổi mật khẩu
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam("current-password") String currentPassword,
			@RequestParam("new-password") String newPassword, @RequestParam("confirm-password") String confirmPassword,
			RedirectAttributes redirectAttributes,HttpSession session) {
		try {
			User currentUser = (User) session.getAttribute("currentUser");

			// Kiểm tra mật khẩu cũ có đúng không
	        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
	            redirectAttributes.addFlashAttribute("error", "Mật khẩu cũ không đúng.");
	            return "redirect:/user/updatePassword";
	        }

	        // Kiểm tra mật khẩu mới và xác nhận mật khẩu trùng nhau
	        if (!newPassword.equals(confirmPassword)) {
	            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
	            return "redirect:/user/updatePassword";
	        }

	        // Mã hóa mật khẩu mới trước khi lưu
	        String encodedNewPassword = passwordEncoder.encode(newPassword);

	        // Cập nhật mật khẩu cho người dùng
	        userService.changePassword(currentUser, encodedNewPassword);
	        
	        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	    }
	    return "redirect:/user/updatePassword";
	}

	@GetMapping("/purchaseHistory")
	public String getPurchaseHistoryPage(Model model,
			@RequestParam(defaultValue = "0") int page,HttpSession session) {
		User user = (User) session.getAttribute("currentUser");
        Long userID = user.getId();
// Tạo Pageable cho phân trang (mỗi trang 5 đơn hàng)
		Pageable pageable = PageRequest.of(page, 3, Sort.by("createdAt").descending());

// Lấy các đơn hàng theo trạng thái với phân trang
		model.addAttribute("newOrders", orderService.getOrdersByStatus(OrderStatus.NEW, userID, pageable));
		model.addAttribute("confirmedOrders", orderService.getOrdersByStatus(OrderStatus.CONFIRMED, userID, pageable));
		model.addAttribute("deliveringOrders", orderService.getOrdersByStatus(OrderStatus.SHIPPING, userID, pageable));
		model.addAttribute("deliveredOrders", orderService.getOrdersByStatus(OrderStatus.DELIVERED, userID, pageable));
		model.addAttribute("canceledOrders", orderService.getOrdersByStatus(OrderStatus.CANCELLED, userID, pageable));
		model.addAttribute("returnOrders", orderService.getOrdersByStatus(OrderStatus.REFUNDED, userID, pageable));

		return "user/user-purchase-history";
	}

	@GetMapping("/bookdetail")
	public String getBookDetailPage(@RequestParam Long id, Model model) {
		Book book = bookService.getBookById(id);

		model.addAttribute("book", book);

		return "user/product-detail";
	}

	@GetMapping("/allBooks")
	public String getAllBooks(@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, Model model) {

		// Lấy danh sách các thể loại
		List<Category> categories = categoryService.getAllCategories();
		model.addAttribute("categories", categories);

		// Lấy danh sách sách dựa trên bộ lọc
		Page<Book> books = bookService.getBooks(categoryId, sortBy, sortDir, 1, 10); // Ví dụ phân trang
		model.addAttribute("books", books.getContent());
		model.addAttribute("currentPage", books.getNumber());
		model.addAttribute("totalPages", books.getTotalPages());
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("categoryId", categoryId);

		return "user/all-books";
	}

}
