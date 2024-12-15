package ute.bookstore.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ute.bookstore.entity.Book;
import ute.bookstore.service.IBookService;



@Controller
public class HomeController {
	@Autowired
	IBookService bookService;

    @GetMapping("/home")
    public String Home(Model model) {
			List<Map<String, Object>> topBooks = bookService.getTop10BestSellingBooks();
			for (Map<String, Object> book : topBooks) {
			    System.out.println("Book ID: " + book.get("id"));
			    System.out.println("Title: " + book.get("title"));
			    System.out.println("Price: " + book.get("price"));
			    System.out.println("Category ID: " + book.get("category_id"));
			    System.out.println("Total Sold: " + book.get("totalSold"));
			    System.out.println("Image URL: " + book.get("imageUrl"));
			    System.out.println("-----------------------------");
			}
            model.addAttribute("topBooks", topBooks);        
        return "/auth/index" ;
    }
    @GetMapping("/login")
    public String login() {
        return "auth/login" ;
    }
    @GetMapping("/books/detail/{id}")
    public String showBookDetail(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id); // Lấy thông tin sách theo id
        model.addAttribute("book", book); // Thêm thông tin sách vào model
        return "/auth/book-detail"; // Tên của view chi tiết sách
    }
}
