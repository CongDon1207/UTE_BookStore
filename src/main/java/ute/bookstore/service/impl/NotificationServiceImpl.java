package ute.bookstore.service.impl;

import org.springframework.stereotype.Service;
import ute.bookstore.service.INotificationService;
import ute.bookstore.entity.Order;

@Service
public class NotificationServiceImpl implements INotificationService {
    @Override
    public void sendOrderStatusNotification(Order order) {
        // Implement notification logic here
    }
}
