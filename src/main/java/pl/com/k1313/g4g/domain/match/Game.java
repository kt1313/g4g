package pl.com.k1313.g4g.domain.match;

import lombok.AccessLevel;
import lombok.Setter;
import pl.com.k1313.g4g.domain.club.Club;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Setter(value = AccessLevel.NONE)
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToMany
    private List<Club> gameClubs;
    @OneToOne
    private Club hostClub;
    @OneToOne
    private Club guestClub;
    private int hostScore;
    private int guestScore;
    private boolean isPenaltyScore;
    private boolean inProgress;
    private GameType gameType;
    private GameStatus gameStatus;
    private long leagueId;
    private boolean gamePlayed;
    private int hostBallPossession;
    private int guestBallPossession;
    private int hostShotsOnGoal;
    private int guestShotsOnGoal;
    private int hostCounterAttacks;
    private  int guestCounterAttacks;
    @ElementCollection
    public List<String> gameCommentaryList;


//    @ElementCollection
//    @CollectionTable(name = "game_gamecommentary_mapping",
//            joinColumns = {@JoinColumn(name = "game_id", referencedColumnName = "id")})
//    @MapKeyColumn(name = "minute")
//    @Column(name = "commentary")
//    private HashMap<Integer, String> gameCommentary;




    public List<Club> getGameClubs() {
        return gameClubs;
    }

    public void setGameClubs(List<Club> gameClubs) {
        this.gameClubs = gameClubs;
    }

    public Club getHostClub() {
        return hostClub;
    }

    public void setHostClub(Club hostClub) {
        this.hostClub = hostClub;
    }

    public Club getGuestClub() {
        return guestClub;
    }

    public void setGuestClub(Club guestClub) {
        this.guestClub = guestClub;
    }

    public int getHostScore() {
        return hostScore;
    }

    public void setHostScore(int hostScore) {
        this.hostScore = hostScore;
    }

    public int getGuestScore() {
        return guestScore;
    }

    public void setGuestScore(int guestScore) {
        this.guestScore = guestScore;
    }

    public boolean isPenaltyScore() {
        return isPenaltyScore;
    }

    public void setPenaltyScore(boolean penaltyScore) {
        isPenaltyScore = penaltyScore;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(long leagueId) {
        this.leagueId = leagueId;
    }

    public boolean isGamePlayed() {
        return gamePlayed;
    }

    public void setGamePlayed(boolean gamePlayed) {
        this.gamePlayed = gamePlayed;
    }

    public long getId() {
        return id;
    }

    public List<String> getGameCommentaryList() {
        return gameCommentaryList;
    }

    public void setGameCommentaryList(List<String> gameCommentaryList) {
        this.gameCommentaryList = gameCommentaryList;
    }

    public int getHostBallPossession() {
        return hostBallPossession;
    }

    public void setHostBallPossession(int hostBallPosession) {
        this.hostBallPossession = hostBallPosession;
    }

    public int getGuestBallPossession() {
        return guestBallPossession;
    }

    public void setGuestBallPossession(int guestBallPosession) {
        this.guestBallPossession = guestBallPosession;
    }

    public int getHostShotsOnGoal() {
        return hostShotsOnGoal;
    }

    public void setHostShotsOnGoal(int hostShotsOnGoal) {
        this.hostShotsOnGoal = hostShotsOnGoal;
    }

    public int getGuestShotsOnGoal() {
        return guestShotsOnGoal;
    }

    public void setGuestShotsOnGoal(int guestShotsOnGoal) {
        this.guestShotsOnGoal = guestShotsOnGoal;
    }

    public int getHostCounterAttacks() {
        return hostCounterAttacks;
    }

    public void setHostCounterAttacks(int hostCounterAttacks) {
        this.hostCounterAttacks = hostCounterAttacks;
    }

    public int getGuestCounterAttacks() {
        return guestCounterAttacks;
    }

    public void setGuestCounterAttacks(int guestCounterAttacks) {
        this.guestCounterAttacks = guestCounterAttacks;
    }

    public Game() {
    }

    public Game(Club hostClub, Club guestClub, GameType gameType, long leagueId) {
        this.hostClub = hostClub;
        this.guestClub = guestClub;
        this.gameType = gameType;
        this.leagueId = leagueId;
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
