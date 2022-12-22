package pl.com.k1313.g4g.domain.league;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.match.Game;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Setter(value = AccessLevel.NONE)
@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "league")
    private long id;

    private String leagueNumber;
    @ElementCollection
    private final List<Integer> leagueRound = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7));

    @OneToMany
    private List<Club> leagueTeams;

    //    @ElementCollection
////    @CollectionTable(name = "league_fixtures_maping",
////            joinColumns = {@JoinColumn(name = "league_id", referencedColumnName = "id")})
//    @MapKeyColumn(name = "round_number")
//    @Column(name = "games")
//    private Map<Integer, List<Game>> leagueFixtures;
    @OneToMany
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

    public void setLeagueAllGames(List<Game> leagueRoundGames) {
        this.leagueAllGames = leagueRoundGames;
    }

    //    public void setLeagueFixtures(HashMap<Integer, List<Game>> leagueFixtures) {
//        this.leagueFixtures = leagueFixtures;
//    }
//
//    public Map<Integer, List<Game>> getLeagueFixtures() {
//        return leagueFixtures;
//    }

    @Override
    public String toString() {
        return
                id
                        + " LeagueTeams "
//                        + new ArrayList<>(getLeagueTeams()) + '\''
                ;
    }

}
