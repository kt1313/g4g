package pl.com.k1313.g4g.domain.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LeagueService {

    private LeagueRepository leagueRepository;
    private ClubRepository clubRepository;
    private ClubService clubService;

    @Autowired
    public LeagueService(LeagueRepository leagueRepository, ClubRepository clubRepository, ClubService clubService) {
        this.leagueRepository = leagueRepository;
        this.clubRepository = clubRepository;
        this.clubService = clubService;
    }

    public League createLeague(long userClubId) {

        Club userClub = this.clubRepository.findByClubId(userClubId);
        League userLeague = checkAvailableLeague();
        List<Club> leagueTeams = userLeague.getLeagueTeams();
        if (leagueTeams != null) {
            for (Club c : leagueTeams
            ) {
                if (c.getAppUser() == null) {
                    this.clubRepository.delete(c);
                    break;
                }
            }
            leagueTeams.add(userClub);
            while (userLeague.getLeagueTeams().size() < 8) {
                this.clubRepository.save(this.clubService.botClubCreation());
            }
        } else {
            leagueTeams=new ArrayList<>();
            leagueTeams.add(userClub);
            while (leagueTeams.size() < 8) {
                Club newBotClub=this.clubService.botClubCreation();
                leagueTeams.add(newBotClub);
                this.clubRepository.save(newBotClub);
            }
        }
        this.leagueRepository.save(userLeague);
        return userLeague;
    }

    public League checkAvailableLeague() {
        League league = new League();
        List<League> leagues = this.leagueRepository.findAll();
        for (League l : leagues
        ) {
            List<Club> leagueClubs = this.clubRepository.findByClubLeague(l);
            List<AppUser> leagueAppUsers = new ArrayList<>();
            for (Club c : leagueClubs
            ) {
                AppUser appUser = c.getAppUser();
                if (appUser != null) {
                    leagueAppUsers.add(c.getAppUser());
                }
            }
            if (leagueAppUsers.size() < 8) {
                league = l;
                break;
            }
        }
        this.leagueRepository.save(league);
        return league;
    }
}
