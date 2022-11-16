package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerPosition;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/team")
public class ClubController {
    private PlayerRepository playerRepository;
    private ClubRepository clubRepository;
    private PlayerService playerService;
    private ClubService clubService;

    @Autowired
    public ClubController(
            PlayerRepository playerRepository, PlayerService playerService,
            ClubRepository clubRepository, ClubService clubService) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.clubRepository = clubRepository;
        this.clubService = clubService;
    }

    @GetMapping("/takeover")
    public String teakeoverTeam() {
        return "registrationStepOne";
    }

    @GetMapping
    public String teamhomepage() {
        return "appuser";
    }

    @PostMapping("/firstsquadplayers")
    public String handleFirstSquad(@RequestParam(value = "firstSquadPlayer", required = false) List<String> ids
            , @RequestParam(value = "clubId", required = true) String stringClubId, Model model, HttpSession session, HttpServletRequest request) {

//        @RequestMapping(value={"/sendAddress"},method = RequestMethod.POST)
//        public String messageCenterHome(Model model,HttpSession session,HttpServletRequest request) {
//
//            String selectedCity= request.getParameter("nameOfCity")
//            //return view
//        }

        //musi w players po wybraniu pozycji kazdemu playerowi
        //isc do kazdego z osobna playera i mu zmienic pozycje i save w repo zrobic
        String playerPos=request.getParameter("position");
        try {
            long clubId = Long.parseLong(stringClubId);

            if (!ids.isEmpty()) {
                this.playerService.confirmFirst11(ids);
                Club club = this.clubRepository.findByClubId(clubId);
                List<Player> firstsquadplayers = this.clubService.setUpFirstEleven(club);

                 Optional<Player> goalkeeper = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.GK))
                        .findFirst();
                goalkeeper.ifPresent(windback -> model.addAttribute("goalkeeper", goalkeeper));


                Optional<Player> rightWingback = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.RWB))
                        .findFirst();
                rightWingback.ifPresent(windback -> model.addAttribute("rightWinback", rightWingback));

                Optional<Player> rightCentreback = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.RCB))
                        .findFirst();
                rightCentreback.ifPresent(windback -> model.addAttribute("rightCentreback", rightCentreback));

                Optional<Player> centreback = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.CB))
                        .findFirst();
                centreback.ifPresent(windback -> model.addAttribute("rightCentreback", rightCentreback));

                Optional<Player> leftCentreback = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.LCB))
                        .findFirst();
                leftCentreback.ifPresent(windback -> model.addAttribute("leftCentreback", leftCentreback));

                Optional<Player> leftWingback = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.LWB))
                        .findFirst();
                leftWingback.ifPresent(windback -> model.addAttribute("leftWingback", leftWingback));

                Optional<Player> rightWinger = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.RW))
                        .findFirst();
                rightWinger.ifPresent(windback -> model.addAttribute("rightWinger", rightWinger));

                Optional<Player> centreMidDef = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.CMD))
                        .findFirst();
                centreMidDef.ifPresent(windback -> model.addAttribute("centreMidDef", centreMidDef));

                Optional<Player> centreMid = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.CM))
                        .findFirst();
                centreMid.ifPresent(windback -> model.addAttribute("centreMid", centreMid));

                Optional<Player> centreMidAtt = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.CMA))
                        .findFirst();
                centreMidAtt.ifPresent(windback -> model.addAttribute("centreMidAtt", centreMidAtt));

                Optional<Player> leftWinger = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.LW))
                        .findFirst();
                leftWinger.ifPresent(windback -> model.addAttribute("leftWinger", leftWinger));

                Optional<Player> leftForward = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.LF))
                        .findFirst();
                leftForward.ifPresent(windback -> model.addAttribute("leftForward", leftForward));

                Optional<Player> centreForward = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.CF))
                        .findFirst();
                centreForward.ifPresent(windback -> model.addAttribute("centreForward", centreForward));

                Optional<Player> rightForward = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.RF))
                        .findFirst();
                rightForward.ifPresent(windback -> model.addAttribute("rightForward", rightForward));


                model.addAttribute("firstsquadplayers", firstsquadplayers);
                return "firstsquadplayers";
            } else {
                return "redirect:/players";
            }
        } catch (NumberFormatException ex) {
            // handle your exception
        }
        return "redirect:/players";
    }
}
