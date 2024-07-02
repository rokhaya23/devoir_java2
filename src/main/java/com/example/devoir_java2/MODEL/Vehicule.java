package com.example.devoir_java2.MODEL;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehicules")
@Data
public class Vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String immatriculation;

    private String marque;

    private String modele;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;


}
