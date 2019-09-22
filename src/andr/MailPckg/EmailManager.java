package andr.MailPckg;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;

public class EmailManager {
    public static void send(String to){
        try {
        Properties properties = new Properties();
        properties.load(new FileInputStream("properties.properties"));

        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("shalyap3211"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Ваш пароль для входа в систему");
        message.setText(GenerateSecurePassword.generatePassword(8));

        Transport transport = session.getTransport();
        transport.connect(null, "Qwerty228");
        transport.sendMessage(message, message.getAllRecipients());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}