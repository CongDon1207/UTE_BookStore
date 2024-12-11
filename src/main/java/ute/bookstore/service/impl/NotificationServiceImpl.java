package ute.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ute.bookstore.service.INotificationService;
import ute.bookstore.entity.Notification;
import ute.bookstore.entity.Order;
import ute.bookstore.repository.NotificationRepository;

@Service
public class NotificationServiceImpl implements INotificationService {
	 @Autowired
	    private NotificationRepository notificationRepository;
    @Override
    public void sendOrderStatusNotification(Order order) {
        // Implement notification logic here
    }
    
    // Tìm thông báo theo ID
    @Override
    public Notification findById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    // Lưu thông báo vào cơ sở dữ liệu
    @Override
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }
}
