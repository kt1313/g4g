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
        this.clubRepository=clubRepository;
        this.clubService=clubService;
        this.leagueService=leagueService;
    }

    public void createAppUser(String appusername, String clubname, String email, String password) {
        AppUser appUser = new AppUser(appusername, clubname, email, password);
        this.appUserRepository.save(appUser);
        AppUserRegistrationEvent event = new AppUserRegistrationEvent(this, appusername, clubname, email, password);
        publisher.publishEvent(event);

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
        this.clubRepository.save(newClub);
        League newLeague = this.leagueService.createLeague(newClub.getClubId());
        System.out.println("User:"+appusername+" Club name: "
                +clubname+" League ID and LeagueNr: "+newLeague+" League Teams: "+newLeague.getLeagueTeams());
    }

    public boolean confirmRegistration(long appUserId) {

        Optional<AppUser> byAppUserId = this.appUserRepository.findById(appUserId);

        if (byAppUserId.isPresent()) {
            byAppUserId.get().confirmRegistry();
            return true;
        } else {
            return false;
        }
    }
}
