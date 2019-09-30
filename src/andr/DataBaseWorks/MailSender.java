package andr.DataBaseWorks;

import javax.mail.Authenticator;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    private MailService service;
    private String login;
    private String password;


    public MailSender(MailService service, String login, String password){
        this.service = service;
        this.login = login;
        this.password = password;
    }


    public void send(String theme, String message, String toEmail){
        System.out.println("Sending email!");
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.host", service.getHost());
        props.put("mail.smtp.port", service.getPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout", "1000");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.port", service.getPort());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(login, password);
            }
        });

// Construct the message
        String to = toEmail;
        String from = login;
        String subject = theme;
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setText(message);

// Send the message.
            Transport.send(msg);
            System.out.println("Отправлено!");
        }catch (AuthenticationFailedException e){
            System.err.println("Error: invalid login or password!");
        }
        catch (javax.mail.MessagingException e) {
            System.err.println("Error: something went wrong sending email");
        }
    }
}
