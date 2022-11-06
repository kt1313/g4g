package pl.com.k1313.g4g.domain.player;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Setter(value = AccessLevel.NONE)
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;
    private String lastName;
    private int age;
    private String playersTeam;
    private PlayerPosition playerPosition;

    private boolean firstSquadPlayer;

    private int attacking;
    private int ballControl;
    private int passing;
    private int tackling;
    private int goalkeeping;


    public Player() {

    }

    public Player(String firstName,
                  String lastName,
                  int age,
                  String playersTeam,
                  PlayerPosition playerPosition,
                  boolean firstSquadPlayer
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.playersTeam = playersTeam;
        this.playerPosition = playerPosition;
        this.firstSquadPlayer = firstSquadPlayer;
    }

    public Player(long id,
                  String firstName,
                  String lastName,
                  int age,
                  String playersTeam,
                  PlayerPosition playerPosition,
                  boolean firstSquadPlayer,
                  int attacking,
                  int ballControl,
                  int passing,
                  int tackling,
                  int goalkeeping
    ) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.playersTeam = playersTeam;
        this.playerPosition = playerPosition;
        this.firstSquadPlayer = firstSquadPlayer;
        this.attacking = attacking;
        this.ballControl = ballControl;
        this.passing = passing;
        this.tackling = tackling;
        this.goalkeeping = goalkeeping;

    }

    //ponizej dla PlayerServiceu generowanie randomowego grajka
    public Player(String firstName,
                  String lastName,
                  int age
            , int attacking
            , int ballControl
            , int passing
            , int tackling
            , int goalkeeping
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.attacking = attacking;
        this.ballControl = ballControl;
        this.passing = passing;
        this.tackling = tackling;
        this.goalkeeping = goalkeeping;

    }

    public Player(String firstName, String lastName, int age, String playersTeam, PlayerPosition playerPosition, boolean firstSquadPlayer, int attacking, int ballControl, int passing, int tackling, int goalkeeping) {
    }


    public void setFirstSquadPlayer(boolean firstSquadPlayer) {
        this.firstSquadPlayer = firstSquadPlayer;
    }

    public void setPlayerPosition(PlayerPosition playerPosition) {
        this.playerPosition = playerPosition;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", playersTeam=" + playersTeam +
                ", position=" + playerPosition +
                ", firstSquadPlayer=" + firstSquadPlayer +
                ", attacking=" + attacking +
                ", ballControl=" + ballControl +
                ", passing=" + passing +
                ", tackling=" + tackling +
                ", goalkeeping=" + goalkeeping +
                '}';
    }

}
