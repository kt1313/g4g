package pl.com.k1313.g4g.domain.club;

import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.appuser.AppUserService;
import pl.com.k1313.g4g.domain.league.LeagueRepository;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ClubService {
    public ClubService(ClubRepository clubRepository, PlayerRepository playerRepository,
                       PlayerService playerService, LeagueRepository leagueRepository
//            , AppUserService appUserService
    ) {
        this.clubRepository = clubRepository;
        this.playerRepository = playerRepository;
        this.leagueRepository = leagueRepository;
        this.playerService = playerService;
//        this.appUserService=appUserService;
    }

//    private AppUserService appUserService;
    private PlayerService playerService;
    private LeagueRepository leagueRepository;
    private PlayerRepository playerRepository;
    private ClubRepository clubRepository;

    //
    public Club clubCreation(AppUser appUser, String clubname) {
        Club newClub = new Club(appUser, clubname);
        this.clubRepository.save(newClub);
        System.out.println(" user club" + this.clubRepository.findByClubId(newClub.getClubId()));
        return newClub;
    }

    public Club botClubCreation() {
        Club newClub = new Club();
        newClub.setClubName(botClubNameCreation());
        newClub.setClubFirst11(this.playerService.botPlayersCreation(newClub.getClubId()));
        newClub.setAppUser(new AppUser("Teddy Bot", newClub.getClubName()));
        this.clubRepository.save(newClub);
        return newClub;
    }

    public String botClubNameCreation() {
        Random r = new Random();
        List clubNames = new ArrayList<>(List.of
                ("FC BigDaddy", "A-Team", "Strongmen FC", "L-losers", "Handycaps Club", "WeWinOnWednesday United",
                        "NeverSeeYourGoal CF", "FC True team", "WeWillChewYourMeat Utd", "FC GiveUp"));
        String clubName = (String) clubNames.get(r.nextInt(10));
        return clubName;
    }

//    public List<Player> setUpFirstEleven(Club club) {
//        List<Player> clubFirst11 = this.playerRepository.findAllByPlayerClub(club)
//                .stream().filter(Player::isFirstSquadPlayer)
//                .collect(Collectors.toList());
//        club.setClubFirst11(clubFirst11);
//        this.clubRepository.save(club);
//        return clubFirst11;
//    }

//    public String[][] setUpFirst11(List<Player> firstsquadplayers) {
//
//        List<Player> clubFirst11;
//        String[][] first11Table = new String[5][4];
//        first11Table[0][0] = "0";
//        first11Table[0][1] = "right Wingback";
//        first11Table[0][2] = "right Winger";
//        first11Table[0][3] = "3";
//        first11Table[1][0] = "4";
//        first11Table[1][1] = "right Centreback";
//        first11Table[1][2] = "centre Midfielder Defending";
//        first11Table[1][3] = "right Forward";
//        first11Table[2][0] = "goalkeeper";
//        first11Table[2][1] = "centreback";
//        first11Table[2][2] = "centre Midfielder";
//        first11Table[2][3] = "centre Forward";
//        first11Table[3][0] = "12";
//        first11Table[3][1] = "left Centreback";
//        first11Table[3][2] = "centre Midfielder Attacking";
//        first11Table[3][3] = "left Forward";
//        first11Table[4][0] = "17";
//        first11Table[4][1] = "left Wingback";
//        first11Table[4][2] = "left Winger";
//        first11Table[4][3] = "20";
//
//        String[][] first11FinalTable = new String[5][4];
//        for (int x = 0; x < 5; x++) {
//            for (int y = 0; y < 4; y++) {
//
//                //tu sparwdza czy pozycja jest wolna i jesli tak, to pobiera
//                // sprawdza czy jej pozycja jest rowna pozycji zawodnika i
//                // pobiera dane o zawodniku
//                //i do niej przypisuje
//                for (Player player : firstsquadplayers) {
//                    if (first11FinalTable[x][y].isEmpty()) {
//                        String playerPos = String.valueOf(player.getPlayerPosition());
//                        if (Objects.equals(first11Table[x][y], playerPos)) {
//                            first11FinalTable[x][y] = player.getFirstName() + " " + player.getLastName();
//                        }
//                    }
//                }
//            }
//        }
//        return first11FinalTable;
//
//    }
}
