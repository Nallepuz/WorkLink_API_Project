package com.svalero.worklink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "application")
@Table(name = "application")


public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;
    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime created;
    @Column(name = "resolved_date")
    private LocalDateTime resolved;

    // CAMPOS PARA SOLICITUDES DE DÍAS (vacaciones, exceso, no retribuido)
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;

    // CAMPOS PARA SOLICITUDES DE HORAS (bolsa de horas)
    @Column(name = "hours_requested")
    private Float hoursRequested;
    @Column(name = "date")
    private LocalDate date; // Día que pide las horas
    @Column(name = "from_time")
    private LocalTime fromTime;
    @Column(name = "to_time")
    private LocalTime toTime;

    // CAMPOS PARA CAMBIO DE TURNO
    @ManyToOne
    @JoinColumn(name = "affected_user_id", nullable = true)
    private User affectedUser;

    @ManyToOne
    @JoinColumn(name = "turn_give_id", nullable = true)
    private Turns turnGive;

    @ManyToOne
    @JoinColumn(name = "turn_receive_id", nullable = true)
    private Turns turnReceive;

    // COMENTARIOS
    @Column(length = 500)
    private String comments; // Comentarios del solicitante

    @Column(name = "resolver_comments", length = 500)
    private String resolverComments; // Comentarios del que aprueba/rechaza

    // RELACIONES
    @ManyToOne
    @JoinColumn(name = "application_type_id", nullable = false)
    private ApplicationType applicationType;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Usuario solicitante
    @ManyToOne
    @JoinColumn(name = "resolver_id")
    private User resolver; // Usuario que aprueba/rechaza
}
