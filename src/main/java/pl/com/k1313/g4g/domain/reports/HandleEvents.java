package pl.com.k1313.g4g.domain.reports;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.com.k1313.g4g.domain.appuser.events.AppUserRegistrationEvent;

@Component
public class HandleEvents {
    @Async
    @EventListener
    public void handleAppUserRegistrationEvent(AppUserRegistrationEvent event) {
        System.out.println("Timestamp: " + event.getTimestamp());
        System.out.println("Handling event registrated at " + event.toString());
    }
}
