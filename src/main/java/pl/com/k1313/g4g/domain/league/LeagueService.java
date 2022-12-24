package pl.com.k1313.g4g.domain.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;
import pl.com.k1313.g4g.domain.match.Game;
import pl.com.k1313.g4g.domain.match.GameRepository;
import pl.com.k1313.g4g.domain.match.GameType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeagueService {

    private LeagueRepository leagueRepository;
    private ClubRepository clubRepository;
    private GameRepository gameRepository;
    private ClubService clubService;

    @Autowired
    public LeagueService(LeagueRepository leagueRepository, ClubRepository clubRepository,
                         GameRepository gameRepository, ClubService clubService) {
        this.leagueRepository = leagueRepository;
        this.clubRepository = clubRepository;
        this.gameRepository=gameRepository;
        this.clubService = clubService;
    }

    public League createLeague(long userClubId) {

        Club userClub = this.clubRepository.findByClubId(userClubId);
        League userLeague = checkAvailableLeague();
        userClub.setClubLeague(userLeague);
        this.clubRepository.save(userClub);
        List<Club> leagueTeams = new ArrayList<>();
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

        while (leagueTeams.size() < 8) {
            Club newBotClub = this.clubService.botClubCreation();

            newBotClub.setClubLeague(userLeague);
            leagueTeams.add(newBotClub);
            this.clubRepository.save(newBotClub);
        }
        userLeague.setLeagueTeams(leagueTeams);
        userClub.setClubLeague(userLeague);
        this.clubRepository.save(userClub);
        createGamesFixtures(userLeague.getId());
        this.leagueRepository.save(userLeague);
        return userLeague;
    }

    public League checkAvailableLeague() {
        League league = new League();
        List<League> leagues = this.leagueRepository.findAll();
        for (League l : leagues
        ) {
            List<Club> leagueClubs = this.clubRepository.findByClubLeagueId(l.getId());
            List<AppUser> leagueAppUsers = new ArrayList<>();
            for (Club c : leagueClubs
            ) {
                if (c.getAppUser() != null) {
                    leagueAppUsers.add(c.getAppUser());
                    if (leagueAppUsers.size() < 8) {
                        league = l;
                        //teraz wyciagnij nr ligi, a jesli nie ma to nadaj pierwszy wolny
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

    public Map<Integer, List<Game>> createGamesFixtures(long leagueId) {
////              I. 1-8, 2-7,3-6,4-5
////            II. 1-7, 8-6, 2-5, 3-4
////            III. 1-6, 7-5, 8-4, 2-3
////            IV. 1-5, 6-4, 7-3, 8-2
////            V. 1-4, 5-3, 6-2, 7-8
////            VI. 1-3, 4-2, 5-8, 6-7
////            VII. 1-2, 3-8, 4-7, 5-6
        League league=this.leagueRepository.findAllById(leagueId);
        Map<Integer, List<Game>> roundsWithGames=new HashMap<>();
        List<Club> clubsList = this.clubRepository.findByClubLeagueId(leagueId);
        Club clubA = clubsList.get(0);
        Club clubB = clubsList.get(1);
        Club clubC = clubsList.get(2);
        Club clubD = clubsList.get(3);
        Club clubE = clubsList.get(4);
        Club clubF = clubsList.get(5);
        Club clubG = clubsList.get(6);
        Club clubH = clubsList.get(7);

        Game[][] leagueFixtures = {
                {new Game(clubA, clubH, GameType.LG, leagueId),
                        new Game(clubB, clubG, GameType.LG, leagueId),
                        new Game(clubC, clubF, GameType.LG, leagueId),
                        new Game(clubD, clubE, GameType.LG, leagueId)},
                {new Game(clubA, clubG, GameType.LG, leagueId),
                        new Game(clubH, clubF, GameType.LG, leagueId),
                        new Game(clubB, clubE, GameType.LG, leagueId),
                        new Game(clubC, clubD, GameType.LG, leagueId)},
                {new Game(clubA, clubF, GameType.LG, leagueId),
                        new Game(clubG, clubE, GameType.LG, leagueId),
                        new Game(clubH, clubD, GameType.LG, leagueId),
                        new Game(clubB, clubC, GameType.LG, leagueId)},
                {new Game(clubA, clubE, GameType.LG, leagueId),
                        new Game(clubF, clubD, GameType.LG, leagueId),
                        new Game(clubG, clubC, GameType.LG, leagueId),
                        new Game(clubH, clubB, GameType.LG, leagueId)},
                {new Game(clubA, clubD, GameType.LG, leagueId),
                        new Game(clubE, clubC, GameType.LG, leagueId),
                        new Game(clubF, clubB, GameType.LG, leagueId),
                        new Game(clubG, clubH, GameType.LG, leagueId)},
                {new Game(clubA, clubC, GameType.LG, leagueId),
                        new Game(clubD, clubB, GameType.LG, leagueId),
                        new Game(clubE, clubH, GameType.LG, leagueId),
                        new Game(clubF, clubG, GameType.LG, leagueId)},
                {new Game(clubA, clubB, GameType.LG, leagueId),
                        new Game(clubC, clubH, GameType.LG, leagueId),
                        new Game(clubD, clubG, GameType.LG, leagueId),
                        new Game(clubE, clubF, GameType.LG, leagueId)}
        };
        List<Game> leagueAllGames = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 4; j++) {
                List<Club> gameClubs=new ArrayList<>();
                gameClubs.add(leagueFixtures[i][j].getHostClub());
                gameClubs.add(leagueFixtures[i][j].getGuestClub());
                Game game =leagueFixtures[i][j];
                game.setGameClubs(gameClubs);
                this.gameRepository.save(game);
//                leagueFixtures[i][j].setGameClubs(gameClubs);
                leagueAllGames.add(game);
                league.setLeagueAllGames(leagueAllGames);
                this.leagueRepository.save(league);
            }
            roundsWithGames.put(i, leagueAllGames);
        }
//        this.leagueRepository.findAllById(leagueId).setLeagueFixtures(rounds);
//        this.leagueRepository.save(this.leagueRepository.findAllById(leagueId));
        return roundsWithGames;
    }

}

