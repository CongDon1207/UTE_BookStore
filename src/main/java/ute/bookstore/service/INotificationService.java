package ute.bookstore.service;

import ute.bookstore.entity.Notification;
import ute.bookstore.entity.Order;

public interface INotificationService {
    void sendOrderStatusNotification(Order order);
    
    Notification findById(Long id);
    void save(Notification notification);
}