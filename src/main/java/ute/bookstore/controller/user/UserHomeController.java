package ute.bookstore.controller.user;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ute.bookstore.entity.Address;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category;
import ute.bookstore.entity.User;
import ute.bookstore.service.IAddressService;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.ICategoryService;
import ute.bookstore.service.IUserService;

@Controller
@RequestMapping("/user")
public class UserHomeController {
	@Autowired
	private ICategoryService categoryService;
	 @Autowired
	    private IBookService bookService;
	 @Autowired
	 private IUserService userService;
	  @Autowired
	   private IAddressService addressService;
	
	@GetMapping({"","/home"})
    public String homePage(Model model) {
        // Lấy danh sách sách từ database
        List<Book> featuredBooks = bookService.getFeaturedBooks();
        model.addAttribute("featuredBooks", featuredBooks);
        return "user/user-home";
    }

	@GetMapping("/cart")
	public String getCartPage() {
		return "user/cart/show.html";
	}
	@GetMapping("/shopping")
	public String getShoppingPage() {
		return "user/user-shopping.html";
	}
	// Lấy trang cập nhật thông tin
    @GetMapping("/updateInfo")
    public String getUpdateInfoPage(@RequestParam(value = "id", required = false) Long id, Model model) {
        // Sử dụng ID từ request hoặc mặc định là người dùng đang đăng nhập
        if (id == null) {
            id = 1L; // ID mặc định cho mục đích thử nghiệm, thay bằng logic lấy ID người dùng đang đăng nhập
        }
        User user = userService.getUserById(id);
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
	public String getDeliveryAddressesPage(Model model/* , Principal principal */) {
		 // Lấy thông tin user hiện tại
       // User user = userService.findByUsername(principal.getName());
		 User user = userService.getUserById(1L);
		 // Lấy danh sách địa chỉ của user
        List<Address> addresses = addressService.getAddressesByUserId(user.getId());
        
        int hasDefaultAddress = addresses != null && 
        	    addresses.stream().anyMatch(Address::getIsDefault) ? 1 : 0;
        
        model.addAttribute("user", user);
        model.addAttribute("addresses", addresses);
        model.addAttribute("hasDefaultAddress", hasDefaultAddress);
        // Thêm đối tượng Address rỗng cho form
        model.addAttribute("newAddress", new Address());
        return "user/user-manage-address.html";
		
	}
	
	 @PostMapping("/addresses/add")
		public String addAddress(@ModelAttribute("newAddress") Address address,
				/* Principal principal, */ RedirectAttributes redirectAttributes) {
	        // Lấy user hiện tại
	        //User user = userService.findByUsername(principal.getName());
		 User user = userService.getUserById(1L);

	        // Gán user cho địa chỉ
	        address.setUser(user);

	        // Lưu địa chỉ mới
	        addressService.saveAddress(address);
	        redirectAttributes.addFlashAttribute("message", "Địa chỉ đã được thêm thành công.");
	        return "redirect:/user/manageDeliveryAddresses";
	    }
	 
	 @GetMapping("/addresses/delete/{id}")
	    public String deleteAddress(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		 try {
		        addressService.deleteAddressById(id); // Thực hiện xóa địa chỉ
		        redirectAttributes.addFlashAttribute("message", "Địa chỉ đã được xóa thành công!");
		    } catch (Exception e) {
		        redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa địa chỉ!");
		    }
		    return "redirect:/user/manageDeliveryAddresses"; // Chuyển hướng về trang quản lý địa chỉ
	    }
	 	 
	 @GetMapping("/addresses/edit/{id}")
	 public String showEditAddressForm(@PathVariable("id") Long id, Model model) {
		  Address address = addressService.getAddressById(id);
		    model.addAttribute("address", address);
		    return "redirect:/user/manageDeliveryAddresses";
		 
	 }
	 
	 @PostMapping("/addresses/edit/{id}")
	 public String updateAddress(@PathVariable("id") Long id, 
	                              @ModelAttribute Address address, 
	                              RedirectAttributes redirectAttributes) {
	     try {
	         Address existingAddress = addressService.getAddressById(id);
	         
	         // Update existing address fields
	         existingAddress.setStreet(address.getStreet());
	         existingAddress.setDistrict(address.getDistrict());
	         existingAddress.setCity(address.getCity());
	         existingAddress.setPhone(address.getPhone());
	         existingAddress.setIsDefault(address.getIsDefault());
	         
				/*
				 * // Handle default address logic if (address.getIsDefault()) {
				 * address.setIsDefault(existingAddress.getIsDefault()); }
				 */
	         
	         addressService.saveAddress(existingAddress);
	         redirectAttributes.addFlashAttribute("message", "Địa chỉ đã được cập nhật thành công.");
	     } catch (Exception e) {
	         redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật địa chỉ.");
	     }
	     return "redirect:/user/manageDeliveryAddresses";
	 }
	

	
	// Hiển thị trang đổi mật khẩu
	@GetMapping("/updatePassword")
	public String getUpdatePasswordPage() {
		return "user/user-edit-password";
	}
	
	// Xử lý form đổi mật khẩu
    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam("current-password") String currentPassword,
                                 @RequestParam("new-password") String newPassword,
                                 @RequestParam("confirm-password") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
        	 // Lấy người dùng từ session (hoặc security context)
            User currentUser =  userService.getUserById(1L);
            
            // Gọi service để đổi mật khẩu
            userService.changePassword(currentUser, currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/updatePassword";
    }
	
	@GetMapping("/purchaseHistory")
	public String getPurchaseHistoryPage() {
		return "user/user-purchase-history";
	}
	@GetMapping("/bookdetail")
	public String getBookDetailPage(@RequestParam Long id, Model model) {
	    Book book = bookService.getBookById(id);
	    model.addAttribute("book", book);
	  
	    return "user/product-detail";
	}

	
	@GetMapping("/favoriteBooks")
	public String getFavouriteBookPage() {
		return "user/favourite-books";
	}
	
	@GetMapping("/allBooks")
	public String getAllBooks(
	    @RequestParam(value = "categoryId", required = false) Long categoryId,
	    @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
	    @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
	    Model model) {

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
