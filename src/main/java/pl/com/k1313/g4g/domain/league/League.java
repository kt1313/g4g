package pl.com.k1313.g4g.domain.league;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.match.Game;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Setter(value = AccessLevel.NONE)
@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String leagueNumber;
    @ElementCollection
    private final List<Integer> leagueRound = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7));

    @OneToMany
    private List<Club> leagueTeams;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Game> leagueAllGames;



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


    public List<Integer> getLeagueRound() {
        return leagueRound;
    }

    public List<Game> getLeagueAllGames() {
        return leagueAllGames;
    }

    public void setLeagueAllGames(List<Game> leagueAllGames) {
        this.leagueAllGames = leagueAllGames;
    }

    @Override
    public String toString() {
        return
                "League Id:"+id   ;
    }

}
