package com.example.devoir_java2.Repository;

import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.JPAUTIL;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

import java.util.List;

public class UserRepository {

    public boolean addUser(User user) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(user);
        entityManager.getTransaction().commit();
        entityManager.close();
        return true;
    }

    public User getUserByEmail(String email) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = null;
        try {
            user = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            user = null;
        } finally {
            entityManager.close();
        }
        return user;
    }

    public User getUserByEmailAndPassword(String email, String password) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = null;
        try {
            user = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            user = null;
        } finally {
            entityManager.close();
        }
        return user;
    }

    public List<User> getAllUsers() {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<User> users = null;
        try {
            users = entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.role", User.class).getResultList();
        } finally {
            entityManager.close();
        }
        return users;
    }


    public void updateUser(User user) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(user);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void deleteUser(Long user) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User userFromDb = entityManager.find(User.class, user);
        if (userFromDb != null) {
            entityManager.remove(userFromDb);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public User getUserById(Long id) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = null;
        try {
            user = entityManager.find(User.class, id);
        } finally {
            entityManager.close();
        }
        return user;
    }

    public List<User> findByRole(String roleName) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<User> users = null;
        try {
            users = entityManager.createQuery("SELECT u FROM User u WHERE u.role.name = :roleName", User.class)
                    .setParameter("roleName", roleName)
                    .getResultList();
        } finally {
            entityManager.close();
        }
        return users;
    }
}
