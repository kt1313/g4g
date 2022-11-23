package pl.com.k1313.g4g.domain.league;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerPosition;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Setter(value = AccessLevel.NONE)
@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String leagueNumber;

    private int leagueRound;

    //    private LeagueTable leagueTable;
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

    public void setLeagueNumber(String leagueNumber) {
        this.leagueNumber = leagueNumber;
    }

    public void setLeagueTeams(List<Club> leagueTeams) {
        this.leagueTeams = leagueTeams;
    }

    @Override
    public String toString() {
        return
                id
                        + " LeagueTeams "
//                        + new ArrayList<>(getLeagueTeams()) + '\''
                ;
    }

}
