package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.appuser.AppUserRepository;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.league.League;
import pl.com.k1313.g4g.domain.league.LeagueRepository;
import pl.com.k1313.g4g.domain.league.LeagueService;
import pl.com.k1313.g4g.domain.match.Game;
import pl.com.k1313.g4g.domain.match.GameRepository;
import pl.com.k1313.g4g.domain.match.GameService;
import pl.com.k1313.g4g.domain.match.GameType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/game")
public class GameController {
    private GameRepository gameRepository;
    private GameService gameService;

    private AppUserRepository appUserRepository;
    private ClubRepository clubRepository;
    private LeagueRepository leagueRepository;
    private LeagueService leagueService;


    @Autowired
    public GameController(GameRepository gameRepository,
                          GameService gameService,
                          AppUserRepository appUserRepository,
                          ClubRepository clubRepository,
                          LeagueRepository leagueRepository,
                          LeagueService leagueService) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.appUserRepository = appUserRepository;
        this.clubRepository = clubRepository;
        this.leagueRepository = leagueRepository;
        this.leagueService=leagueService;
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

    @PostMapping("/leaguefixtures/{leagueId}")
    public String leagueGames(@PathVariable long leagueId, Model model) {
//        List<Game> leagueGames = this.gameRepository.findAllByLeagueId(leagueId);
        //jestem tutaj, do zrobienia templatka oraz kontroller tutaj
        HashMap<Integer, List<Game>> rounds = this.leagueService.createGamesFixtures(leagueId);
       model.addAttribute("rounds", rounds);
        return "leaguefixtures";
    }



    @PostMapping("/test")
    public String appUserPage(String appusertimestamp, Long clubId, Model model) {
        String test = appusertimestamp;
        model.addAttribute("appusertimestamp", appusertimestamp);
        model.addAttribute("clubId", clubId);

        return "test";
    }
}
