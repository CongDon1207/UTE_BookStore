package ute.bookstore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

	// Thêm vào ChatRepository
	List<ChatMessage> findBySenderAndReceiverOrReceiverAndSenderOrderBySentAtAsc(User sender, User receiver,
			User receiver2, User sender2);

	@Query("SELECT DISTINCT c FROM ChatMessage c WHERE c.sender = ?1 OR c.receiver = ?1 ORDER BY c.sentAt DESC")  
	List<ChatMessage> findDistinctBySenderOrReceiverOrderBySentAtDesc(User user);
}
