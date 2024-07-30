package com.example.devoir_java2.Repository;

import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.JPAUTIL;
import com.example.devoir_java2.MODEL.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ReservationRepository {

    private EntityManagerFactory entityManagerFactory;

    public ReservationRepository() {
        this.entityManagerFactory = JPAUTIL.getEntityManagerFactory();
    }

    public void addReservation(Reservation reservation) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(reservation);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public List<Reservation> getAllReservations() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // Assurez-vous que la colonne "client_id" ou le champ associé est correct
            TypedQuery<Reservation> query = entityManager.createQuery(
                    "SELECT r FROM Reservation r WHERE r.client.id = :userId", Reservation.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }
    public Long countReservation(){
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Long resservation = entityManager.createQuery("select count(u) from Reservation u", Long.class).getSingleResult();
        entityManager.getTransaction().commit();
        entityManager.close();
        return resservation;
    }

    public void updateReservation(Reservation reservation) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();

            // Rechercher la réservation existante dans la base de données
            Reservation reservationBd = entityManager.find(Reservation.class, reservation.getId());
            if (reservationBd != null) {
                // Mettre à jour les propriétés de la réservation
                reservationBd.setDateReservation(reservation.getDateReservation());
                reservationBd.setNbPlaces(reservation.getNbPlaces());
                reservationBd.setVilleArrivee(reservation.getVilleArrivee());
                reservationBd.setVilleDepart(reservation.getVilleDepart());
                reservationBd.setClient(reservation.getClient());
                reservationBd.setStatut(reservation.getStatut()); // Mettre à jour le statut

                // Fusionner l'état de l'entité
                entityManager.merge(reservationBd);
                entityManager.getTransaction().commit();
            } else {
                entityManager.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public void deleteReservation(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Reservation reservation = entityManager.find(Reservation.class, id);
            if (reservation != null) {
                entityManager.remove(reservation);
                entityManager.getTransaction().commit();
            } else {
                entityManager.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public List<Reservation> searchReservation(String search){
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Reservation> reservations = entityManager.createQuery("from Reservation r where r.villeDepart like :search or r.villeArrivee like :search", Reservation.class).setParameter("search", "%"+search+"%").getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return reservations;
    }

    // Fermer le EntityManagerFactory lorsque vous avez fini d'utiliser le ReservationRepository
    public void close() {
        entityManagerFactory.close();
    }
}