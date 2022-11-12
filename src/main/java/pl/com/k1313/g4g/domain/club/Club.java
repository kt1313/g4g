package pl.com.k1313.g4g.domain.club;

import lombok.Data;
import pl.com.k1313.g4g.domain.appuser.AppUser;
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
    @OneToOne
    private AppUser appUser;

    @ElementCollection(targetClass = Player.class)
    private List<Player> clubPlayers;



    public Club(String clubName, AppUser appUser, List<Player> clubPlayers) {
        this.clubName = clubName;
        this.appUser = appUser;
        this.clubPlayers = clubPlayers;
    }

    public Club(AppUser appUser, String clubname) {
        this.appUser = appUser;
        this.clubName = clubname;
    }

    public Club() {

    }

//    public long getClubId(){
//        return this.clubId;
//    }
}
