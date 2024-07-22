package com.example.devoir_java2.MODEL;
import com.example.devoir_java2.MODEL.User;


public class AppContext {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
