package pl.com.k1313.g4g.util.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.com.k1313.g4g.domain.appuser.events.AppUserRegistrationEvent;

@Component
public class HandleMailAppUserRegistrationEvent implements ApplicationListener<AppUserRegistrationEvent> {

    private final EmailService emailService;

    @Autowired
    public HandleMailAppUserRegistrationEvent(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(AppUserRegistrationEvent event) {
        System.out.println("EMAIL- handle event by implementing AppListener");
        this.emailService.sendRegistryConfirmationEmail(event.appUserEmail, event.appUserName, event.appUserPassword);
    }
}

