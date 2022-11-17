package pl.com.k1313.g4g.domain.league;

import org.springframework.beans.factory.annotation.Autowired;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;

import java.util.ArrayList;
import java.util.List;

public class LeagueService {

    private LeagueRepository leagueRepository;
    private ClubRepository clubRepository;

    @Autowired
    public LeagueService(LeagueRepository leagueRepository, ClubRepository clubRepository) {
        this.leagueRepository = leagueRepository;
        this.clubRepository = clubRepository;
    }

    public void createLeague(long userClubId) {

        Club userClub = this.clubRepository.findByClubId(userClubId);
        List<League> leagues = this.leagueRepository.findAll();
        List<Club> leagueTeams = new ArrayList<>();
        League league = new League();
        if (leagues.size() > 0) {
            league = leagues.get(0);
            leagueTeams = league.getLeagueTeams();

            if (checkLeagueForUsers(league.getId()).size() < 8) {
                leagueTeams.add(userClub);
                this.leagueRepository.save(league);
            } else {
                League newleague = new League();
                newleague.getLeagueTeams().add(userClub);
                this.leagueRepository.save(newleague);
            }
        }else{
            leagueTeams.add(userClub);
            for (int i=0;i<leagueTeams.size();i++){

            }
        }
        return;
    }

    public AppUser checkClubForUser(long clubId) {
        AppUser clubAppUser = this.clubRepository.findByClubId(clubId).getAppUser();
        return clubAppUser;
    }

    public List<AppUser> checkLeagueForUsers(long leagueId) {
        List<AppUser> leagueAppUsers = new ArrayList<>();
        List<Club> leagueClubs = this.leagueRepository.findAll();
        for (Club c : leagueClubs
        ) {
            if (c.getAppUser().isRegistered()) {
                leagueAppUsers.add(c.getAppUser());
            }
        }
        return leagueAppUsers;
    }
}
