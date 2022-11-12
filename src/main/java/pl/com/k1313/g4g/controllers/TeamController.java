package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;

@Controller
@RequestMapping("/team")
public class TeamController {
    private PlayerRepository playerRepository;
    private ClubRepository clubRepository;
    private PlayerService playerService;
    private ClubService clubService;

    @Autowired
    public TeamController(
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
    public String teamhomepage(){return "appuser";}

}
