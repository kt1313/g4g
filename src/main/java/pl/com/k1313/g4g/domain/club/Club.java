package pl.com.k1313.g4g.domain.club;

import lombok.Data;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.league.League;
import pl.com.k1313.g4g.domain.player.Player;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long clubId;

    private String clubName;

    @OneToOne(cascade=CascadeType.PERSIST)
    private AppUser appUser;

    @ManyToOne
    private League clubLeague;

    @ElementCollection(targetClass = Player.class)
    private List<Player> clubPlayers;

    @ElementCollection(targetClass = Player.class)
    private List<Player> clubFirst11;

    public Club(String clubName, AppUser appUser, List<Player> clubPlayers) {
        this.clubName = clubName;
        this.appUser = appUser;
        this.clubPlayers = clubPlayers;
    }
    public Club(AppUser appUser, String clubName) {
        this.appUser = appUser;
        this.clubName = clubName;
    }

    public Club() {
    }

    @Override
    public String toString() {
        return "Club{" +
                "clubId=" + clubId +
                ", clubName='" + clubName + '\'' +
                ", appUser=" + appUser +
                ", clubLeague=" + clubLeague +
//                ", clubPlayers=" + clubPlayers +
//                ", clubFirst11=" + clubFirst11 +
                '}';
    }
}
