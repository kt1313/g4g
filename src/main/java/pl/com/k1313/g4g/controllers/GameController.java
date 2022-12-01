package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.match.Game;
import pl.com.k1313.g4g.domain.match.GameRepository;
import pl.com.k1313.g4g.domain.match.GameService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/game")
public class GameController {
    private GameRepository gameRepository;
    private GameService gameService;


    @Autowired
        public GameController (GameRepository gameRepository, GameService gameService){
        this.gameRepository=gameRepository;
        this.gameService=gameService;

    }
    //tutaj stworzyc najpierw cos co utworzy Game z Id, zapisze do Repo, a potem
    //rozpocznie Game, a potem nowy POstMapping i bedzie do Game mozna wrocic w kazdym momencie
    @PostMapping("/play")
    public String handleGame(Club hostClub, Club guestClub, ModelMap map, Model m) throws InterruptedException {
        //ma pobrac JUŻ utworzony match z druzynami
        List<Club> gameClubs=new ArrayList<>(List.of(hostClub, guestClub));
        Optional<Game> playGameOptional=this.gameRepository.findFirstByGameClubsInAndInProgress(gameClubs, Boolean.TRUE);
        Game playGame = new Game();
        if (playGameOptional.isPresent()){
            playGame=playGameOptional.get();
        }

        //ma teraz ROZEGRAC ten mecz
        HashMap<Integer, String> matchCommentary = this.gameService.handleMatchEngine(hostClub, guestClub);
        map.addAttribute("matchCommentary", matchCommentary);

        //tu naglowek, nazwy druzyn i wynik
        String hostClubName = playGame.getGameClubs().get(0).getClubName();
        String guestClubName = playGame.getGameClubs().get(1).getClubName();
        Integer hostClubScore = playGame.getHostScore();
        Integer guestClubScore = playGame.getGuestScore();
        m.addAttribute("hostClubName", hostClubName);
        m.addAttribute("guestClubName", guestClubName);
        m.addAttribute("hostClubScore", hostClubScore);
        m.addAttribute("guestClubScore", guestClubScore);

        return "gameinprogress";
    }
}
