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
    private String clubname;
    private String appUserPassword;
    private String userEmail;

    private long clubId;

    private boolean registered = false;


    AppUser(long appUserId, String appUserName, String userPassword, String userEmail) {
        this.appUserId = appUserId;
        this.appUserName = appUserName;
        this.appUserPassword = userPassword;
        this.userEmail = userEmail;
    }

    AppUser(long appUserId, String appUserName, String userPassword, String userEmail, boolean registered) {
        this.appUserId = appUserId;
        this.appUserName = appUserName;
        this.appUserPassword = userPassword;
        this.userEmail = userEmail;
        this.registered = registered;
    }

    public AppUser(String appUserName, String clubname) {
        this.appUserName = appUserName;
        this.clubname = clubname;
    }

    public AppUser(String appusername, String clubname, String userEmail, String password) {
        this.appUserName = appusername;
        this.clubname = clubname;
        this.userEmail = userEmail;
        this.appUserPassword = password;
    }

    public AppUser() {

    }

    public void confirmRegistry() {
        this.registered = true;
    }
}
