package ute.bookstore.controller.admin;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ute.bookstore.entity.*;
import ute.bookstore.service.admin.AdminBookManagementService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/product-management")
@RequiredArgsConstructor
public class AdminBookManagementController {
    private final AdminBookManagementService adminBookManagementService;

    // Hiển thị trang quản lý sản phẩm
    @GetMapping
    public String showProductManagement(Model model, Pageable pageable) {
        Page<Book> books = adminBookManagementService.getAllBooks(pageable);
        List<Category> categories = adminBookManagementService.getAllCategories();
        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        return "admin/product/product-management";
    }

    // Category Management
    @GetMapping("/categories")
    public String showCategories(Model model) {
        model.addAttribute("categories", adminBookManagementService.getAllCategories());
        return "admin/categories";
    }

    @PostMapping("/categories/create")
    public String createCategory(@ModelAttribute Category category) {
        adminBookManagementService.createCategory(category);
        return "redirect:/admin/product-management/categories";
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category) {
        adminBookManagementService.updateCategory(id, category);
        return "redirect:/admin/product-management/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        adminBookManagementService.deleteCategory(id);
        return "redirect:/admin/product-management/categories";
    }

    // Book Management
    @GetMapping("/books")
    public String showBooks(Model model, Pageable pageable) {
        model.addAttribute("books", adminBookManagementService.getAllBooks(pageable));
        return "admin/books";
    }

    @PostMapping("/books/status/{id}")
    public String updateBookStatus(@PathVariable Long id, @RequestParam Boolean isAvailable) {
        adminBookManagementService.updateBookStatus(id, isAvailable);
        return "redirect:/admin/product-management/books";
    }

    @GetMapping("/books/category/{categoryId}")
    public String getBooksByCategory(@PathVariable Long categoryId, Model model, Pageable pageable) {
        model.addAttribute("books", adminBookManagementService.getBooksByCategory(categoryId, pageable));
        return "admin/books";
    }

    @PostMapping("/books/update/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book) {
        adminBookManagementService.updateBook(id, book);
        return "redirect:/admin/product-management/books";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        adminBookManagementService.deleteBook(id);
        return "redirect:/admin/product-management/books";
    }

    // Ajax endpoints for async operations
    @GetMapping("/api/books")
    @ResponseBody
    public Page<Book> getBooksList(Pageable pageable) {
        return adminBookManagementService.getAllBooks(pageable);
    }

    @GetMapping("/api/categories")
    @ResponseBody
    public List<Category> getCategoriesList() {
        return adminBookManagementService.getAllCategories();
    }
}