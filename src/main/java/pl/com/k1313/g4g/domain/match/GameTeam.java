package pl.com.k1313.g4g.domain.match;

import lombok.Data;
import pl.com.k1313.g4g.domain.player.Player;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Data
@Entity
public class GameTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String teamName;
    private int attack;
    private int midfield;
    private int defence;

    private int goalkeeperSkill;
    @OneToMany
    private List<Player> matchTeamPlayers = new ArrayList<Player>();

    public GameTeam() {
    }

    public GameTeam(String teamName, int attack, int midfield, int defence, int goalkeeperSkill) {

        this.teamName = teamName;
        this.attack = attack;
        this.midfield = midfield;
        this.defence = defence;
        this.goalkeeperSkill = goalkeeperSkill;
    }

    public GameTeam(long id, String teamName, int attack, int midfield, int defence, int goalkeeperSkill) {

        this.id = id;
        this.teamName = teamName;
        this.attack = attack;
        this.midfield = midfield;
        this.defence = defence;
        this.goalkeeperSkill = goalkeeperSkill;
    }

    public GameTeam(String teamName) {
        this.teamName = teamName;
    }

    public GameTeam(String teamName, List<Player> matchTeamPlayers) {
        this.teamName = teamName;
        this.matchTeamPlayers = matchTeamPlayers;
    }
}
