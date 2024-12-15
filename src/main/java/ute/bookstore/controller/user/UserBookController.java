package ute.bookstore.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ute.bookstore.entity.FavoriteBook;

import ute.bookstore.entity.User;
import ute.bookstore.service.IFavoriteBookService;
import ute.bookstore.service.IUserService;

@Controller
@RequestMapping("/user/favoriteBooks")
public class UserBookController {
	
	private long userID = 2L;
	
	 @Autowired
	  private IFavoriteBookService favoriteBookService;
	 @Autowired
	 private IUserService userService;
	
	@GetMapping("")
	public String getFavouriteBookPage(Model model) {
		User user = userService.getUserById(userID);
	    List<FavoriteBook> favoriteBooks = user.getFavoriteBooks();
	    model.addAttribute("favoriteBooks", favoriteBooks);
		return "user/favourite-books";
	}
	
	 @PostMapping("/add/{bookId}")
	    @ResponseBody
	    public ResponseEntity<String> addToFavorite(@PathVariable Long bookId) {
	        boolean success = favoriteBookService.addBookToFavorites(bookId);
	        if (success) {
	            return ResponseEntity.ok("Đã thêm vào danh sách yêu thích!");
	        } else {
	            return ResponseEntity.badRequest().body("Thêm thất bại!");
	        }
	    }

	    @PostMapping("/remove/{bookId}")
	    @ResponseBody
	    public ResponseEntity<String> removeFromFavorite(@PathVariable Long bookId) {
	        boolean success = favoriteBookService.removeBookFromFavorites(bookId);
	        if (success) {
	            return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích!");
	        } else {
	            return ResponseEntity.badRequest().body("Xóa thất bại!");
	        }
	    }

	
}
