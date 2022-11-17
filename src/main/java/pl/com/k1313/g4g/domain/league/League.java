package pl.com.k1313.g4g.domain.league;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerPosition;

import javax.persistence.*;
import java.util.List;

@Data
@Setter(value = AccessLevel.NONE)
@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String leagueNumber;

    private int leagueRound;

    @OneToOne
    private LeagueTable leagueTable;
    @OneToMany
    private List<Club> leagueTeams;

    public League() {

    }

    public League(String leagueNumber, List<Club> leagueTeams) {
        this.leagueNumber = leagueNumber;
        this.leagueTeams = leagueTeams;
    }

    public List<Club> getLeagueTeams() {
        return leagueTeams;
    }
}
