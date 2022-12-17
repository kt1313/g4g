package pl.com.k1313.g4g.domain.match;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import pl.com.k1313.g4g.domain.club.Club;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@Setter(value = AccessLevel.NONE)
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

    public void setGameClubs(List<Club> gameClubs) {
        this.gameClubs = gameClubs;
    }

    public void setHostScore(int hostScore) {
        this.hostScore = hostScore;
    }

    public void setGuestScore(int guestScore) {
        this.guestScore = guestScore;
    }

    public void setPenaltyScore(boolean penaltyScore) {
        isPenaltyScore = penaltyScore;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public Game() {
    }

    public Game(List<Club> gameClubs, boolean inProgress) {
        this.gameClubs = gameClubs;
        this.inProgress = inProgress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return id == game.id && hostScore == game.hostScore && guestScore == game.guestScore && isPenaltyScore == game.isPenaltyScore && inProgress == game.inProgress && Objects.equals(gameClubs, game.gameClubs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gameClubs, hostScore, guestScore, isPenaltyScore, inProgress);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", gameClubs=" + gameClubs +
                ", hostScore=" + hostScore +
                ", guestScore=" + guestScore +
                '}';
    }
}
