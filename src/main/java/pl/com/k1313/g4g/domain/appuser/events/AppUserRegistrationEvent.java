package pl.com.k1313.g4g.domain.appuser.events;


import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Data
public class AppUserRegistrationEvent extends ApplicationEvent {
    public final LocalDateTime registrationTime;
    public final String appUserName;
    public final String appUserEmail;
    public final String appUserPassword;

    public AppUserRegistrationEvent(Object context,String userName, String userEmail, String appUserPassword){
        super(context);
        this.registrationTime=LocalDateTime.now();
        this.appUserName=userName;
        this.appUserEmail=userEmail;
        this.appUserPassword=appUserPassword;
    }
}
