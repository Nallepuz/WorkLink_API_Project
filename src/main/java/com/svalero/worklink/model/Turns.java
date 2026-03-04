package com.svalero.worklink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "turn")
@Table(name = "turn")

public class Turns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column(name = "work_start")
    private LocalTime workStart;
    @Column(name = "work_end")
    private LocalTime workEnd;
    @Column(name = "color_hex", nullable = false)
    private String colorHex;
    @Column
    private Boolean nights;
    @Column
    private Boolean active;
}
