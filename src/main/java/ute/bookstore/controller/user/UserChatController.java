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

import jakarta.servlet.http.HttpSession;
import ute.bookstore.entity.ChatMessage;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.repository.ShopRepository;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.IChatService;

@Controller
@RequestMapping("/user/chat")
public class UserChatController {
    @Autowired 
    private IChatService chatService;
    
    @Autowired 
    private UserRepository userRepository;
    
    @Autowired 
    private ShopRepository shopRepository;

    @GetMapping("/{shopId}") 
    public String chatPage(@PathVariable Long shopId, Model model, HttpSession session) {
        // Lấy user từ session
        User currentUser = (User) session.getAttribute("currentUser");
        
        // Kiểm tra user đã đăng nhập chưa
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new RuntimeException("Shop not found"));

        // Lấy tin nhắn chat
        List<ChatMessage> messages = chatService.getChatMessages(currentUser, shop.getUser());
        
        model.addAttribute("messages", messages);
        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("shopId", shopId);
        
        return "user/chat-messages";
    }

    @PostMapping("/send")
    public String sendMessage(
        @RequestParam String content, 
        @RequestParam Long shopId,
        HttpSession session
    ) {
        // Lấy thông tin user từ session 
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new RuntimeException("Shop not found"));

        ChatMessage message = new ChatMessage();
        message.setContent(content);
        message.setSender(currentUser);
        message.setReceiver(shop.getUser());
        
        chatService.sendMessage(message);

        return "redirect:/user/chat/" + shopId;
    }
}
