package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;
import pl.com.k1313.g4g.domain.player.dto.PlayerContractingDTO;
import pl.com.k1313.g4g.domain.player.dto.PlayerUpdateDTO;
import pl.com.k1313.g4g.domain.team.TeamService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/players")
public class PlayerController {

    private PlayerService playerService;
    private TeamService teamService;

    private PlayerRepository playerRepository;

    @Autowired
    public PlayerController(PlayerRepository playerRepository,
                            PlayerService playerService,
                            TeamService teamService
                            ) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

//    @Autowired
//    public TeamService teamService;

    //unit test done- not working
    @GetMapping
    public String players(Model model) {
        model.addAttribute("players",
                this.playerRepository.findAll());
        return "players";
    }

//    @GetMapping("/hire")
//    public String createNewPlayer() {
//        return "playerform";
//    }

//kontraktowanie nowych zawodnkow poznije, bo ma wiele opcji (mlody lub kupno np)
//    @PostMapping
//    public String handleContractNewPlayer
//            (@Valid PlayerContractingDTO playerDTO,
//             BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("errors", result.getAllErrors());
//            return "playerform";
//        } else {
//            this.playerService.contractNewPlayer(playerDTO);
//            return "redirect:/players";
//        }
//    }

    //unit test done-  working
    @GetMapping("/delete/{id}")
    public String firePlayer(@PathVariable("id") Long id) {
        this.playerRepository.deleteById(id);

        return "redirect:/players";
    }

    @GetMapping("/managePlayer/{id}")
    public String editPlayer(@PathVariable Long id, Model model) {
        Player player = this.playerRepository.getById(id);
        model.addAttribute("player", player);
        return "managePlayer";
    }

    //unit test done -  working
    @PostMapping("/managePlayer")
    public String editPlayer(PlayerUpdateDTO updatedPlayer) {
        Player p = new Player(
                updatedPlayer.getFirstName(),
                updatedPlayer.getLastName(),
                updatedPlayer.getAge(),
                updatedPlayer.getPlayerPosition(),
                updatedPlayer.isFirstSquadPlayer(),
                updatedPlayer.getAttacking(),
                updatedPlayer.getBallControl(),
                updatedPlayer.getPassing(),
                updatedPlayer.getTackling(),
                updatedPlayer.getGoalkeeping());
        this.playerRepository.save(p);
        return "redirect:/players";
    }


    //unit test done-  working
    //obsluga powolan do 11
    //pobiera wszystkie checkboxy o nazwie firstsquadplayer i sprawdza czy tickniete
    //wtedy tworzy pierwsza 11
    //a potem tworzy tabele first11FinalTable z wybranymi nazwiskami na odpowiednich pozycjach
    @PostMapping("/firstsquadplayers")
    public String handleFirstSquad(@RequestParam(value = "firstSquadPlayer", required = false) List<String> ids
            , Model model) {
        if (ids != null) {
            List<Player> firstsquadplayers;
            firstsquadplayers = this.playerService.createFirst11(ids);

            model.addAttribute("firstsquadplayers", firstsquadplayers);
            String[][] first11FinalTable = this.teamService.setUpFirst11(firstsquadplayers);
            model.addAttribute("first11FinalTable", first11FinalTable);

            return "firstsquadplayers";
        } else {
            return "redirect:/players";
        }
    }
}
