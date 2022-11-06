package pl.com.k1313.g4g.domain.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerService {

    private PlayerRepository playerRepository;
    @Autowired
    public PlayerService (PlayerRepository playerRepository){this.playerRepository=playerRepository;}

    public List<Player> createFirst11(List<String> ids) {
        List<Player> firstSquadPlayers = new ArrayList<>();

        if (ids != null) {
            for (String idplayer : ids) {
                long l = Long.parseLong(idplayer);
                Player first11Player = this.playerRepository.getById(l);
                first11Player.setFirstSquadPlayer(true);
                firstSquadPlayers.add(first11Player);
                this.playerRepository.save(first11Player);
            }
        }
        return firstSquadPlayers;
    }


}
