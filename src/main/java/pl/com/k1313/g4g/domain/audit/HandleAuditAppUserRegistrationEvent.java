package pl.com.k1313.g4g.domain.audit;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.com.k1313.g4g.domain.appuser.events.AppUserRegistrationEvent;

@Component
public class HandleAuditAppUserRegistrationEvent  {
@Async
@EventListener
    public void handleAppUserRegistrationEvent(AppUserRegistrationEvent event) {
        System.out.println("AUDIT handle  Async event");
    }
}

