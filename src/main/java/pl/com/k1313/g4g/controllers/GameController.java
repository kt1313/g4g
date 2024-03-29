package pl.com.k1313.g4g.controllers;

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
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerRepository;

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
    private PlayerRepository playerRepository;


    @Autowired
    public GameController(GameRepository gameRepository,
                          GameService gameService,
                          AppUserRepository appUserRepository,
                          ClubRepository clubRepository,
                          ClubService clubService,
                          LeagueRepository leagueRepository,
                          LeagueService leagueService,
                          PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.appUserRepository = appUserRepository;
        this.clubRepository = clubRepository;
        this.clubService = clubService;
        this.leagueRepository = leagueRepository;
        this.leagueService = leagueService;
        this.playerRepository = playerRepository;
    }

    @GetMapping("/viewgame/gameplayed/{id}/{appusertimestamp}")
    public String showGameCommentary(@PathVariable("id") long gameId, @PathVariable(required = false) String appusertimestamp, Model m) {
        List<String> gameCommentaryList;
        Game playGame = this.gameRepository.getById(gameId);
        gameCommentaryList = playGame.getGameCommentaryList();

        AppUser appUser = this.appUserRepository.findByTimeStampAppUser(appusertimestamp);
        Club club = this.clubRepository.findByAppUser(appUser);
        long clubId = club.getClubId();

        float hostBallPossession = (playGame.getHostBallPossession() / (playGame.getHostBallPossession() + playGame.getGuestBallPossession())) * 100;
        float guestBallPossession = (playGame.getHostBallPossession() / (playGame.getHostBallPossession() + playGame.getGuestBallPossession())) * 100;
        int hostShotsOnGoal = playGame.getHostShotsOnGoal();
        int hostCounterAttacks = playGame.getHostCounterAttacks();
        int guestShotsOnGoal = playGame.getGuestShotsOnGoal();
        int guestCounterAttacks = playGame.getGuestCounterAttacks();
        int hostBallPossessionInt = (int) hostBallPossession;
        int guestBallPossessionInt = (int) guestBallPossession;

        String hostClubName = this.gameRepository.findById(gameId).getHostClub().getClubName();
        String guestClubName = this.gameRepository.findById(gameId).getGuestClub().getClubName();
        Integer hostClubScore = playGame.getHostScore();
        Integer guestClubScore = playGame.getGuestScore();
        m.addAttribute("playgame",playGame);
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

    //tutaj stworzyc najpierw cos co utworzy Game z Id, zapisze do Repo, a potem
    //rozpocznie Game, a potem nowy POstMapping i bedzie do Game mozna wrocic w kazdym momencie
    @PostMapping("/viegame/inprogress")
    public String handleGame(String appusertimestamp, Long clubId, String gameTypeString,
                             ModelMap map, Model m) throws InterruptedException {


        AppUser teamUser = this.appUserRepository.findByClubId(clubId);
        boolean botUser = !teamUser.equals(this.appUserRepository.findByTimeStampAppUser(appusertimestamp));
        AppUser appUser = this.appUserRepository.findByTimeStampAppUser(appusertimestamp);
        Club hostClub = this.clubRepository.findByAppUser(appUser);
        Club guestClub = this.clubRepository.findByClubId(clubId);
        List<Player> hostFirsSquadPlayers = this.clubService.findFirst11Players(hostClub);
        List<Player> guestFirsSquadPlayers = this.clubService.findFirst11Players(guestClub);
        List<String> errors = new ArrayList<>();
        String squadError = this.gameService.checkFirstSquad(errors, hostFirsSquadPlayers, guestFirsSquadPlayers);

        if (errors.isEmpty()) {
            List<Club> gameClubs = new ArrayList<>(List.of(hostClub, guestClub));
            Game playGame = new Game();
//            gameTypeString=this.gameService.
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

            float hostBallPossession = (playGame.getHostBallPossession() / (playGame.getHostBallPossession() + playGame.getGuestBallPossession())) * 100;
            float guestBallPossession = (playGame.getHostBallPossession() / (playGame.getHostBallPossession() + playGame.getGuestBallPossession())) * 100;
            int hostShotsOnGoal = playGame.getHostShotsOnGoal();
            int hostCounterAttacks = playGame.getHostCounterAttacks();
            int guestShotsOnGoal = playGame.getGuestShotsOnGoal();
            int guestCounterAttacks = playGame.getGuestCounterAttacks();
            int hostBallPossessionInt = (int) hostBallPossession;
            int guestBallPossessionInt = (int) guestBallPossession;
            map.addAttribute("gameCommentary", gameCommentaryList);

            //tu naglowek, nazwy druzyn i wynik
            String hostClubName = hostClub.getClubName();
            String guestClubName = guestClub.getClubName();
            Integer hostClubScore = playGame.getHostScore();
            Integer guestClubScore = playGame.getGuestScore();
            m.addAttribute("playgame", playGame);
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
        } else {
            if (squadError.equals("host>11") || squadError.equals("host<8")) {
                m.addAttribute("errors", errors);
                m.addAttribute("players", this.playerRepository.findAllByPlayerClub(hostClub));
                m.addAttribute("clubId", hostClub.getClubId());
                m.addAttribute("clubName", hostClub.getClubName());
            } else {
                m.addAttribute("errors", errors);
                m.addAttribute("players", this.playerRepository.findAllByPlayerClub(guestClub));
                m.addAttribute("clubId", guestClub.getClubId());
                m.addAttribute("clubName", guestClub.getClubName());
            }
        }
        m.addAttribute("appusertimestamp", appusertimestamp);
        m.addAttribute("botUser", botUser);
        return "players";
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
                Club hostClub = g.getHostClub();
                Club guestClub = g.getGuestClub();
                List<Player> hostFirsSquadPlayers = this.clubService.findFirst11Players(hostClub);
                List<Player> guestFirsSquadPlayers = this.clubService.findFirst11Players(guestClub);
                String squadError = this.gameService.checkFirstSquad(errors, hostFirsSquadPlayers, guestFirsSquadPlayers);
                if (errors.isEmpty()) {
                    this.gameService.handleGameEngine(g);
                    this.gameRepository.save(g);
                } else {
                    if (squadError.equals("host>11") || squadError.equals("host<8") || squadError.equals(("Host has no Forward in the Squad"))) {
                        model.addAttribute("errors", errors);
                        model.addAttribute("players", this.playerRepository.findAllByPlayerClub(hostClub));
                        model.addAttribute("clubId", hostClub.getClubId());
                        model.addAttribute("clubName", hostClub.getClubName());
                    } else {
                        model.addAttribute("errors", errors);
                        model.addAttribute("players", this.playerRepository.findAllByPlayerClub(guestClub));
                        model.addAttribute("clubId", guestClub.getClubId());
                        model.addAttribute("clubName", guestClub.getClubName());
                    }
                //tutaj zakladamy ze nie jest to botUser, bo First11 w botach ustawia komp(na przyszlosc)
                    boolean botUser =false;
                    model.addAttribute("appusertimestamp", appusertimestamp);
                    model.addAttribute("botUser", botUser);
                    return "players";
                }
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
