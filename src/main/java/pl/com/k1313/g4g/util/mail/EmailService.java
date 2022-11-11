package pl.com.k1313.g4g.util.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender sender;

    @Value("${g4g.protocol}")
    private String protocol;
    @Value("${g4g.domain}")

    private String domain;
    @Value("${g4g.port}")

    private String port;
    private String confirmationEndpoint = "appuser/registration/confirmed";

    @Autowired
    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

    public void sendRegistryConfirmationEmail(String appUserEmail, String appUserName, String clubname, String appUserPassword) {

        System.out.println(protocol+" "+ domain+" "+ port+" "+ confirmationEndpoint);
        String endpoint = String.format("%s://%s:%s/%s/%s", protocol, domain, port, confirmationEndpoint, appUserName);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(appUserEmail);
        email.setFrom("k1313coding@gmail.com");
        email.setSubject("G4G - registration confirmed");
        email.setText("Thank you for joining us!" +appUserName+" \n Your "+clubname+" is waiting for you.");
        System.out.println("To login click: " + endpoint);
        this.sender.send(email);

    }
}
