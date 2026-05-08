package com.svalero.worklink.service;

import com.svalero.worklink.Dto.NotificationOutDto;
import com.svalero.worklink.model.Notification;
import com.svalero.worklink.model.User;
import com.svalero.worklink.repository.NotificationRepository;
import com.svalero.worklink.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<NotificationOutDto> findByUser(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
        return modelMapper.map(notifications, new TypeToken<List<NotificationOutDto>>() {}.getType());
    }

    public List<NotificationOutDto> findUnreadByUser(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        return modelMapper.map(notifications, new TypeToken<List<NotificationOutDto>>() {}.getType());
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public void createNotification(Long userId, String message) {
        createNotificationWithType(userId, message, "PERSONAL");
    }

    public void createNotificationWithType(Long userId, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setType(type);
        notificationRepository.save(notification);
    }

    public void createAnnouncement(String message) {
        List<User> allUsers = userRepository.findAll();
        allUsers.forEach(user -> {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(message);
            notification.setRead(false);
            notification.setType("ANNOUNCEMENT");
            notificationRepository.save(notification);
        });
    }

    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }

    public void deleteAllRead(Long userId) {
        List<Notification> read = notificationRepository.findByUserIdAndReadTrue(userId);
        notificationRepository.deleteAll(read);
    }
}