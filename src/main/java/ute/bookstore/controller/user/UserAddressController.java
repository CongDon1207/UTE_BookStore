package ute.bookstore.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import ute.bookstore.entity.Address;
import ute.bookstore.entity.User;
import ute.bookstore.service.IAddressService;

import ute.bookstore.service.IUserService;

@Controller
@RequestMapping("/user/addresses")
public class UserAddressController {
	
	
	 @Autowired
	 private IUserService userService;
	  @Autowired
	   private IAddressService addressService;
	@PostMapping("/add")
	public String addAddress(@ModelAttribute("newAddress") Address address,
			/* Principal principal, */ RedirectAttributes redirectAttributes, HttpSession session) {
        // Lấy user hiện tại
        //User user = userService.findByUsername(principal.getName());
		User user = (User) session.getAttribute("currentUser");


        // Gán user cho địa chỉ
        address.setUser(user);

        // Lưu địa chỉ mới
        addressService.saveAddress(address);
        redirectAttributes.addFlashAttribute("message", "Địa chỉ đã được thêm thành công.");
        return "redirect:/user/manageDeliveryAddresses";
    }
 
 @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
	 try {
	        addressService.deleteAddressById(id); // Thực hiện xóa địa chỉ
	        redirectAttributes.addFlashAttribute("message", "Địa chỉ đã được xóa thành công!");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa địa chỉ!");
	    }
	    return "redirect:/user/manageDeliveryAddresses"; // Chuyển hướng về trang quản lý địa chỉ
    }
 	 
 @GetMapping("/edit/{id}")
 public String showEditAddressForm(@PathVariable("id") Long id, Model model) {
	  Address address = addressService.getAddressById(id);
	    model.addAttribute("address", address);
	    return "redirect:/user/manageDeliveryAddresses";
	 
 }
 
 @PostMapping("/edit/{id}")
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


	
}
