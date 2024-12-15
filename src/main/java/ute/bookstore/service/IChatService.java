package ute.bookstore.service;

import java.util.List;

import ute.bookstore.entity.ChatMessage;
import ute.bookstore.entity.User;

public interface IChatService {
	List<ChatMessage> getConversations(User user);
    List<ChatMessage> getChatMessages(User sender, User receiver);
    ChatMessage sendMessage(ChatMessage message);
    List<User> getActiveUsers();
    void handleUserConnection(String sessionId, User user);
    void handleUserDisconnection(String sessionId);
}