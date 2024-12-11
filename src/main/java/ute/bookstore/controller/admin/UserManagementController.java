package ute.bookstore.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.Valid;
import ute.bookstore.entity.User;
import ute.bookstore.entity.UserActivityLog;
import ute.bookstore.enums.UserRole;
import ute.bookstore.service.impl.UserServiceImpl;
import ute.bookstore.service.impl.admin.UserExportService;

@Controller
@RequestMapping("/admin/user-management")
public class UserManagementController {

	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private UserExportService userExportService;

	@GetMapping
	public String getUserManagementPage(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {

		Page<User> users = userService.getAllUsers(page, size, search);
		model.addAttribute("users", users);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", users.getTotalPages());
		model.addAttribute("search", search);

		return "admin/user/index";
	}

	@GetMapping("/create")
	public String getCreateUserPage(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("roles", UserRole.values());
		return "admin/user/create";
	}

	@PostMapping("/create")
	public String createUser(@Valid @ModelAttribute User user, BindingResult result) {
		if (result.hasErrors()) {
			return "admin/user/create";
		}
		userService.createUser(user);
		return "redirect:/admin/user-management";
	}

	@GetMapping("/edit/{id}")
	public String getEditUserPage(@PathVariable Long id, Model model) {
	    User user = userService.getUserById(id);
	    model.addAttribute("user", user);
	    model.addAttribute("roles", UserRole.values());
	    return "admin/user/edit";
	}

	@PostMapping("/edit/{id}")
	public String updateUser(@PathVariable Long id, 
	                        @Valid @ModelAttribute User userDetails, 
	                        BindingResult result) {
	    if (result.hasErrors()) {
	        return "admin/user/edit";
	    }
	    
	    userService.updateUser(id, userDetails);
	    return "redirect:/admin/user-management";
	}

	@PostMapping("/toggle-status/{id}")
	@ResponseBody
	public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
		boolean newStatus = userService.toggleUserStatus(id);
		return ResponseEntity.ok(Map.of("status", newStatus));
	}
	
	@GetMapping("/export")
	public ResponseEntity<Resource> exportUsers() {
	    String filename = "users_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
	    
	    InputStreamResource file = new InputStreamResource(userExportService.exportToExcel());
	    
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
	        .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
	        .body(file);
	}
	
	@GetMapping("/detail/{id}")
	public ResponseEntity<Map<String, Object>> getUserDetail(@PathVariable Long id) {
	    User user = userService.getUserById(id);
	    List<UserActivityLog> activities = userService.getUserActivities(id, 0, 5); // 5 hoạt động gần nhất
	    
	    Map<String, Object> response = new HashMap<>();
	    response.put("user", user);
	    response.put("activities", activities);
	    
	    return ResponseEntity.ok(response);
	}
}
