package pl.com.k1313.g4g.domain.team;

import lombok.Data;
import pl.com.k1313.g4g.domain.player.Player;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String teamName;
    private String owner;

    @ElementCollection(targetClass = Player.class)
    private List<Player> teamPlayers;

    Team() {
    }

    public Team(String teamName, String owner, List<Player> teamPlayers) {
        this.teamName = teamName;
        this.owner = owner;
        this.teamPlayers = teamPlayers;
    }
}
