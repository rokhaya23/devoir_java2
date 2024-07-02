package com.example.devoir_java2.Repository;

import com.example.devoir_java2.MODEL.Evaluation;
import com.example.devoir_java2.JPAUTIL;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class EvaluationRepository {

    private EntityManagerFactory entityManagerFactory;

    public EvaluationRepository() {
        this.entityManagerFactory = JPAUTIL.getEntityManagerFactory();
    }

    public void addEvaluation(Evaluation evaluation) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(evaluation);
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

    public List<Evaluation> getAllEvaluations() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT e FROM Evaluation e", Evaluation.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Evaluation getEvaluationById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Evaluation.class, id);
        } finally {
            entityManager.close();
        }
    }

    public void updateEvaluation(Evaluation evaluation) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Evaluation evaluationBd = entityManager.find(Evaluation.class, evaluation.getId());
            if (evaluationBd != null) {
                evaluationBd.setCommentaire(evaluation.getCommentaire());
                evaluationBd.setNote(evaluation.getNote());
                evaluationBd.setClient(evaluation.getClient());
                evaluationBd.setTrajet(evaluation.getTrajet());
                entityManager.merge(evaluationBd);
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

    public void deleteEvaluation(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Evaluation evaluation = entityManager.find(Evaluation.class, id);
            if (evaluation != null) {
                entityManager.remove(evaluation);
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

    // Fermer le EntityManagerFactory lorsque vous avez fini d'utiliser le EvaluationRepository
    public void close() {
        entityManagerFactory.close();
    }
}
