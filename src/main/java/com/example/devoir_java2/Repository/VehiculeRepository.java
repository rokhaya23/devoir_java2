package com.example.devoir_java2.Repository;

import com.example.devoir_java2.MODEL.Vehicule;
import com.example.devoir_java2.JPAUTIL;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class VehiculeRepository {

    private EntityManagerFactory entityManagerFactory;

    public VehiculeRepository() {
        this.entityManagerFactory = JPAUTIL.getEntityManagerFactory();
    }

    public void addVehicule(Vehicule vehicule) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(vehicule);
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

    public List<Vehicule> getAllVehicules() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT v FROM Vehicule v", Vehicule.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Vehicule getVehiculeById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Vehicule.class, id);
        } finally {
            entityManager.close();
        }
    }

    public void updateVehicule(Vehicule vehicule) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Vehicule vehiculeBd = entityManager.find(Vehicule.class, vehicule.getId());
            if (vehiculeBd != null) {
                vehiculeBd.setImmatriculation(vehicule.getImmatriculation());
                vehiculeBd.setMarque(vehicule.getMarque());
                vehiculeBd.setModele(vehicule.getModele());
                vehiculeBd.setUser(vehicule.getUser()); // Mettre à jour l'utilisateur associé si nécessaire
                entityManager.merge(vehiculeBd);
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

    public void deleteVehicule(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Vehicule vehicule = entityManager.find(Vehicule.class, id);
            if (vehicule != null) {
                entityManager.remove(vehicule);
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

    // Fermer le EntityManagerFactory lorsque vous avez fini d'utiliser le VehiculeRepository
    public void close() {
        entityManagerFactory.close();
    }
}
