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
    private static final String USERNAME = "2956851bec81e1";
    private static final String PASSWORD = "c2d5123b3a003c";

    public static void sendEmail(String toEmail, String subject, String messageBody) {

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        prop.put("mail.smtp.port", "2525");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@example.com")); // L'expéditeur
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
