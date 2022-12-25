package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.appuser.AppUserRepository;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;
import pl.com.k1313.g4g.domain.league.League;
import pl.com.k1313.g4g.domain.league.LeagueRepository;
import pl.com.k1313.g4g.domain.league.LeagueService;
import pl.com.k1313.g4g.domain.match.Game;
import pl.com.k1313.g4g.domain.match.GameRepository;
import pl.com.k1313.g4g.domain.match.GameService;
import pl.com.k1313.g4g.domain.match.GameType;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/game")
public class GameController {
    private GameRepository gameRepository;
    private GameService gameService;

    private AppUserRepository appUserRepository;
    private ClubRepository clubRepository;
    private ClubService clubService;
    private LeagueRepository leagueRepository;
    private LeagueService leagueService;


    @Autowired
    public GameController(GameRepository gameRepository,
                          GameService gameService,
                          AppUserRepository appUserRepository,
                          ClubRepository clubRepository,
                          ClubService clubService,
                          LeagueRepository leagueRepository,
                          LeagueService leagueService) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.appUserRepository = appUserRepository;
        this.clubRepository = clubRepository;
        this.clubService = clubService;
        this.leagueRepository = leagueRepository;
        this.leagueService = leagueService;
    }

    //tutaj stworzyc najpierw cos co utworzy Game z Id, zapisze do Repo, a potem
    //rozpocznie Game, a potem nowy POstMapping i bedzie do Game mozna wrocic w kazdym momencie
    @PostMapping("/playgame")
    public String handleGame(String appusertimestamp, Long clubId, GameType gameType, ModelMap map, Model m) throws InterruptedException {
        //ma pobrac JUŻ utworzony match z druzynami - nie. tylko z Id klubu wyzwanego, a klub wyzywajacego z ...?
        //no, skad?
        AppUser appUser = this.appUserRepository.findByTimeStampAppUser(appusertimestamp);
        Club hostClub = this.clubRepository.findByAppUser(appUser);
        Club guestClub = this.clubRepository.findByClubId(clubId);
        List<Club> gameClubs = new ArrayList<>(List.of(hostClub, guestClub));

        Optional<Game> playGameOptional = this.gameRepository.findFirstByGameClubsInAndInProgress(gameClubs, Boolean.TRUE);
        Game playGame = new Game();
        playGame.setGameType(gameType);
        if (playGameOptional.isPresent()) {
            playGame = playGameOptional.get();
        } else {
            playGame.setGameClubs(gameClubs);
            playGame.setInProgress(Boolean.TRUE);
        }
        if (playGame.getGameType().equals(GameType.LG)) {
            playGame.setLeagueId(hostClub.getClubLeague().getId());
        }

        this.gameRepository.save(playGame);
        //ma teraz ROZEGRAC ten mecz
        HashMap<Integer, String> matchCommentary = this.gameService.handleGameEngine(playGame);
        map.addAttribute("matchCommentary", matchCommentary);

        //tu naglowek, nazwy druzyn i wynik
        String hostClubName = hostClub.getClubName();
        String guestClubName = guestClub.getClubName();
        Integer hostClubScore = playGame.getHostScore();
        Integer guestClubScore = playGame.getGuestScore();
        m.addAttribute("clubId", clubId);
        m.addAttribute("appusertimestamp", appusertimestamp);
        m.addAttribute("hostClubName", hostClubName);
        m.addAttribute("guestClubName", guestClubName);
        m.addAttribute("hostClubScore", hostClubScore);
        m.addAttribute("guestClubScore", guestClubScore);

        return "gameview";
    }

    @PostMapping("/playleagueround")
    public String playLeagueRound(long leagueId, String appusertimestamp, Model model) throws InterruptedException {
        League league = this.leagueRepository.findAllById(leagueId);
        List<List<Game>> rounds = new ArrayList<>();
        rounds.add(league.getLeagueAllGames().stream().limit(4).collect(Collectors.toList()));
        rounds.add(league.getLeagueAllGames().stream().skip(4).limit(4).collect(Collectors.toList()));
        rounds.add(league.getLeagueAllGames().stream().skip(8).limit(4).collect(Collectors.toList()));
        rounds.add(league.getLeagueAllGames().stream().skip(12).limit(4).collect(Collectors.toList()));
        rounds.add(league.getLeagueAllGames().stream().skip(16).limit(4).collect(Collectors.toList()));
        rounds.add(league.getLeagueAllGames().stream().skip(20).limit(4).collect(Collectors.toList()));
        rounds.add(league.getLeagueAllGames().stream().skip(24).limit(4).collect(Collectors.toList()));
        int roundToPlay = this.gameService.findRoundToPlay(rounds, leagueId);
        List<String> errors = new ArrayList<>();
        List<Club> leagueSortedByPointsAndGoalsDiff;

        model.addAttribute("appusertimestamp", appusertimestamp);
        model.addAttribute("leagueId", leagueId);
        model.addAttribute("league", league);
        model.addAttribute("leaguerounds", rounds);

        if (roundToPlay == 0) {
            errors.add("All rounds have been already played!");
        }
        if (errors.isEmpty()) {
            for (Game g : rounds.get(roundToPlay - 1)
            ) {
                this.gameService.handleGameEngine(g);
            }
            leagueSortedByPointsAndGoalsDiff = this.clubService.sortingByPointsAndGoalsDiff(leagueId);
            List<Game> leagueGames = league.getLeagueAllGames();
            model.addAttribute("leaguegames", leagueGames);
            model.addAttribute("clubslistsorted", leagueSortedByPointsAndGoalsDiff);
            return "league";
        } else
            leagueSortedByPointsAndGoalsDiff = this.clubService.sortingByPointsAndGoalsDiff(leagueId);
        List<Game> leagueGames = league.getLeagueAllGames();
        model.addAttribute("leaguegames", leagueGames);
        model.addAttribute("clubslistsorted", leagueSortedByPointsAndGoalsDiff);
        return "league";
    }

    @PostMapping("/test")
    public String appUserPage(String appusertimestamp, Long clubId, Model model) {
        String test = appusertimestamp;
        model.addAttribute("appusertimestamp", appusertimestamp);
        model.addAttribute("clubId", clubId);

        return "test";
    }
}
