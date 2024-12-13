package ute.bookstore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ute.bookstore.entity.ChatMessage;
import ute.bookstore.entity.User;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByReceiverIdOrSenderIdOrderBySentAtDesc(Long receiverId, Long senderId);
    List<ChatMessage> findBySenderIdAndReceiverIdOrderBySentAtAsc(Long senderId, Long receiverId);
    Page<ChatMessage> findByReceiverIdOrSenderIdOrderBySentAtDesc(Long receiverId, Long senderId, Pageable pageable);
    List<ChatMessage> findByReceiverOrSenderOrderBySentAtDesc(User receiver, User sender);
    List<ChatMessage> findBySenderAndReceiverOrderBySentAtAsc(User sender, User receiver);
}
