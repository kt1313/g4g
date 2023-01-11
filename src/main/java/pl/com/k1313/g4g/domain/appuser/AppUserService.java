package pl.com.k1313.g4g.domain.appuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.appuser.events.AppUserRegistrationEvent;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;
import pl.com.k1313.g4g.domain.league.League;
import pl.com.k1313.g4g.domain.league.LeagueService;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;

import java.text.SimpleDateFormat;
import java.util.Optional;

@Service
public class AppUserService {

    private ApplicationEventPublisher publisher;
    private AppUserRepository appUserRepository;
    private PlayerRepository playerRepository;
    private PlayerService playerService;
    private ClubRepository clubRepository;
    private ClubService clubService;
    private LeagueService leagueService;

    @Autowired
    public AppUserService(ApplicationEventPublisher publisher,
                          AppUserRepository appUserRepository,
                          PlayerRepository playerRepository,
                          PlayerService playerService,
                          ClubRepository clubRepository,
                          ClubService clubService,
                          LeagueService leagueService) {
        this.publisher = publisher;
        this.appUserRepository = appUserRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.clubRepository = clubRepository;
        this.clubService = clubService;
        this.leagueService = leagueService;
    }

    public void createAppUser(String appusername, String clubname, String email, String password) {

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        AppUser appUser = new AppUser(appusername, clubname, email, password, timeStamp);
        this.appUserRepository.save(appUser);
        AppUserRegistrationEvent event = new AppUserRegistrationEvent(this, appusername, clubname, email, password);
        publisher.publishEvent(event);

        Club newClub = this.clubService.clubCreation(appUser, clubname);
        for (int i = 0; i < 18; i++) {
            Player newPlayer = this.playerService.autoCreatePlayer();
            newPlayer.setPlayerClub(newClub);
            this.playerRepository.save(newPlayer);
            System.out.println(" Player nr " + i + " " + newPlayer);
        }
        for (int i = 0; i < 3; i++) {
            Player newGoalkeeper = this.playerService.autoCreateGoalkeeper();
            newGoalkeeper.setPlayerClub(newClub);
            this.playerRepository.save(newGoalkeeper);
            System.out.println(" Goalkeeper nr " + i + " " + newGoalkeeper);
        }
        this.clubRepository.save(newClub);
        appUser.setClubId(newClub.getClubId());
        this.appUserRepository.save(appUser);
        League newLeague = this.leagueService.createLeague(newClub.getClubId());
  }

    public boolean confirmRegistration(long appUserId) {

        AppUser byAppUserId = this.appUserRepository.findByAppUserId(appUserId);
        if (byAppUserId != null) {
            byAppUserId.confirmRegistry();
            this.appUserRepository.save(byAppUserId);
            return true;
        } else {
            return false;
        }
    }

    public AppUser botAppUserCreation(Club club){
        long clubId=club.getClubId();
        AppUser appUser=new AppUser("Teddy Bot", club.getClubName(), clubId);
        this.appUserRepository.save(appUser);
        return appUser;
    }
}
