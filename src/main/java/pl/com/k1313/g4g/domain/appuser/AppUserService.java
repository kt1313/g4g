package pl.com.k1313.g4g.domain.appuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.appuser.events.AppUserRegistrationEvent;

import java.util.Optional;

@Service
public class AppUserService {

    private ApplicationEventPublisher publisher;
    private AppUserRepository repository;

    @Autowired
    public AppUserService(ApplicationEventPublisher publisher, AppUserRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

//    public void AppUserRegistration(long appUserId, String username, String password, String email) {
//        AppUser newUser = new AppUser(appUserId, username, password, email);
//        this.repository.save(newUser);
//        AppUserRegistrationEvent event=new AppUserRegistrationEvent(username, email);
//        publisher.publishEvent(event);
//
//    }


    public void createTempAppUser(String appusername, String email, String password) {
        AppUser tmpUser = new AppUser(appusername, email, password);
        this.repository.save(tmpUser);
        AppUserRegistrationEvent event = new AppUserRegistrationEvent(this, appusername, email, password);
        publisher.publishEvent(event);
        System.out.println("UDALO SIE ZAREJESTROWAC UZYTKOWNIKA");
        System.out.println(tmpUser);
    }

    public boolean confirmRegistration(String appUserName) {

        Optional<AppUser> byAppUserName = this.repository.findByAppUserName(appUserName);

        if (byAppUserName.isPresent()) {
            byAppUserName.get().confirmRegistry();
            return true;
        } else {
            return false;
        }
    }
}
