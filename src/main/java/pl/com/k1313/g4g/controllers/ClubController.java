package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.league.League;
import pl.com.k1313.g4g.domain.league.LeagueRepository;

import pl.com.k1313.g4g.domain.match.Game;
import pl.com.k1313.g4g.domain.match.GameRepository;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerPosition;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/team")
public class ClubController {
    private PlayerRepository playerRepository;
    private ClubRepository clubRepository;
    private PlayerService playerService;
    private ClubService clubService;
    private LeagueRepository leagueRepository;
    private GameRepository gameRepository;

    @Autowired
    public ClubController(
            PlayerRepository playerRepository, PlayerService playerService,
            ClubRepository clubRepository, ClubService clubService,
            LeagueRepository leagueRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.clubRepository = clubRepository;
        this.clubService = clubService;
        this.leagueRepository = leagueRepository;
        this.gameRepository = gameRepository;
    }

    @GetMapping("/takeover")
    public String teakeoverTeam() {
        return "registrationStepOne";
    }

    @GetMapping("/league/{leagueId}/{appusertimestamp}")
    public String league(@PathVariable long leagueId, @PathVariable String appusertimestamp, Model model) {
        League league = this.leagueRepository.findAllById(leagueId);
//        List<Game> leagueGames1=this.gameRepository.findAllByLeagueId(leagueId);
        List<Game> leagueGames = league.getLeagueAllGames();
        List<Club> clubsSortedByPointsAndGoalsDiff = this.clubService.sortingByPointsAndGoalsDiff(leagueId);
        List<Integer> leaguerounds = league.getLeagueRound();

        model.addAttribute("leaguegames", leagueGames);
        model.addAttribute("leaguerounds", leaguerounds);
        model.addAttribute("league", league);
        model.addAttribute("clubslistsorted", clubsSortedByPointsAndGoalsDiff);
        model.addAttribute("appusertimestamp", appusertimestamp);
        return "league";
    }


    @PostMapping("/firstsquadplayers")
//            poprawa zmiany pozycji u zawodnika
    public String handleFirstSquad(@RequestParam(value = "firstSquadPlayer", required = false) List<String> ids,
                                   @RequestParam(value = "clubId") String stringClubId,
                                   @RequestParam(value = "sortplayersbypos", required = false) String sortPlayersByPos,
                                   @RequestParam(value = "createnewplayerposition", required = false) List<String> stringPlayerPos,
                                   String appusertimestamp, Model model) {

        long clubId = Long.parseLong(stringClubId);
        Club club = this.clubRepository.findByClubId(clubId);
        String clubName=club.getClubName();
        List<Player> players=this.playerRepository.findAllByPlayerClub(club);
        List<String> duplicatePosErrorList = new ArrayList<>();
        for (String s : stringPlayerPos
        ) {
            int a = 0;
            if (!s.equals("0")) {
                a = Collections.frequency(stringPlayerPos, s);
            }
            if (a > 1) {
                duplicatePosErrorList.add("One of Squad position is duplicated");
                break;
            }
        }
        if (duplicatePosErrorList.isEmpty()) {
            this.playerService.assignPlayerPosition(clubId, ids, stringPlayerPos, sortPlayersByPos);

            List<Player> firstsquadplayers = this.clubService.findFirst11Players(club);
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
        } else {
            model.addAttribute("duplicatePosErrorList", duplicatePosErrorList);
            model.addAttribute("appusertimestamp", appusertimestamp);
            model.addAttribute("clubId", clubId);
            model.addAttribute("clubname",clubName);
            model.addAttribute("players",players);
            return "players";
        }
    }
}