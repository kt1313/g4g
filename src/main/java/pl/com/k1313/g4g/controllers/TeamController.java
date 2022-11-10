package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;
import pl.com.k1313.g4g.domain.team.TeamRepository;
import pl.com.k1313.g4g.domain.team.TeamService;

@Controller
@RequestMapping("/team")
public class TeamController {
    private PlayerRepository playerRepository;
    private TeamRepository teamRepository;
    private PlayerService playerService;
    private TeamService teamService;

    @Autowired
    public TeamController(
            PlayerRepository playerRepository, PlayerService playerService,
            TeamRepository teamRepository, TeamService teamService) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.teamRepository = teamRepository;
        this.teamService = teamService;
    }
    @GetMapping("/takeover")
    public String teakeoverTeam() {
        return "registrationStepOne";
    }

    @GetMapping
    public String teamhomepage(){return "appuser";}

}
