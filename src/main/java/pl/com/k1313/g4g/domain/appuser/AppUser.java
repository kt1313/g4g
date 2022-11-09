package pl.com.k1313.g4g.domain.appuser;


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

    private String appUserName;
    private String appUserPassword;
    private String userEmail;

    AppUser(){}
    AppUser (long appUserId, String appUserName, String userPassword, String userEmail){
        this.appUserId=appUserId;
        this.appUserName = appUserName;
        this.appUserPassword=userPassword;
        this.userEmail=userEmail;
    }

    public AppUser(String appusername, String userEmail, String password) {
        this.appUserName=appusername;
        this.userEmail=userEmail;
        this.appUserPassword=password;
    }
}
