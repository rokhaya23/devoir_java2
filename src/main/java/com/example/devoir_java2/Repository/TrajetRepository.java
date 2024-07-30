package com.example.devoir_java2.Repository;

import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.MODEL.Trajet;
import com.example.devoir_java2.JPAUTIL;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrajetRepository {

    private EntityManagerFactory entityManagerFactory;

    public TrajetRepository() {
        this.entityManagerFactory = JPAUTIL.getEntityManagerFactory();
    }

    public void addTrajet(Trajet trajet) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(trajet);
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

    public List<Trajet> getAllTrajets() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT t FROM Trajet t", Trajet.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Trajet getTrajetById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Trajet.class, id);
        } finally {
            entityManager.close();
        }
    }

    public void updateTrajet(Trajet trajet) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();

            // Trouver l'entité Trajet existante dans la base de données
            Trajet trajetBd = entityManager.find(Trajet.class, trajet.getId());
            if (trajetBd != null) {
                // Mettre à jour les champs nécessaires
                trajetBd.setDateReservation(trajet.getDateReservation());
                trajetBd.setVilleDepart(trajet.getVilleDepart());
                trajetBd.setVilleArrivee(trajet.getVilleArrivee());
                trajetBd.setUser(trajet.getUser()); // Mettre à jour l'utilisateur associé si nécessaire

                // Mettre à jour le nombre de places
                trajetBd.setNbPlaces(trajet.getNbPlaces());

                // Synchroniser les modifications avec la base de données
                entityManager.merge(trajetBd);
                entityManager.getTransaction().commit();
            } else {
                entityManager.getTransaction().rollback();
                throw new IllegalArgumentException("Trajet not found");
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace(); // Afficher l'exception pour le débogage
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public void deleteTrajet(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Trajet trajet = entityManager.find(Trajet.class, id);
            if (trajet != null) {
                entityManager.remove(trajet);
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
    public Long countTrajet(){
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Long trajet = entityManager.createQuery("select count(u) from Trajet u", Long.class).getSingleResult();
        entityManager.getTransaction().commit();
        entityManager.close();
        return trajet;
    }
    public Map<Month, Integer> getTrajetsParMois() {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Object[]> result = entityManager.createQuery("SELECT MONTH(t.dateReservation), COUNT(t) FROM Trajet t GROUP BY MONTH(t.dateReservation)").getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        Map<Month, Integer> trajetsParMois = new HashMap<>();
        for (Object[] objects : result) {
            int month = ((Number) objects[0]).intValue();
            int count = ((Number) objects[1]).intValue();
            trajetsParMois.put(Month.of(month), count);
        }
        return trajetsParMois;
    }
    public Map<Month, Integer> getRevenusParMois() {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Object[]> result = entityManager.createQuery("SELECT MONTH(t.dateReservation), SUM(t.tarif) FROM Trajet t GROUP BY MONTH(t.dateReservation)").getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        Map<Month, Integer> revenusParMois = new HashMap<>();
        for (Object[] objects : result) {
            int month = ((Number) objects[0]).intValue();
            int sum = ((Number) objects[1]).intValue();
            revenusParMois.put(Month.of(month), sum);
        }
        return revenusParMois;
    }

    public List<Trajet> searchTrajet(String search){
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Trajet> trajets = entityManager.createQuery("from Trajet t where t.villeDepart like :search or t.villeArrivee like :search", Trajet.class).setParameter("search", "%"+search+"%").getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return trajets;
    }

    // Fermer le EntityManagerFactory lorsque vous avez fini d'utiliser le TrajetRepository
    public void close() {
        entityManagerFactory.close();
    }
}
