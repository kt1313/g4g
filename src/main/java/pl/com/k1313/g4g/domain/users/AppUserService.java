package pl.com.k1313.g4g.domain.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.users.events.AppUserRegistrationEvent;

@Service
public class AppUserService {

    private ApplicationEventPublisher publisher;
    private AppUserRepository repository;

    @Autowired
    public AppUserService(ApplicationEventPublisher publisher, AppUserRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    public void AppUserRegistration(long appUserId, String username, String password, String email) {
        AppUser newUser = new AppUser(appUserId, username, password, email);
        this.repository.save(newUser);
        AppUserRegistrationEvent event=new AppUserRegistrationEvent(username, email);
        publisher.publishEvent(event);

    }


}
