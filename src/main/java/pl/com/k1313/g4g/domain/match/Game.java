package pl.com.k1313.g4g.domain.match;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import pl.com.k1313.g4g.domain.club.Club;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Setter(value = AccessLevel.NONE)
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToMany
    private List<Club> gameClubs;

    @ManyToOne
    @JoinColumn(name = "host_club_club_id")
    private Club hostClub;
    @ManyToOne
    @JoinColumn(name = "guest_club_club_id")
    private Club guestClub;
    private int hostScore;
    private int guestScore;
    private boolean isPenaltyScore;
    private boolean inProgress;
    private GameType gameType;
    private long leagueId;

    public void setGameClubs(List<Club> gameClubs) {
        this.gameClubs = gameClubs;
    }

    public void setHostClub(Club hostClub) {
        this.hostClub = hostClub;
    }

    public void setGuestClub(Club guestClub) {
        this.guestClub = guestClub;
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

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public void setLeagueId(long leagueId) {
        this.leagueId = leagueId;
    }

    public List<Club> getGameClubs() {
        return gameClubs;
    }

    public Club getHostClub() {
        return hostClub;
    }

    public Club getGuestClub() {
        return guestClub;
    }

    public int getHostScore() {
        return hostScore;
    }

    public int getGuestScore() {
        return guestScore;
    }

    public boolean isPenaltyScore() {
        return isPenaltyScore;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public GameType getGameType() {
        return gameType;
    }

    public long getLeagueId() {
        return leagueId;
    }

    public Game() {
    }

    public Game(Club hostClub, Club guestClub, GameType gameType, long leagueId){
        this.hostClub=hostClub;
        this.guestClub=guestClub;
        this.gameType=gameType;
        this.leagueId=leagueId;
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
