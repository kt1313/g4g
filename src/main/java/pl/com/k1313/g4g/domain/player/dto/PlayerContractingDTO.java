package pl.com.k1313.g4g.domain.player.dto;

import lombok.Data;
import pl.com.k1313.g4g.domain.player.PlayerPosition;
@Data
public class PlayerContractingDTO {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final int age;
    private final String playersTeam;
    private final PlayerPosition playerPosition;
    private final boolean firstSquadPlayer=false;
    private final int attacking;
    private final int ballControl;
    private final int passing;
    private final int tackling;
    private final int goalkeeping;
}
