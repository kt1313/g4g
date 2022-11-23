package pl.com.k1313.g4g.domain.league;

import com.mysql.cj.protocol.a.NativeConstants;
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
        userClub.setClubLeague(userLeague);
        this.clubRepository.save(userClub);
        List<Club> leagueTeams=new ArrayList<>();
        //to do zrpbienia w przyszlosci - co jesliw  availbele league jest juz uzytkownik
//      if (!userLeague.getLeagueTeams().isEmpty()){leagueTeams=userLeague.getLeagueTeams();}
        leagueTeams.add(userClub);
        this.leagueRepository.save(userLeague);
//        tutaj trzeba sprawdzic czy zespoly ligi to boty i wyrzucic jeden jesli tak
        if (leagueTeams.size() > 8) {
            for (Club c : leagueTeams
            ) {
                if (c.getAppUser() == null) {
                    this.clubRepository.delete(c);
                    break;
                }
            }
        }

//        while (userLeague.getLeagueTeams().size() < 8) {
//            this.clubRepository.save(this.clubService.botClubCreation());
//        }
////            } else {
        while (leagueTeams.size() < 8) {
            Club newBotClub = this.clubService.botClubCreation();
;            newBotClub.setClubLeague(userLeague);
            leagueTeams.add(newBotClub);
            this.clubRepository.save(newBotClub);
        }
//            }

        this.leagueRepository.save(userLeague);
        //jak wydlubac zespoly z ligi??? tylko z leagueTeams??
        System.out.println("----------------");
        System.out.println("Liga: "+ userLeague.getId());
        System.out.println("----------------");
        System.out.println("zespoly: "+leagueTeams);
        //zwraca NUlllaaaa!! ponizej
        return userLeague;
    }

    public League checkAvailableLeague() {
        League league = new League();
        List<League> leagues = this.leagueRepository.findAll();
        for (League l : leagues
        ) {
            List<Club> leagueClubs = this.clubRepository.findByClubLeague(l.getId());
            List<AppUser> leagueAppUsers = new ArrayList<>();
            for (Club c : leagueClubs
            ) {
                if (c.getAppUser() != null) {
                    leagueAppUsers.add(c.getAppUser());
                    if (leagueAppUsers.size() < 8) {
                        league = l;
                        //teraz wyciagnij nr ligi a jesli nie ma to nadaj pierwszy wolny
                        if (league.getLeagueNumber().isEmpty()) {
                            setLeagueNumber(league);
                        }
                    } else {
                        league = l;
                        break;
                    }
                }
            }
        }
        this.leagueRepository.save(league);
        return league;

    }

    public String setLeagueNumber(League league) {
        String stringMaxLeagueNr = "1";
        List<League> leaguesWithNoNumbers =
                this.leagueRepository.findAnyByLeagueNumberNotNull();
        int maxInt = 0;
        for (League leag : leaguesWithNoNumbers
        ) {
            int leagueNumberToSet = Integer.parseInt(leag.getLeagueNumber());
            if (leagueNumberToSet > maxInt) {
                maxInt = leagueNumberToSet;
            }
        }
        stringMaxLeagueNr = String.valueOf(maxInt + 1);
        league.setLeagueNumber(stringMaxLeagueNr);
        return stringMaxLeagueNr;
    }
}
