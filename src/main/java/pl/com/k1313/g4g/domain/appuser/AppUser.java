package pl.com.k1313.g4g.domain.appuser;


import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;

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
    private String timeStampAppUser;

    private boolean registered = false;


    AppUser(long appUserId, String appUserName, String userPassword, String userEmail) {
        this.appUserId = appUserId;
        this.appUserName = appUserName;
        this.appUserPassword = userPassword;
        this.userEmail = userEmail;
    }
    AppUser(long appUserId, String appUserName, String userPassword, String userEmail, String timeStampAppUser) {
        this.appUserId = appUserId;
        this.appUserName = appUserName;
        this.appUserPassword = userPassword;
        this.userEmail = userEmail;
        this.timeStampAppUser=timeStampAppUser;
    }

    AppUser(long appUserId, String appUserName, String userPassword, String userEmail, boolean registered) {
        this.appUserId = appUserId;
        this.appUserName = appUserName;
        this.appUserPassword = userPassword;
        this.userEmail = userEmail;
        this.registered = registered;
    }

    public AppUser(String appUserName, String clubname, long clubId) {
        this.appUserName = appUserName;
        this.clubname = clubname;
        this.clubId=clubId;
    }

    public AppUser(String appusername, String clubname, String userEmail, String password, String timeStampAppUser) {
        this.appUserName = appusername;
        this.clubname = clubname;
        this.userEmail = userEmail;
        this.appUserPassword = password;
        this.timeStampAppUser=timeStampAppUser;
    }

    public AppUser() {

    }

    public long getAppUserId() {
        return appUserId;
    }

    public String getAppUserName() {
        return appUserName;
    }

    public void setAppUserName(String appUserName) {
        this.appUserName = appUserName;
    }

    public String getClubname() {
        return clubname;
    }

    public void setClubname(String clubname) {
        this.clubname = clubname;
    }

    public String getAppUserPassword() {
        return appUserPassword;
    }

    public void setAppUserPassword(String appUserPassword) {
        this.appUserPassword = appUserPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getClubId() {
        return clubId;
    }

    public void setClubId(long clubId) {
        this.clubId = clubId;
    }

    public String getTimeStampAppUser() {
        return timeStampAppUser;
    }

    public void setTimeStampAppUser(String timeStampAppUser) {
        this.timeStampAppUser = timeStampAppUser;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public void confirmRegistry() {
        this.registered = true;
    }
}
