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
        List<Club> leagueTeams = new ArrayList<>();
        if (userLeague.getLeagueTeams() != null) {
            leagueTeams = userLeague.getLeagueTeams();
        }
        int test = leagueTeams.size();
//        tutaj trzeba sprawdzic czy zespoly ligii to boty i wyrzucic jeden jesli tak
//            if (leagueTeams != null) {
        for (Club c : leagueTeams
        ) {
            if (c.getAppUser() == null) {
                this.clubRepository.delete(c);
                break;
            }
        }
        leagueTeams.add(userClub);
        userClub.setClubLeague(userLeague);
        this.clubRepository.save(userClub);
        //tutaj musi cos userLeague sprawdzic stworzyc
        this.leagueRepository.save(
        while (userLeague.getLeagueTeams().size() < 8) {
            this.clubRepository.save(this.clubService.botClubCreation());
        }
//            } else {
        leagueTeams.add(userClub);
        while (leagueTeams.size() < 8) {
            Club newBotClub = this.clubService.botClubCreation();
            leagueTeams.add(newBotClub);
            this.clubRepository.save(newBotClub);
        }
//            }

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
                if (c.getAppUser() != null) {
                    leagueAppUsers.add(c.getAppUser());
                    if (leagueAppUsers.size() < 8) {
                        league = l;
                        //teraz wyciagnij nr ligi a jesli nie ma to nadaj pierwszy wolny
                        if (league.getLeagueNumber().isEmpty()) {
                            List<League> leaguesWithNoNumbers =
                                    this.leagueRepository.findAnyByLeagueNumberNotNull();
                            if (!leaguesWithNoNumbers.isEmpty()) {
                                int maxInt = 0;
                                for (League leag : leaguesWithNoNumbers
                                ) {
                                    int leagueNumber = Integer.parseInt(leag.getLeagueNumber());
                                    if (leagueNumber > maxInt) {
                                        maxInt = leagueNumber;
                                    }
                                }
                                String stringMaxInt=String.valueOf(maxInt+1);
                                league.setLeagueNumber(stringMaxInt);
                            }
                        } else {

                            dokonczyc tego elsa
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        this.leagueRepository.save(league);
        return league;

    }
}
