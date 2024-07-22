package com.example.devoir_java2.MODEL;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    private String villeDepart;

    private String villeArrivee;

    private LocalDateTime dateReservation;

    private int nbPlaces;

    private String statut;


    @ManyToOne
    @JoinColumn(name = "id_client")
    private User client;

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", villeDepart='" + villeDepart + '\'' +
                ", villeArrivee='" + villeArrivee + '\'' +
                ", dateReservation=" + dateReservation +
                ", nbPlaces=" + nbPlaces +
                ", statut='" + statut + '\'' +
                ", client=" + client +
                '}';
    }
}
