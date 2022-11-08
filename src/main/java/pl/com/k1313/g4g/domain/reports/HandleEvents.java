package pl.com.k1313.g4g.domain.reports;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.com.k1313.g4g.domain.users.events.AppUserRegistrationEvent;

@Component
public class HandleEvents {

    @EventListener
    public void handleAppUserRegistrationEvent(AppUserRegistrationEvent event){
        System.out.println("Handling event registrated at "+event.toString());
    }
}
