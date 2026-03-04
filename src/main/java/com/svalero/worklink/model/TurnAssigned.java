package com.svalero.worklink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "turnAssigned")
@Table(name = "turnAssigned")

public class TurnAssigned {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime created;

    // RELACIÓN CON EL USUARIO ASIGNADO AL TURNO
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // RELACIÓN CON EL TURNO (M/T/N)
    @ManyToOne
    @JoinColumn(name = "turn_id")
    private Turns turn;
}
