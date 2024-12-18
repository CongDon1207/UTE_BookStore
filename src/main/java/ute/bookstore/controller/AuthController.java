package ute.bookstore.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpSession;

import ute.bookstore.dto.RegisterRequest;
import ute.bookstore.entity.Author;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.BookAuthor;
import ute.bookstore.entity.Category;
import ute.bookstore.entity.User;
import ute.bookstore.entity.Shop;
import ute.bookstore.enums.UserRole;
import ute.bookstore.repository.AuthorRepository;
import ute.bookstore.repository.BookAuthorRepository;
import ute.bookstore.repository.BookRepository;
import ute.bookstore.service.AuthService;
import ute.bookstore.service.AuthorService;
import ute.bookstore.service.EmailService;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.ICategoryService;
import ute.bookstore.service.IShopService;
import ute.bookstore.service.IUserService;
import ute.bookstore.service.OtpService;
import ute.bookstore.service.bookAuthorService;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final EmailService emailService;
	private final OtpService otpService;

	@Autowired
    private IUserService userService;
	@Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private bookAuthorService bookAuthorService;
    @Autowired
    private IShopService shopService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IBookService bookService;
    @GetMapping("/login")
    public String login() {
        return "/auth/login" ;
    }
	// Hiển thị trang đăng ký
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("registerRequest", new RegisterRequest());
		return "/auth/register"; // Trả về trang đăng ký Thymeleaf
	}
	@GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "/auth/forgot-password"; // Tên file HTML của bạn (không cần phần mở rộng .html)
    }
	// Endpoint đăng ký tài khoản
	@PostMapping("/register")
	public String register(@ModelAttribute RegisterRequest registerRequest, Model model) {
	    // Kiểm tra email đã tồn tại
	    if (authService.isEmailExist(registerRequest.getEmail())) {
	        model.addAttribute("error", "Email đã tồn tại.");
	        return "/auth/register";
	    }

	    // Tạo tài khoản mới với trạng thái chưa kích hoạt
	    User user = authService.createInactiveUser(registerRequest);

	    // Tạo và gửi OTP qua email
	    String otp = emailService.generateOtp();
	    authService.saveOtpForUser(user, otp);
	    emailService.sendOtpToEmail(user.getEmail(), otp);

	    model.addAttribute("message", "OTP đã được gửi vào email của bạn.");
	    model.addAttribute("email", user.getEmail());
	    return "/auth/verifyOtp"; // Chuyển đến trang nhập OTP
	}


	// Endpoint xác thực OTP
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
	    if (authService.verifyOtp(email, otp)) {
	        model.addAttribute("message", "Tài khoản đã được kích hoạt thành công. Vui lòng đăng nhập.");
	        return "/auth/login"; // Chuyển đến trang đăng nhập
	    } else {
	        model.addAttribute("error", "OTP không đúng hoặc đã hết hạn.");
	        return "/auth/verifyOtp"; // Quay lại trang nhập OTP với thông báo lỗi
	    }
	}



    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {
        // Find the user by email
        User user = userService.findByEmail(email);
        
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Invalid email or password");
            return "/auth/login"; // Show login page with error
        }

        // Check if the user is active
        if (!user.getIsActive()) {
            model.addAttribute("error", "Account is inactive. Please contact support.");
            return "/auth/login";
        }

        // Store user information in session
        session.setAttribute("currentUser", user);

        // Redirect based on role
        if (user.getRole() == UserRole.ADMIN) {
            return "redirect:/admin";
        } else if (user.getRole() == UserRole.USER) {
            return "redirect:/user/home";
        } else if (user.getRole() == UserRole.VENDOR) {
            return "redirect:/seller/home";
        } else if (user.getRole() == UserRole.SHIPPER) {
            return "redirect:/shipper/home";
        } else {
            return "redirect:/home";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clear the session
        return "redirect:/home"; // Redirect to login page
    }

	
	// Xử lý yêu cầu gửi OTP
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, Model model) {
        if (!authService.isEmailExist(email)) {
            model.addAttribute("error", "Email không tồn tại.");
            return "/auth/forgot-password"; // Quay lại trang quên mật khẩu với thông báo lỗi
        }

        // Gửi OTP qua email
        String otp = emailService.generateOtp();
        otpService.storeOtp(email, otp);
        emailService.sendOtpToEmail(email, otp);

        model.addAttribute("message", "OTP đã được gửi đến email của bạn.");
        return "/auth/reset-password"; // Chuyển sang trang đặt lại mật khẩu
    }

    // Xử lý yêu cầu đặt lại mật khẩu
    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String email, 
                                       @RequestParam String otp, 
                                       @RequestParam String newPassword, 
                                       Model model) {
        if (!otpService.verifyOtp(email, otp)) {
            model.addAttribute("error", "OTP không hợp lệ hoặc đã hết hạn.");
            return "/auth/reset-password"; // Quay lại trang đặt lại mật khẩu với thông báo lỗi
        }

        // Cập nhật mật khẩu
        authService.updatePassword(email, newPassword);
        otpService.clearOtp(email);

        model.addAttribute("message", "Mật khẩu đã được đặt lại thành công.");
        return "/auth/login"; // Quay lại trang đăng nhập
    }

    @GetMapping("/authors")
    public String getAllAuthors(Model model) {
        List<Author> authors = authorService.findAll();
        model.addAttribute("authors", authors); // Thêm danh sách tác giả vào model
        return "/auth/authors"; // Trả về tên view (authors.html)
    }
    @GetMapping("/authors/{authorId}/books")
    public String getBooksByAuthor(@PathVariable Long authorId, Model model) {
        // Lấy thông tin tác giả
        Author author = authorService.findById(authorId);
        if (author == null) {
            model.addAttribute("error", "Tác giả không tồn tại.");
            return "error";
        }

        // Lấy danh sách sách của tác giả này
        List<BookAuthor> bookAuthors = bookAuthorService.findByAuthorId(authorId);
        List<Book> books = bookAuthors.stream()
                                      .map(BookAuthor::getBook)
                                      .toList();

        model.addAttribute("author", author);
        model.addAttribute("books", books);

        return "/auth/author-books";  // View để hiển thị các sách của tác giả
    }
    @GetMapping("/shops")
    public String getAllShops(Model model) {
        List<Shop> shops = shopService.findAll();
        model.addAttribute("shops", shops); // Thêm danh sách tác giả vào model
        return "auth/shop-list"; // Trả về tên view (authors.html)
    }
    @GetMapping("/shop/{shopid}/details")
    public String getShopDetails(@PathVariable("shopid") Long shopId, Model model) {
        // Retrieve shop by id
        Shop shop = shopService.getShopById(shopId);

        if (shop == null) {
            model.addAttribute("errorMessage", "Không tìm thấy shop này.");
            return "error"; // Redirect to an error page if the shop doesn't exist
        }

        // Pass shop and its books to the view
        model.addAttribute("shop", shop);
        model.addAttribute("books", shop.getBooks());
        return "/auth/shop-details"; // Name of the Thymeleaf template for shop details
    }
    @GetMapping("/categories")
    public String getAllCategories(Model model) {
        List<Category> cates = categoryService.getAllCategories();
        model.addAttribute("cates", cates); // Thêm danh sách tác giả vào model
        return "/auth/category-list"; // Trả về tên view (authors.html)
    }
    @GetMapping("/category/{cateid}/details")
    public String getCategoryDetails(@PathVariable("cateid") Long cateId, Model model) {
        // Retrieve shop by id
        Category cate = categoryService.getCategoryById(cateId);
        
        if (cate == null) {
            model.addAttribute("errorMessage", "Không tìm thấy shop này.");
            return "error"; // Redirect to an error page if the shop doesn't exist
        }

        // Pass shop and its books to the view
        model.addAttribute("cate", cate);
        model.addAttribute("books", cate.getBooks());
        return "/auth/category-details"; // Name of the Thymeleaf template for shop details
    }
}
