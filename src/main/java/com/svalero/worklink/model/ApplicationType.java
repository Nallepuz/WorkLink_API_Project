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
@Entity(name = "applicationType")
@Table(name = "applicationType")

public class ApplicationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column
    private String description;
    @Column(name = "affects_balance", nullable = false)
    private boolean affectsBalance;
    @Column(nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime created;
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updated;
}
