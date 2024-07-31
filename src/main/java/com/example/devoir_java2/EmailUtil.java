package com.example.devoir_java2;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {
    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String USERNAME = System.getenv("SMTP_USERNAME");
    private static final String PASSWORD = System.getenv("SMTP_PASSWORD");

    public static void sendEmail(String toEmail, String subject, String messageBody) {
        // Debugging statements
        System.out.println("SMTP_USERNAME: " + USERNAME);
        System.out.println("SMTP_PASSWORD: " + PASSWORD);

        if (USERNAME == null || PASSWORD == null) {
            System.out.println("Username or Password is null. Please check environment variables.");
            return;
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.host", SMTP_SERVER);
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME)); // L'expéditeur
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail) // Le destinataire
            );
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message); // Envoi de l'email

            System.out.println("Email sent successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
