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

    private int nbres_place;

    @ManyToOne
    @JoinColumn(name = "chauffeur_id")
    private User chauffeur; // Ajout de la relation avec l'utilisateur (chauffeur)

    public User getChauffeur() {
        return chauffeur;
    }

    public void setChauffeur(User chauffeur) {
        this.chauffeur = chauffeur;
    }

}
