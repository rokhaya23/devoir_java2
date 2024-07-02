package com.example.devoir_java2;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUTIL {

    public static final String PERSISTANCE_UNIT_NAME = "PERSISTENCE";
    private static EntityManagerFactory factory;
    public static EntityManagerFactory getEntityManagerFactory() {
        //singleton (design pattern) d'instencier la classe une seule fois
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(PERSISTANCE_UNIT_NAME);
        }
        return factory;
    }

    public static void shutdown() {
        if (factory != null) {
            factory.close();
        }
    }

}
