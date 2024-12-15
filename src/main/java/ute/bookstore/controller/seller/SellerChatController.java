package ute.bookstore.controller.seller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ute.bookstore.entity.ChatMessage;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.IChatService;
import ute.bookstore.service.IShopService;
@Controller
@RequestMapping("/seller/chat")
public class SellerChatController {
   @Autowired private IChatService chatService;
   @Autowired private UserRepository userRepository; 
   @Autowired private IShopService shopService;

   @ModelAttribute
   public void addAttributes(Model model, HttpServletRequest request, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser != null) {
           Shop shop = shopService.getShopByUserEmail(currentUser.getEmail());
           model.addAttribute("shop", shop != null ? shop : new Shop());
           model.addAttribute("user", currentUser);
       }
       model.addAttribute("requestURI", request.getRequestURI());
   }

   @GetMapping
   public String chatPage(Model model, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) {
           return "redirect:/auth/login";
       }
       
       model.addAttribute("conversations", chatService.getConversations(currentUser));
       model.addAttribute("userId", currentUser.getId());
       return "seller/chat-messages";
   }

   @GetMapping("/{userId}")
   public String getMessages(@PathVariable Long userId, Model model, HttpSession session) {
       User currentUser = (User) session.getAttribute("currentUser");
       if (currentUser == null) {
           return "redirect:/auth/login";
       }

       User receiver = userRepository.findById(userId).orElseThrow();
       model.addAttribute("conversations", chatService.getConversations(currentUser));
       model.addAttribute("messages", chatService.getChatMessages(currentUser, receiver));
       model.addAttribute("selectedUser", userId);
       model.addAttribute("userId", currentUser.getId());
       return "seller/chat-messages";
   }

   @PostMapping("/send/{userId}")
   public String sendMessage(@PathVariable Long userId, @RequestParam String content, HttpSession session) {
       // Thay vì lấy User từ session, lấy lại từ repository
       User currentUser = userRepository.findById(((User)session.getAttribute("currentUser")).getId())
                                      .orElseThrow();
       
       ChatMessage message = new ChatMessage();
       message.setContent(content);
       message.setSender(currentUser);
       message.setReceiver(userRepository.findById(userId).orElseThrow());
       chatService.sendMessage(message);
       return "redirect:/seller/chat/" + userId;
   }
}