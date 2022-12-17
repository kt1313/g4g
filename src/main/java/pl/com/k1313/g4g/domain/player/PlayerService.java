package pl.com.k1313.g4g.domain.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PlayerService {

    private PlayerRepository playerRepository;
    private ClubRepository clubRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, ClubRepository clubRepository) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;
    }


    public Player autoCreatePlayer() {
        String firstName = randomFirstName();
        String lastName = randomLastName();
        //wiek zalezny bedzie czy Mlodzik czy nie. ustaw w metodzie randomAge
        int age = randomAge();
        int attacking = randomFrom100();
        int ballControl = randomFrom100();
        int passing = randomFrom100();
        int tackling = randomFrom100();
        int goalkeeping = randomFrom100() / 3;

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
        int result = r.nextInt(20) + 19;
        return result;
    }

    //tworzy goalkeepera - inny rozdzaj zawodnika
    public Player autoCreateGoalkeeper() {
        Player newPlayer = autoCreatePlayer();
        Player newGoalkeeper = new Player(
                newPlayer.getFirstName(), newPlayer.getLastName(),
                newPlayer.getAge(), newPlayer.getAttacking() / 2,
                newPlayer.getBallControl() / 2, newPlayer.getPassing() / 2,
                newPlayer.getInterception() / 2,
                //potem sprawdz bo nie moze byc wiecej niz 100
                newPlayer.getGoalkeeping() * 2
        );
        this.playerRepository.save(newGoalkeeper);
        return newGoalkeeper;
    }

    public void confirmFirst11(List<String> ids, Long clubId) {
//        tu ma zasejwowac firstSquadPlayerow na true a pozostalym wyczyscic FirstSquadPlayer
        List<Player> firstSquadPlayers = new ArrayList<>();
        Club club=this.clubRepository.findByClubId(clubId);
        List<Player> allClubPlayers=this.playerRepository.findAllByPlayerClub(club);

        if (!ids.isEmpty()) {
            for (String playerId : ids) {
                long l = Long.parseLong(playerId);
                Player first11Player = this.playerRepository.getById(l);
                first11Player.setFirstSquadPlayer(true);
                firstSquadPlayers.add(first11Player);
                this.playerRepository.save(first11Player);
            }
        }
        boolean isFound=false;
        for (Player p:allClubPlayers
             ) {
            String pString=String.valueOf(p.getId());
            isFound=ids.contains(pString);
            if (!isFound){
//                p.setPlayerPosition(PlayerPosition.NoPosition);
                p.setFirstSquadPlayer(false);
                this.playerRepository.save(p);
            }
        }
    }

    public List<Player> botPlayersCreation(long clubId) {
        List<Player> players=new ArrayList<>();

        Player newGoalkeeper=autoCreateGoalkeeper();
        newGoalkeeper.setPlayerClub(this.clubRepository.findByClubId(clubId));
        newGoalkeeper.setPlayerPosition(PlayerPosition.GK);
        newGoalkeeper.setFirstSquadPlayer(Boolean.TRUE);
        Club club=this.clubRepository.findByClubId(clubId);
        newGoalkeeper.setPlayerClub(club);
        players.add(newGoalkeeper);
        this.playerRepository.save(newGoalkeeper);

        for (int i = 1; i < 11 ; i++) {
            Player newPlayer = autoCreatePlayer();
            newPlayer.setPlayerClub(this.clubRepository.findByClubId(clubId));
            List<PlayerPosition> playersPosition=botPlayersPositions();
            newPlayer.setPlayerPosition((PlayerPosition) playersPosition.get(i-1));
            newPlayer.setFirstSquadPlayer(Boolean.TRUE);
            newPlayer.setPlayerClub(club);
            players.add(newPlayer);
            this.playerRepository.save(newPlayer);

        }


        return players;
    }

    public List<PlayerPosition> botPlayersPositions() {
        List<PlayerPosition> playersPositions = new ArrayList<>(List.of(
                PlayerPosition.RWB, PlayerPosition.RCB, PlayerPosition.LCB, PlayerPosition.LWB,
                PlayerPosition.RW, PlayerPosition.CMD, PlayerPosition.CMA, PlayerPosition.LW,
                PlayerPosition.RF, PlayerPosition.LF));
        return playersPositions;
    }
}
