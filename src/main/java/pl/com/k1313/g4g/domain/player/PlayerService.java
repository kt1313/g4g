package pl.com.k1313.g4g.domain.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PlayerService {

    private PlayerRepository playerRepository;
    @Autowired
    public PlayerService (PlayerRepository playerRepository){this.playerRepository=playerRepository;}


    public Player autoCreatePlayer() {
        String firstName = randomFirstName();
        String lastName = randomLastName();
        //wiek zalezny bedzie czy Mlodzik czy nie. ustaw w metodzie randomBirthDate
        int age = randomAge();
        int attacking = randomFrom100();
        int ballControl = randomFrom100();
        int passing = randomFrom100();
        int tackling = randomFrom100();
        int goalkeeping = randomFrom100()/3;

        Player newPlayer = new Player(
                firstName,
                lastName,
                age,
                attacking,
                ballControl,
                passing,
                tackling,
                goalkeeping);

        this.playerRepository.save(newPlayer);
        return newPlayer;
    }

    public int randomFrom100() {
        Random r = new Random();
        int result = r.nextInt(100);
        return result;
    }

    public String randomFirstName() {
        Random r = new Random();
        List firstNamesList = new ArrayList<>(List.of(
                "Adam", "Obi-Wan", "Frankie", "Janusz", "Joshua", "One-One", "Kuling"));
        String newFirstName = (String) firstNamesList.get(r.nextInt(7));
        return newFirstName;
    }

    public String randomLastName() {
        Random r = new Random();
        List firstNamesList = new ArrayList<>(List.of(
                "Kaleka", "McWolny", "Anemikus", "Ci-Ho-Pek", "Omojboszszsz", "Nieten", "Aninietamten"));
        String newFirstName = (String) firstNamesList.get(r.nextInt(7));
        return newFirstName;
    }

    public int randomAge() {
        Random r = new Random();
        int result = r.nextInt(30);
        return result;
    }
    //tworzy goalkeepera - inny rozdzaj zawodnika
    public Player autoCreateGoalkeeper() {
        Player newPlayer=autoCreatePlayer();
        int newGoalkeeping=newPlayer.getGoalkeeping()*2;
        Player newGoalkeeper=new Player(
                newPlayer.getFirstName(), newPlayer.getLastName(),
                newPlayer.getAge(),newPlayer.getAttacking()/2,
                newPlayer.getBallControl()/2, newPlayer.getPassing()/2,
                newPlayer.getTackling()/2,
                //potem sprawdz bo nie moze byc wiecej niz 100
                newPlayer.getGoalkeeping()*2
        );
        this.playerRepository.save(newGoalkeeper);
        return newGoalkeeper;
    }
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
