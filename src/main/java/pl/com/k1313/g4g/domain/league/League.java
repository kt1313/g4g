package pl.com.k1313.g4g.domain.league;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.match.Game;

import javax.persistence.*;
import java.util.HashMap;
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

    @OneToMany
    private List<Club> leagueTeams;

    private HashMap<Integer, List<Game>> leagueFixtures;

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

    public void setLeagueRound(int leagueRound) {
        this.leagueRound = leagueRound;
    }

    public void setLeagueFixtures(HashMap<Integer, List<Game>> leagueFixtures) {
        this.leagueFixtures = leagueFixtures;
    }

    public HashMap<Integer, List<Game>> getLeagueFixtures() {
        return leagueFixtures;
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
