package com.example.devoir_java2.Repository;

import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.MODEL.Vehicule;
import com.example.devoir_java2.JPAUTIL;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public Vehicule findByChauffeurId(Long chauffeurId) {
        EntityManagerFactory emf = JPAUTIL.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        Vehicule vehicule = null;

        try {
            vehicule = em.createQuery("SELECT v FROM Vehicule v WHERE v.chauffeur.id = :chauffeurId", Vehicule.class)
                    .setParameter("chauffeurId", chauffeurId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return vehicule;
    }

    public void updateVehicule(Vehicule vehicule) {
        if (vehicule == null || vehicule.getId() == null) {
            throw new IllegalArgumentException("Le véhicule ou l'ID du véhicule ne peut pas être nul.");
        }

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Vehicule vehiculeBd = entityManager.find(Vehicule.class, vehicule.getId());
            if (vehiculeBd != null) {
                vehiculeBd.setImmatriculation(vehicule.getImmatriculation());
                vehiculeBd.setMarque(vehicule.getMarque());
                vehiculeBd.setModele(vehicule.getModele());
                vehiculeBd.setNbres_place(vehicule.getNbres_place());  // Assurez-vous que cette ligne est incluse si vous mettez à jour aussi le nombre de places.
                entityManager.merge(vehiculeBd);
                entityManager.getTransaction().commit();
            } else {
                entityManager.getTransaction().rollback();
                throw new RuntimeException("Véhicule non trouvé pour l'ID: " + vehicule.getId());
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            Logger.getLogger(VehiculeRepository.class.getName()).log(Level.SEVERE, "Erreur lors de la mise à jour du véhicule", e);
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

    public List<Vehicule> searchVehicule(String search){
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Vehicule> vehicules = entityManager.createQuery("from Vehicule v where v.marque like :search or v.modele like :search or v.immatriculation like :search", Vehicule.class).setParameter("search", "%"+search+"%").getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return vehicules;
    }

    // Fermer le EntityManagerFactory lorsque vous avez fini d'utiliser le VehiculeRepository
    public void close() {
        entityManagerFactory.close();
    }
}
