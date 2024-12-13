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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ute.bookstore.entity.ChatMessage;
import ute.bookstore.entity.User;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.IChatService;

@Controller
@RequestMapping("/user/chat")
public class UserChatController {
    @Autowired private IChatService chatService;
    @Autowired private UserRepository userRepository;
    private static final Long TEMP_USER_ID = 2L; // Temporary user ID

    @GetMapping("/{shopId}")
    public String chatPage(@PathVariable Long shopId, Model model) {
        model.addAttribute("userId", TEMP_USER_ID);
        model.addAttribute("shopId", shopId);
        return "user/chat-messages";
    }

    @GetMapping("/messages/{shopId}")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable Long shopId) {
        User user = userRepository.findById(TEMP_USER_ID).orElseThrow();
        User shop = userRepository.findById(shopId).orElseThrow();
        return chatService.getChatMessages(user, shop);
    }

    @MessageMapping("/chat.send.user")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        message.setSender(userRepository.findById(TEMP_USER_ID).orElseThrow());
        return chatService.sendMessage(message);
    }
}
