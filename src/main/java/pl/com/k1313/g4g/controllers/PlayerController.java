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
import pl.com.k1313.g4g.domain.player.PlayerPosition;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;
import pl.com.k1313.g4g.domain.player.dto.PlayerUpdateDTO;
import pl.com.k1313.g4g.domain.club.ClubService;

import java.util.List;
import java.util.Optional;

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
boolean botUser=!teamUser.equals(this.appUserRepository.findByTimeStampAppUser(appusertimestamp));
        String teamUserTimeStamp = teamUser.getTimeStampAppUser();
        model.addAttribute("appusertimestamp", appusertimestamp);
        model.addAttribute("botUser", botUser);
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
        boolean botUser=!teamUser.equals(this.appUserRepository.findByTimeStampAppUser(appUserTimeStamp));
        String teamUserTimeStamp = teamUser.getTimeStampAppUser();

        List<Player> sortedPlayers = this.playerRepository.findAllByPlayerClub(club);
        this.playerService.sortPlayersBy(sortplayers, sortedPlayers);
        model.addAttribute("appusertimestamp", appUserTimeStamp);
        model.addAttribute("botUser", botUser);
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
//obsluga auto ustawienia first11
@PostMapping("/autofirst11")
    public String handleAutoSetUpFirst11(long clubId, Model model){
        List <Player>firstsquadplayers=this.playerService.autoSetUpFirst11(clubId);
    Optional<Player> goalkeeper = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.GK))
            .findFirst();
    goalkeeper.ifPresent(p -> model.addAttribute("goalkeeper", goalkeeper));
    System.out.println("goalkeeper" + goalkeeper);

    Optional<Player> rightWingback = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.RWB))
            .findFirst();
    rightWingback.ifPresent(p -> model.addAttribute("rightWingback", rightWingback));
    System.out.println("rightWingback" + rightWingback);

    Optional<Player> rightCentreback = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.RCB))
            .findFirst();
    rightCentreback.ifPresent(p -> model.addAttribute("rightCentreback", rightCentreback));

    Optional<Player> centreback = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.CB))
            .findFirst();
    centreback.ifPresent(p -> model.addAttribute("centreback", centreback));

    Optional<Player> leftCentreback = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.LCB))
            .findFirst();
    leftCentreback.ifPresent(p -> model.addAttribute("leftCentreback", leftCentreback));

    Optional<Player> leftWingback = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.LWB))
            .findFirst();
    leftWingback.ifPresent(p -> model.addAttribute("leftWingback", leftWingback));

    Optional<Player> rightWinger = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.RW))
            .findFirst();
    rightWinger.ifPresent(p -> model.addAttribute("rightWinger", rightWinger));

    Optional<Player> centreMidDef = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.CMD))
            .findFirst();
    centreMidDef.ifPresent(p -> model.addAttribute("centreMidDef", centreMidDef));

    Optional<Player> centreMid = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.CM))
            .findFirst();
    centreMid.ifPresent(p -> model.addAttribute("centreMid", centreMid));

    Optional<Player> centreMidAtt = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.CMA))
            .findFirst();
    centreMidAtt.ifPresent(p -> model.addAttribute("centreMidAtt", centreMidAtt));

    Optional<Player> leftWinger = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.LW))
            .findFirst();
    leftWinger.ifPresent(p -> model.addAttribute("leftWinger", leftWinger));

    Optional<Player> leftForward = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.LF))
            .findFirst();
    leftForward.ifPresent(p -> model.addAttribute("leftForward", leftForward));

    Optional<Player> centreForward = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.CF))
            .findFirst();
    centreForward.ifPresent(p -> model.addAttribute("centreForward", centreForward));

    Optional<Player> rightForward = firstsquadplayers.stream()
            .filter(p -> p.getPlayerPosition().equals(PlayerPosition.RF))
            .findFirst();
    rightForward.ifPresent(p -> model.addAttribute("rightForward", rightForward));

    model.addAttribute("firstsquadplayers", firstsquadplayers);
    return "firstsquadplayers";
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
