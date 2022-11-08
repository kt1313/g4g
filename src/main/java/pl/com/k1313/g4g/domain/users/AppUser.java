package pl.com.k1313.g4g.domain.users;


import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;

@Data
@Setter(value = AccessLevel.NONE)
@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long appUserId;

    private String userName;
    private String userPassword;
    private String userEmail;

    AppUser(){}
    AppUser (long appUserId, String userName, String userPassword, String userEmail){
        this.appUserId=appUserId;
        this.userName=userName;
        this.userPassword=userPassword;
        this.userEmail=userEmail;
    }
}
