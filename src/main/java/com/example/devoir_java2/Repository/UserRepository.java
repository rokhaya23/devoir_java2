package com.example.devoir_java2.Repository;

import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.JPAUTIL;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class UserRepository {

    public void addUser(User user) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(user);

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();
    }

    public List<User> getAllUsers() {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();

        return users;
    }

    public void updateUser(User user) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User clientBd = entityManager.find(User.class, user.getId());
        entityManager.merge(user);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void deleteUser(User user) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User clientBd = entityManager.find(User.class, user.getId());
        entityManager.remove(clientBd);
        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();
    }

    public User getUserById(Long id) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = entityManager.find(User.class, id);
        entityManager.getTransaction().commit();
        entityManager.close();
        return user;
    }
}
