package ute.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ute.bookstore.entity.ChatMessage;
import ute.bookstore.entity.User; 
import ute.bookstore.repository.ChatRepository;
import ute.bookstore.service.IChatService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
@Transactional
public class ChatServiceImpl implements IChatService {
    @Autowired private ChatRepository chatRepository;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    private Map<String, User> activeUsers = new ConcurrentHashMap<>();

    @Override
    public List<ChatMessage> getConversations(User user) {
        List<ChatMessage> messages = chatRepository.findDistinctBySenderOrReceiverOrderBySentAtDesc(user);
        Map<Long, ChatMessage> latestMessages = new HashMap<>();
        
        for (ChatMessage msg : messages) {
            Long otherUserId;
            if (msg.getSender().getId().equals(user.getId())) {
                otherUserId = msg.getReceiver().getId();
            } else {
                otherUserId = msg.getSender().getId();
            }
            
            if (!latestMessages.containsKey(otherUserId) || 
                msg.getSentAt().isAfter(latestMessages.get(otherUserId).getSentAt())) {
                latestMessages.put(otherUserId, msg);
            }
        }
        
        return new ArrayList<>(latestMessages.values());
    }

    @Override
    public List<ChatMessage> getChatMessages(User sender, User receiver) {
        // Lấy tin nhắn 2 chiều và sắp xếp theo thời gian
        return chatRepository.findBySenderAndReceiverOrReceiverAndSenderOrderBySentAtAsc(sender, receiver, sender, receiver);
    }

    @Override
    @Transactional
    public ChatMessage sendMessage(ChatMessage message) {
       message.setSentAt(LocalDateTime.now());
       message = chatRepository.save(message);
       messagingTemplate.convertAndSend("/topic/messages/" + message.getReceiver().getId(), message);
       return message;
    }

    @Override
    public List<User> getActiveUsers() {
        return new ArrayList<>(activeUsers.values());
    }

    @Override
    public void handleUserConnection(String sessionId, User user) {
        activeUsers.put(sessionId, user);
        messagingTemplate.convertAndSend("/topic/active", getActiveUsers());
    }

    @Override
    public void handleUserDisconnection(String sessionId) {
        activeUsers.remove(sessionId);
        messagingTemplate.convertAndSend("/topic/active", getActiveUsers());
    }
}
