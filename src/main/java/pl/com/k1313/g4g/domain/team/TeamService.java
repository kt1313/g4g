package pl.com.k1313.g4g.domain.team;

import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerPosition;

import java.util.List;
import java.util.Objects;

@Service
public class TeamService {
    public String[][] setUpFirst11(List<Player> firstsquadplayers) {
            String[][] first11Table = new String[5][4];
            first11Table[0][0] = "0";
            first11Table[0][1] = "right Wingback";
            first11Table[0][2] = "right Winger";
            first11Table[0][3] = "3";
            first11Table[1][0] = "4";
            first11Table[1][1] = "right Centreback";
            first11Table[1][2] = "centre Midfielder Defending";
            first11Table[1][3] = "right Forward";
            first11Table[2][0] = "goalkeeper";
            first11Table[2][1] = "centreback";
            first11Table[2][2] = "centre Midfielder";
            first11Table[2][3] = "centre Forward";
            first11Table[3][0] = "12";
            first11Table[3][1] = "left Centreback";
            first11Table[3][2] = "centre Midfielder Attacking";
            first11Table[3][3] = "left Forward";
            first11Table[4][0] = "17";
            first11Table[4][1] = "left Wingback";
            first11Table[4][2] = "left Winger";
            first11Table[4][3] = "20";

            String[][] first11FinalTable = new String[5][4];
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 4; y++) {

                    //tu sparwdza czy pozycja jest wolna i jesli tak, to pobiera
                    // sprawdza czy jej pozycja jest rowna pozycji zawodnika i
                    // pobiera dane o zawodniku
                    //i do niej przypisuje
                    for (Player player : firstsquadplayers) {
                        if (first11FinalTable[x][y] == null) {
                            String playerPos = String.valueOf(player.getPlayerPosition());
                            if (Objects.equals(first11Table[x][y], playerPos)) {
                                first11FinalTable[x][y] = player.getFirstName() + " " + player.getLastName();
                            }
                        }
                    }
                }
            }
            return first11FinalTable;

    }
}
