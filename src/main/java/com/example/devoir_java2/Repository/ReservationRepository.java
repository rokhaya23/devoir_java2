package com.example.devoir_java2.Repository;

import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.JPAUTIL;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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

    public Reservation getReservationById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Reservation.class, id);
        } finally {
            entityManager.close();
        }
    }

    public void updateReservation(Reservation reservation) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Reservation reservationBd = entityManager.find(Reservation.class, reservation.getId());
            if (reservationBd != null) {
                reservationBd.setDateReservation(reservation.getDateReservation());
                reservationBd.setNbPlaces(reservation.getNbPlaces());
                reservationBd.setClient(reservation.getClient());
                reservationBd.setTrajet(reservation.getTrajet());
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

    // Fermer le EntityManagerFactory lorsque vous avez fini d'utiliser le ReservationRepository
    public void close() {
        entityManagerFactory.close();
    }
}
