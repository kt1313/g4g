package pl.com.k1313.g4g.domain.appuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.appuser.events.AppUserRegistrationEvent;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;

import java.util.Optional;

@Service
public class AppUserService {

    private ApplicationEventPublisher publisher;
    private AppUserRepository appUserRepository;
    private PlayerRepository playerRepository;
    private PlayerService playerService;
    private ClubRepository clubRepository;
    private ClubService clubService;

    @Autowired
    public AppUserService(ApplicationEventPublisher publisher,
                          AppUserRepository appUserRepository,
                          PlayerRepository playerRepository,
                          PlayerService playerService,
                          ClubRepository clubRepository,
                          ClubService clubService) {
        this.publisher = publisher;
        this.appUserRepository = appUserRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.clubRepository=clubRepository;
        this.clubService=clubService;
    }

//    public void AppUserRegistration(long appUserId, String username, String password, String email) {
//        AppUser newUser = new AppUser(appUserId, username, password, email);
//        this.repository.save(newUser);
//        AppUserRegistrationEvent event=new AppUserRegistrationEvent(username, email);
//        publisher.publishEvent(event);
//
//    }


    public void createAppUser(String appusername, String clubname, String email, String password) {
        AppUser appUser = new AppUser(appusername, clubname, email, password);
        this.appUserRepository.save(appUser);
        AppUserRegistrationEvent event = new AppUserRegistrationEvent(this, appusername, clubname, email, password);
        publisher.publishEvent(event);
        System.out.println("UDALO SIE ZAREJESTROWAC UZYTKOWNIKA: ");
        System.out.println(appUser);
        Club newClub=this.clubService.clubCreation(appUser,clubname);
        for (int i=0; i<18;i++) {
            Player newPlayer=this.playerService.autoCreatePlayer();
            newPlayer.setPlayerClub(newClub);
            this.playerRepository.save(newPlayer);
            System.out.println(" Player nr "+i+" "+newPlayer);
        }
        for (int i=0; i<3;i++) {
            Player newGoalkeeper=this.playerService.autoCreateGoalkeeper();
            newGoalkeeper.setPlayerClub(newClub);
            this.playerRepository.save(newGoalkeeper);
            System.out.println(" Goalkeeper nr "+i+" "+newGoalkeeper);
        }
    }

    public boolean confirmRegistration(String appUserName) {

        Optional<AppUser> byAppUserName = this.appUserRepository.findByAppUserName(appUserName);

        if (byAppUserName.isPresent()) {
            byAppUserName.get().confirmRegistry();
            return true;
        } else {
            return false;
        }
    }
}
