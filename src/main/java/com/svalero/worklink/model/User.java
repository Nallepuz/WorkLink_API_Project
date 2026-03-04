package com.svalero.worklink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, length = 25)
    private String password;
    @Column(nullable = false, length = 9)
    private String phone;
    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime created;
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updated;
    @Column(nullable = false)
    private boolean active;
}
