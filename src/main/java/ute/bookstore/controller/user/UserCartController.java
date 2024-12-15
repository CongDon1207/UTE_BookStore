package ute.bookstore.controller.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ute.bookstore.entity.Address;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Cart;

import ute.bookstore.entity.User;
import ute.bookstore.enums.CheckoutRequest;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.ICartService;
import ute.bookstore.service.IUserService;

@Controller
@RequestMapping("/user/cart")
public class UserCartController {
	  @Autowired
	    private ICartService cartService;
	    
	  	private long userID = 3L;
	  
	    @Autowired
	    private IUserService userService;
	    
	    @Autowired
	    private IBookService bookService;
	    @GetMapping("")
	    public String getCartPage(Model model) {
	        try {
	            

	            // Tìm người dùng hiện tại
	            User currentUser = userService.getUserById(userID);
	            if (currentUser == null) {
	                throw new IllegalArgumentException("Người dùng không tồn tại.");
	            }

	            // Lấy giỏ hàng của người dùng
	            Cart cart = cartService.getCartByUser(currentUser);

	            // Truyền giỏ hàng và danh sách sản phẩm sang view
	            model.addAttribute("cart", cart);
	            model.addAttribute("items", cart.getItems());
	            return "user/cart/show";
	        } catch (Exception e) {
	            model.addAttribute("error", "Không thể tải giỏ hàng: " + e.getMessage());
	            return "user/cart/show";
	        }
	    }

	@PostMapping("/add/{id}")
	public String handleAddBookToCart(@PathVariable Long id, HttpServletRequest request,
	        RedirectAttributes redirectAttributes) {
	    // Lấy thông tin sản phẩm
	    Book book = bookService.getBookById(id);
	    if (book == null) {
	        redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
	        return "redirect:/user";
	    }

	    // Kiểm tra tình trạng còn hàng
	    if (!book.getIsAvailable()) {
	        redirectAttributes.addFlashAttribute("error", "Sản phẩm đã hết hàng!");
	        return "redirect:/user";
	    }

	    // Kiểm tra số lượng tồn kho
	    if (!bookService.checkAvailableQuantity(book, 1)) {
	        redirectAttributes.addFlashAttribute("error", "Không đủ số lượng trong kho!");
	        return "redirect:/user";
	    }

	    try {
	        // Lấy thông tin người dùng
	        User currentUser = userService.getUserById(userID); // Giả định ID người dùng tạm thời

	        // Thêm sản phẩm vào giỏ
	        cartService.addToCart(currentUser, id, 1);

	        redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng!");
	        return "redirect:/user";
	    } catch (RuntimeException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	        return "redirect:/user";
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi không xác định!");
	        return "redirect:/user";
	    }
	}

	 
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<?> addToCart(@RequestParam Long bookId, @RequestParam Integer quantity) {
	    try {
	        User currentUser = userService.getUserById(userID);
	        cartService.addToCart(currentUser, bookId, quantity);
	        return ResponseEntity.ok("Sản phẩm đã được thêm vào giỏ hàng.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}




    
	@PostMapping("/remove/{bookId}")
	public String removeFromCart(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
	    try {
	        User currentUser = userService.getUserById(userID); // Thay thế cách lấy userId cho bảo mật hơn
	        cartService.removeFromCart(currentUser, bookId);
	        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được xóa khỏi giỏ hàng.");
	        return "redirect:/user/cart"; // Chuyển hướng tới trang giỏ hàng sau khi xóa
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thất bại.");
	        return "redirect:/user/cart"; // Chuyển hướng tới trang giỏ hàng khi có lỗi
	    }
	}



    
	@PostMapping("/update/{bookId}")
	public String updateCartItemQuantity(
	        @RequestParam Long bookId,
	        @RequestParam Integer quantity,
	        RedirectAttributes redirectAttributes) {
	    try {
	        // Lấy người dùng hiện tại (có thể lấy userId từ session hoặc frontend)
	        User currentUser = userService.getUserById(userID);  // Thay thế cách lấy userId cho bảo mật hơn
	        
	        // Cập nhật số lượng sản phẩm trong giỏ hàng
	        cartService.updateCartItemQuantity(currentUser, bookId, quantity);
	        
	        // Thêm thông báo cho FlashAttribute
	        redirectAttributes.addFlashAttribute("message", "Cập nhật số lượng thành công.");
	        
	        // Chuyển hướng về trang giỏ hàng
	        return "redirect:/user/cart";
	    } catch (Exception e) {
	        // Thêm thông báo lỗi khi có ngoại lệ
	        redirectAttributes.addFlashAttribute("message", "Cập nhật thất bại.");
	        
	        // Chuyển hướng về trang giỏ hàng khi có lỗi
	        return "redirect:/user/cart";
	    }
	}


    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount() {
        User currentUser = userService.getUserById(userID);
        Cart cart = cartService.getCartByUser(currentUser);
        return ResponseEntity.ok(cart.getItems().size());
    }
    
    @PostMapping("/checkouts")
    public String handleCheckout(@RequestBody CheckoutRequest checkoutRequest, HttpSession session) {
        session.setAttribute("checkoutItems", checkoutRequest.getItems());
        session.setAttribute("totalAmount", checkoutRequest.getTotalAmount());
        return "redirect:/user/cart/checkout";
    }

    
    @GetMapping("/checkout")
    public String getCheckoutCartPage(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CheckoutRequest.CheckoutItem> items = (List<CheckoutRequest.CheckoutItem>) session.getAttribute("checkoutItems");
        Double totalAmount = (Double) session.getAttribute("totalAmount");

        // Thêm danh sách sách được chọn
        List<Book> books = new ArrayList<>();
        if (items != null) {
            for (CheckoutRequest.CheckoutItem item : items) {
                Book book = bookService.getBookById(item.getId()); // Lấy sách từ BookService
                books.add(book);
            }
        }

        model.addAttribute("items", items);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("bookss", books); // Bổ sung danh sách sách 

        User currentUser = userService.getUserById(userID);
        List<Address> addresses = currentUser.getAddresses();
        model.addAttribute("user", currentUser);
        model.addAttribute("addresses", addresses);

        return "user/cart/checkout";
    }


	
}
