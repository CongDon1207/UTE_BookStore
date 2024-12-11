package ute.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ute.bookstore.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Các truy vấn tuỳ chỉnh nếu cần
}
