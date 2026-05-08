package com.svalero.worklink.controller;

import com.svalero.worklink.Dto.NotificationOutDto;
import com.svalero.worklink.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationOutDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.findByUser(userId));
    }

    @GetMapping("/notifications/{userId}/unread")
    public ResponseEntity<List<NotificationOutDto>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.findUnreadByUser(userId));
    }

    @GetMapping("/notifications/{userId}/count")
    public ResponseEntity<Map<String, Long>> countUnread(@PathVariable Long userId) {
        long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(Map.of("unread", count));
    }

    @PutMapping("/notifications/{userId}/read")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notifications/announcement")
    public ResponseEntity<Void> createAnnouncement(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        notificationService.createAnnouncement(message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/notifications/single/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        notificationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/notifications/{userId}/read")
    public ResponseEntity<Void> deleteAllRead(@PathVariable Long userId) {
        notificationService.deleteAllRead(userId);
        return ResponseEntity.noContent().build();
    }
}