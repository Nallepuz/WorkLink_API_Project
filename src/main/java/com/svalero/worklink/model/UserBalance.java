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
@Entity(name = "user_balance")
@Table(name = "user_balance")

public class UserBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @Column(nullable = false)
    private int year; // Año del balance (2024, 2025...)
    @Column(name = "vacation_days", nullable = false)
    private int vacationDays; // Días de vacaciones disponibles
    @Column(name = "excess_days", nullable = false)
    private int excessDays; // Días de exceso disponibles
    @Column(name = "unpaid_days", nullable = false)
    private int unpaidDays; // Días no retribuidos disponibles (o puedes dejarlo sin límite)
    @Column(name = "hours_balance", nullable = false)
    private float hoursBalance; // Bolsa de horas disponibles

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime created;
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updated;
}
