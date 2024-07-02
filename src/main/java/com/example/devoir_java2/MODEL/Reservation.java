package com.example.devoir_java2.MODEL;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@Table(name = "reservations")
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Date dateReservation;

    private int nbPlaces;

    @ManyToOne
    @JoinColumn(name = "id_client")
    private User client;

    @ManyToOne
    @JoinColumn(name = "id_trajet")
    private Trajet trajet;


}
