package ute.bookstore.controller.seller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import ute.bookstore.entity.ChatMessage;
import ute.bookstore.entity.User;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.IChatService;
import java.util.List;
@Controller
@RequestMapping("/seller/chat")
public class SellerChatController {
   @Autowired private IChatService chatService;
   @Autowired private UserRepository userRepository;
   private static final Long TEMP_USER_ID = 1L;

   @GetMapping
   public String chatPage(Model model) {
       User sender = userRepository.findById(TEMP_USER_ID).orElseThrow();
       model.addAttribute("conversations", chatService.getConversations(sender));
       model.addAttribute("userId", TEMP_USER_ID);
       return "seller/chat-messages";
   }

   @GetMapping("/{userId}")
   public String getMessages(@PathVariable Long userId, Model model) {
       User sender = userRepository.findById(TEMP_USER_ID).orElseThrow();
       User receiver = userRepository.findById(userId).orElseThrow();
       model.addAttribute("conversations", chatService.getConversations(sender));
       model.addAttribute("messages", chatService.getChatMessages(sender, receiver));
       model.addAttribute("selectedUser", userId);
       model.addAttribute("userId", TEMP_USER_ID);
       return "seller/chat-messages";
   }

   @PostMapping("/send/{userId}")
   public String sendMessage(@PathVariable Long userId, @RequestParam String content) {
       ChatMessage message = new ChatMessage();
       message.setContent(content);
       message.setSender(userRepository.findById(TEMP_USER_ID).orElseThrow());
       message.setReceiver(userRepository.findById(userId).orElseThrow());
       chatService.sendMessage(message);
       return "redirect:/seller/chat/" + userId;
   }
   
}