package pl.com.k1313.g4g.domain.users.events;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AppUserRegistrationEvent {
    public final LocalDateTime registrationTime;
    public final String userName;
    public final String userEmail;

    public AppUserRegistrationEvent(String userName, String userEmail){
        this.registrationTime=LocalDateTime.now();
        this.userName=userName;
        this.userEmail=userEmail;
    }
}
