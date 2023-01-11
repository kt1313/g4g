package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.appuser.AppUserRepository;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;
import pl.com.k1313.g4g.domain.player.dto.PlayerUpdateDTO;
import pl.com.k1313.g4g.domain.club.ClubService;

import java.util.List;

@Controller
@RequestMapping("/players")
public class PlayerController {

    private PlayerService playerService;
    private ClubService clubService;
    private ClubRepository clubRepository;
    private PlayerRepository playerRepository;
    private AppUserRepository appUserRepository;

    @Autowired
    public PlayerController(PlayerRepository playerRepository,
                            PlayerService playerService,
                            ClubService clubService,
                            ClubRepository clubRepository,
                            AppUserRepository appUserRepository
    ) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.clubService = clubService;
        this.clubRepository = clubRepository;
        this.appUserRepository = appUserRepository;
    }

    //unit test done- not working
    @GetMapping("/{clubId}/{appusertimestamp}")
    public String players(@PathVariable long clubId, @PathVariable String appusertimestamp, Model model) {
        Club club = this.clubRepository.findByClubId(clubId);
        AppUser teamUser = this.appUserRepository.findByClubId(clubId);
//        AppUser teamUser2 = this.appUserRepository.findByClubname(club.getClubName());
        String teamUserTimeStamp = teamUser.getTimeStampAppUser();
        model.addAttribute("appusertimestamp", appusertimestamp);
        model.addAttribute("teamUserTimeStamp", teamUserTimeStamp);
        model.addAttribute("players", this.playerRepository.findAllByPlayerClub(club));
        model.addAttribute("clubId", clubId);
        model.addAttribute("clubName", club.getClubName());
        return "players";
    }

    @PostMapping("/sortedby")
    public String sortPlayersBy(@RequestParam(value = "firstSquadPlayer", required = false) List<String> ids,
                                @RequestParam(value = "clubId") String stringClubId,
                                @RequestParam(value = "appusertimestamp") String appUserTimeStamp,
                                String sortplayers,
                                @RequestParam(value = "createnewplayerposition", required = false) List<String> stringPlayerPos,
                                Model model) {
        long clubId = Long.parseLong(stringClubId);
        Club club = this.clubRepository.findByClubId(clubId);

        AppUser teamUser = this.appUserRepository.findByClubId(clubId);
        String teamUserTimeStamp = teamUser.getTimeStampAppUser();

        List<Player> sortedPlayers = this.playerRepository.findAllByPlayerClub(club);
        this.playerService.sortPlayersBy(sortplayers, sortedPlayers);
        model.addAttribute("appusertimestamp", appUserTimeStamp);
        model.addAttribute("teamUserTimeStamp", teamUserTimeStamp);
        model.addAttribute("sortplayersbypos", sortplayers);
        model.addAttribute("players", sortedPlayers);
        model.addAttribute("clubId", clubId);
        model.addAttribute("clubname", club.getClubName());

        return "/players";
    }

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
                updatedPlayer.getPlayersTeam(),
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
//    @PostMapping("/firstsquadplayers")
//    public String handleFirstSquad(@RequestParam(value = "firstSquadPlayer", required = false) List<String> ids
//            , Model model) {
//        if (!ids.isEmpty()) {
//            this.playerService.confirmFirst11(ids);
//            List<Player> firstsquadplayers=this.clubService.setUpFirstEleven(Club club);
//
//            model.addAttribute("firstsquadplayers", firstsquadplayers);
//            String[][] first11FinalTable = this.clubService.setUpFirst11(firstsquadplayers);
//            model.addAttribute("first11FinalTable", first11FinalTable);
//
//            return "firstsquadplayers";
//        } else {
//            return "redirect:/players";
//        }
//    }
}
