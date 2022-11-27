package pl.com.k1313.g4g.domain.match;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
@Data
@Entity
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToMany
    private List<MatchTeam> matchTeams;

    private int hostScore;
    private int guestScore;
    private boolean isPenaltyScore;
    private boolean inProgress;



    public Match() {
    }

    public Match(List<MatchTeam> matchTeams, boolean inProgress) {
        this.matchTeams = matchTeams;
        this.inProgress = inProgress;
    }
}
