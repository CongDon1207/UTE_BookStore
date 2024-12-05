package ute.bookstore.service;

import ute.bookstore.entity.Order;

public interface INotificationService {
    void sendOrderStatusNotification(Order order);
}