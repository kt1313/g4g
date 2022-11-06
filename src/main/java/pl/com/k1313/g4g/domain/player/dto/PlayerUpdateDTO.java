package pl.com.k1313.g4g.domain.player.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import pl.com.k1313.g4g.domain.player.PlayerPosition;

import java.time.LocalDate;


    @Data
    public class PlayerUpdateDTO {

        private final Long id;
        private final String firstName;
        private final String lastName;
        private final int age;
        private final PlayerPosition playerPosition;
        private final boolean firstSquadPlayer=false;
        private final int attacking;
        private final int ballControl;
        private final int passing;
        private final int tackling;
        private final int goalkeeping;

    }
