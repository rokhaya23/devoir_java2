package com.example.devoir_java2.Repository;

import com.example.devoir_java2.MODEL.Trajet;
import com.example.devoir_java2.JPAUTIL;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

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
            Trajet trajetBd = entityManager.find(Trajet.class, trajet.getId());
            if (trajetBd != null) {
                trajetBd.setDateDepart(trajet.getDateDepart());
                trajetBd.setVilleDepart(trajet.getVilleDepart());
                trajetBd.setVilleArrivee(trajet.getVilleArrivee());
                trajetBd.setUser(trajet.getUser()); // Mettre à jour l'utilisateur associé si nécessaire
                // Ajouter d'autres champs à mettre à jour si nécessaire
                entityManager.merge(trajetBd);
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

    // Fermer le EntityManagerFactory lorsque vous avez fini d'utiliser le TrajetRepository
    public void close() {
        entityManagerFactory.close();
    }
}
