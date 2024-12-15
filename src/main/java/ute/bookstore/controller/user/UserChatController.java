package ute.bookstore.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ute.bookstore.entity.ChatMessage;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.repository.ShopRepository;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.IChatService;

@Controller
@RequestMapping("/user/chat")
public class UserChatController {
   @Autowired private IChatService chatService;
   @Autowired private UserRepository userRepository;
   @Autowired private ShopRepository shopRepository;
   private static final Long TEMP_USER_ID = 3L;

   @GetMapping("/{shopId}") 
   public String chatPage(@PathVariable Long shopId, Model model) {
       User user = userRepository.findById(TEMP_USER_ID).orElseThrow();
       Shop shop = shopRepository.findById(shopId).orElseThrow();
       model.addAttribute("messages", chatService.getChatMessages(user, shop.getUser()));
       model.addAttribute("userId", TEMP_USER_ID);
       model.addAttribute("shopId", shopId);
       return "user/chat-messages";
   }

   @PostMapping("/send")
   public String sendMessage(@RequestParam String content, @RequestParam Long shopId) {
       ChatMessage message = new ChatMessage();
       message.setContent(content);
       message.setSender(userRepository.findById(TEMP_USER_ID).orElseThrow());
       Shop shop = shopRepository.findById(shopId).orElseThrow();
       message.setReceiver(shop.getUser());
       chatService.sendMessage(message);
       return "redirect:/user/chat/" + shopId;
   }
}

