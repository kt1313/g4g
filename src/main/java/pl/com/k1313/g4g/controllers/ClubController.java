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
        this.gameRepository=gameRepository;
    }

    @GetMapping("/takeover")
    public String teakeoverTeam() {
        return "registrationStepOne";
    }

    @GetMapping("/league/{leagueId}/{appusertimestamp}")
    public String league(@PathVariable long leagueId, @PathVariable String appusertimestamp, Model model) {
        League league = this.leagueRepository.findAllById(leagueId);
        List<Game> leagueGames=this.gameRepository.findAllByLeagueId(leagueId);
        List<Club> clubsSortedByPointsAndGoalsDiff=this.clubService.sortingByPointsAndGoalsDiff(leagueId);
        List<Game> round1 = league.getLeagueAllGames().stream().limit(4).collect(Collectors.toList());
        List<Game> round2 = league.getLeagueAllGames().stream().skip(4).limit(4).collect(Collectors.toList());
        List<Game> round3 = league.getLeagueAllGames().stream().skip(8).limit(4).collect(Collectors.toList());
        List<Game> round4 = league.getLeagueAllGames().stream().skip(12).limit(4).collect(Collectors.toList());
        List<Game> round5 = league.getLeagueAllGames().stream().skip(16).limit(4).collect(Collectors.toList());
        List<Game> round6 = league.getLeagueAllGames().stream().skip(20).limit(4).collect(Collectors.toList());
        List<Game> round7 = league.getLeagueAllGames().stream().skip(24).limit(4).collect(Collectors.toList());
        Map<Integer, List<Game>> allRounds = new TreeMap<>();
        allRounds.put(1, round1);
        allRounds.put(2, round2);
        allRounds.put(3, round3);
        allRounds.put(4, round4);
        allRounds.put(5, round5);
        allRounds.put(6, round6);
        allRounds.put(7, round7);

        model.addAttribute("allrounds", allRounds);
        model.addAttribute("gamesinround1", round1);
        model.addAttribute("gamesinround2", round2);
        model.addAttribute("gamesinround3", round3);
        model.addAttribute("gamesinround4", round4);
        model.addAttribute("gamesinround5", round5);
        model.addAttribute("gamesinround6", round6);
        model.addAttribute("gamesinround7", round7);

        model.addAttribute("leaguegames", leagueGames);
        model.addAttribute("league", league);
        model.addAttribute("clubslistsorted", clubsSortedByPointsAndGoalsDiff);
        model.addAttribute("appusertimestamp", appusertimestamp);
        return "league";
    }

    @PostMapping("/firstsquadplayers")
    public String handleFirstSquad(@RequestParam(value = "firstSquadPlayer", required = false) List<String> ids,
                                   @RequestParam(value = "clubId", required = true) String stringClubId,
                                   @RequestParam(value = "createnewplayerposition") List<String> stringPlayerPos,
                                   Model model, HttpSession session, HttpServletRequest request) {

//        System.out.println("pozycja"+stringPlayerPos);
        //musi w players po wybraniu pozycji kazdemu playerowi
        //isc do kazdego z osobna playera i mu zmienic pozycje i save w repo zrobic
        List <String> notemptyPlayerPos=new ArrayList<>();
        for (String p:stringPlayerPos
        ) {
            if(!p.equals("0"))
                notemptyPlayerPos.add(p);
        }
        List<PlayerPosition> playerPositions=new ArrayList<>(notemptyPlayerPos.size());
        for (int i=0; i< notemptyPlayerPos.size();i++){
            playerPositions.add(PlayerPosition.valueOf(notemptyPlayerPos.get(i)));}
        try {
            long clubId = Long.parseLong(stringClubId);

            if (!ids.isEmpty()) {
                this.playerService.confirmFirst11(ids, clubId);
                Club club = this.clubRepository.findByClubId(clubId);
//                TUTAJ SPRAWDZ, LECI TYLKO PO GOALKEEPERZE
                List<Player> firstsquadplayers = this.clubService.findFirst11Players(club);
                for (int i=0; i< firstsquadplayers.size();i++){
                    firstsquadplayers.get(i).setPlayerPosition(playerPositions.get(i));
                    this.playerRepository.save(firstsquadplayers.get(i));
                }
//                System.out.println("Dane o zawodniku, Imie: "
//                        + firstsquadplayers.stream().findFirst().get().getFirstName()
//                        + " pozycja: "
//                        + firstsquadplayers.stream().findFirst().get().getPlayerPosition()) ;


                Optional<Player> goalkeeper = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.GK))
                        .findFirst();
                goalkeeper.ifPresent(p -> model.addAttribute("goalkeeper", goalkeeper));
                System.out.println("goalkeeper"+goalkeeper);

                Optional<Player> rightWingback = firstsquadplayers.stream()
                        .filter(p -> p.getPlayerPosition().equals(PlayerPosition.RWB))
                        .findFirst();
                rightWingback.ifPresent(p -> model.addAttribute("rightWingback", rightWingback));
                System.out.println("rightWingback"+rightWingback);

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
                return "redirect:/players";
            }
        } catch (NumberFormatException ex) {
            // handle your exception
        }
        return "redirect:/players";
    }
}