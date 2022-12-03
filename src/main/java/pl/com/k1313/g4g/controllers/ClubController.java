package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.league.League;
import pl.com.k1313.g4g.domain.league.LeagueRepository;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerPosition;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/team")
public class ClubController {
    private PlayerRepository playerRepository;
    private ClubRepository clubRepository;
    private PlayerService playerService;
    private ClubService clubService;
    private LeagueRepository leagueRepository;

    @Autowired
    public ClubController(
            PlayerRepository playerRepository, PlayerService playerService,
            ClubRepository clubRepository, ClubService clubService, LeagueRepository leagueRepository) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.clubRepository = clubRepository;
        this.clubService = clubService;
        this.leagueRepository = leagueRepository;
    }

    @GetMapping("/takeover")
    public String teakeoverTeam() {
        return "registrationStepOne";
    }

    @GetMapping("/league/{leagueId}/{appusertimestamp}")
    public String league(@PathVariable long leagueId, @PathVariable String appusertimestamp, Model model) {
        League league = this.leagueRepository.findById(leagueId);
        model.addAttribute("league", league);
        model.addAttribute("appusertimestamp", appusertimestamp);
        return "league";
    }
}
