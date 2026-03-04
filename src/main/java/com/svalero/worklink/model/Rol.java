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
@Entity(name = "rol")
@Table(name = "rol")

public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column(name="access_level")
    private float accessLevel;
    @Column
    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime created;
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updated;
}
