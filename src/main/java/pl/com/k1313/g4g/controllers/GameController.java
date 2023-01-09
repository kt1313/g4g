package pl.com.k1313.g4g.controllers;

import net.bytebuddy.dynamic.DynamicType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/viewgame/gameplayed/{id}/{appusertimestamp}")
    public String showGameCommentary(@PathVariable("id") long gameId, @PathVariable(required = false) String appusertimestamp, Model m) {
        List<String> gameCommentaryList;
        Game playGame = this.gameRepository.getById(gameId);
        gameCommentaryList = playGame.getGameCommentaryList();

        float hostBallPossession = (playGame.getHostBallPossession() / (playGame.getHostBallPossession() + playGame.getGuestBallPossession()))*100 ;
        float guestBallPossession = (playGame.getHostBallPossession()  / (playGame.getHostBallPossession()  + playGame.getGuestBallPossession())) * 100;
        int hostShotsOnGoal = playGame.getHostShotsOnGoal();
        int hostCounterAttacks = playGame.getHostCounterAttacks();
        int guestShotsOnGoal = playGame.getGuestShotsOnGoal();
        int guestCounterAttacks = playGame.getGuestCounterAttacks();
        int hostBallPossessionInt=(int)hostBallPossession;
        int guestBallPossessionInt=(int)guestBallPossession;

        String hostClubName = this.gameRepository.findById(gameId).getHostClub().getClubName();
        String guestClubName = this.gameRepository.findById(gameId).getGuestClub().getClubName();
        Integer hostClubScore = playGame.getHostScore();
        Integer guestClubScore = playGame.getGuestScore();
        m.addAttribute("hostBallPossession", hostBallPossessionInt);
        m.addAttribute("hostCounterAttacks", hostCounterAttacks);
        m.addAttribute("hostShotsOnGoal", hostShotsOnGoal);
        m.addAttribute("guestBallPossession", guestBallPossessionInt);
        m.addAttribute("guestShotsOnGoal", guestShotsOnGoal);
        m.addAttribute("guestCounterAttacks", guestCounterAttacks);
        m.addAttribute("gamecommentary", gameCommentaryList);
        m.addAttribute("appusertimestamp", appusertimestamp);
        m.addAttribute("hostClubName", hostClubName);
        m.addAttribute("guestClubName", guestClubName);
        m.addAttribute("hostClubScore", hostClubScore);
        m.addAttribute("guestClubScore", guestClubScore);

        return "gameview";
    }

    //tutaj stworzyc najpierw cos co utworzy Game z Id, zapisze do Repo, a potem
    //rozpocznie Game, a potem nowy POstMapping i bedzie do Game mozna wrocic w kazdym momencie
    @PostMapping("/viegame/inprogress")
    public String handleGame(String appusertimestamp, Long clubId, String gameTypeString,
                             ModelMap map, Model m) throws InterruptedException {
        //ma pobrac JUŻ utworzony match z druzynami - nie.
        // tylko z Id klubu wyzwanego, a klub wyzywajacego z ...?
        //no, skad?
        AppUser appUser = this.appUserRepository.findByTimeStampAppUser(appusertimestamp);
        Club hostClub = this.clubRepository.findByAppUser(appUser);
        Club guestClub = this.clubRepository.findByClubId(clubId);
        List<Club> gameClubs = new ArrayList<>(List.of(hostClub, guestClub));
        Game playGame = new Game();
        switch (gameTypeString) {
            case "friendly":
                playGame.setGameType(GameType.FG);
                break;
            case "cup":
                playGame.setGameType(GameType.CG);
                break;
            case "league":
                playGame.setGameType(GameType.LG);
                break;

        }
        List<String> gameCommentaryList;

        Optional<Game> playGameOptional = this.gameRepository.findFirstByGameClubsInAndInProgress(gameClubs, Boolean.TRUE);
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

        gameCommentaryList = this.gameService.handleGameEngine(playGame);
//pobranie statystyk

        float hostBallPossession = (playGame.getHostBallPossession() / (playGame.getHostBallPossession() + playGame.getGuestBallPossession()))*100 ;
        float guestBallPossession = (playGame.getHostBallPossession()  / (playGame.getHostBallPossession()  + playGame.getGuestBallPossession())) * 100;
        int hostShotsOnGoal = playGame.getHostShotsOnGoal();
        int hostCounterAttacks = playGame.getHostCounterAttacks();
        int guestShotsOnGoal = playGame.getGuestShotsOnGoal();
        int guestCounterAttacks = playGame.getGuestCounterAttacks();
        int hostBallPossessionInt=(int)hostBallPossession;
        int guestBallPossessionInt=(int)guestBallPossession;
        map.addAttribute("gameCommentary", gameCommentaryList);

        //tu naglowek, nazwy druzyn i wynik
        String hostClubName = hostClub.getClubName();
        String guestClubName = guestClub.getClubName();
        Integer hostClubScore = playGame.getHostScore();
        Integer guestClubScore = playGame.getGuestScore();
        m.addAttribute("hostBallPossession", hostBallPossessionInt);
        m.addAttribute("hostCounterAttacks", hostCounterAttacks);
        m.addAttribute("hostShotsOnGoal", hostShotsOnGoal);
        m.addAttribute("guestBallPossession", guestBallPossessionInt);
        m.addAttribute("guestShotsOnGoal", guestShotsOnGoal);
        m.addAttribute("guestCounterAttacks", guestCounterAttacks);
        m.addAttribute("gamecommentary", gameCommentaryList);
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
                this.gameRepository.save(g);
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
