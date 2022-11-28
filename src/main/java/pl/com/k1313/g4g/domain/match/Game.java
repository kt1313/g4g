package pl.com.k1313.g4g.domain.match;

import lombok.Data;
import pl.com.k1313.g4g.domain.club.Club;

import javax.persistence.*;
import java.util.List;
@Data
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToMany
    private List<Club> gameClubs;

    private int hostScore;
    private int guestScore;
    private boolean isPenaltyScore;
    private boolean inProgress;



    public Game() {
    }

    public Game(List<Club> gameClubs, boolean inProgress) {
        this.gameClubs = gameClubs;
        this.inProgress = inProgress;
    }
}
