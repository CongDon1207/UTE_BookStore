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
import org.springframework.web.bind.annotation.RequestMapping;
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
    @Autowired private UserRepository userRepository; // Add this
    private static final Long TEMP_USER_ID = 1L;

    @GetMapping
    public String chatPage(Model model) {
        model.addAttribute("userId", TEMP_USER_ID);
        return "seller/chat-messages";
    }

    @GetMapping("/conversations") 
    @ResponseBody
    public List<ChatMessage> getConversations() {
        User sender = userRepository.findById(TEMP_USER_ID).orElseThrow();
        return chatService.getConversations(sender);
    }

    @GetMapping("/messages/{userId}")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable Long userId) {
        User sender = userRepository.findById(TEMP_USER_ID).orElseThrow();
        User receiver = userRepository.findById(userId).orElseThrow();
        return chatService.getChatMessages(sender, receiver);
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        message.setSender(userRepository.findById(TEMP_USER_ID).orElseThrow());
        return chatService.sendMessage(message);
    }
}