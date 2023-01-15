package pl.com.k1313.g4g.domain.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        return r.nextInt(100);
    }

    public String randomFirstName() {
        Random r = new Random();
        List<String> firstNamesList = new ArrayList<>(List.of(
                "Adam", "Obi-Wan", "Frankie", "Janusz", "Joshua", "One-One", "Kuling"));
        return firstNamesList.get(r.nextInt(firstNamesList.size()));
    }

    public String randomLastName() {
        Random r = new Random();
        List<String> lastNamesList = new ArrayList<>(List.of(
                "Kaleka", "McWolny", "Anemikus", "Ci-Ho-Pek", "Omojboszszsz", "Nieten", "Aninietamten"));
        return lastNamesList.get(r.nextInt(lastNamesList.size()));
    }

    public int randomAge() {
        Random r = new Random();
        return r.nextInt(20) + 19;
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
        Club club = this.clubRepository.findByClubId(clubId);
        List<Player> allClubPlayers = this.playerRepository.findAllByPlayerClub(club);

        if (!ids.isEmpty()) {
            for (String playerId : ids) {
                long l = Long.parseLong(playerId);
                Player first11Player = this.playerRepository.getById(l);
                first11Player.setFirstSquadPlayer(true);
                firstSquadPlayers.add(first11Player);
                this.playerRepository.save(first11Player);
            }
        }
        boolean isFound = false;
        for (Player p : allClubPlayers
        ) {
            String pString = String.valueOf(p.getId());
            isFound = ids.contains(pString);
            if (!isFound) {
                p.setPlayerPosition(PlayerPosition.NoPosition);
                p.setFirstSquadPlayer(false);
                this.playerRepository.save(p);
            }
        }
    }

    public List<Player> botPlayersCreation(long clubId) {
        List<Player> players = new ArrayList<>();

        Player newGoalkeeper = autoCreateGoalkeeper();
        newGoalkeeper.setPlayerClub(this.clubRepository.findByClubId(clubId));
        newGoalkeeper.setPlayerPosition(PlayerPosition.GK);
        newGoalkeeper.setFirstSquadPlayer(Boolean.TRUE);
        Club club = this.clubRepository.findByClubId(clubId);
        newGoalkeeper.setPlayerClub(club);
        players.add(newGoalkeeper);
        this.playerRepository.save(newGoalkeeper);

        for (int i = 1; i < 11; i++) {
            Player newPlayer = autoCreatePlayer();
            newPlayer.setPlayerClub(this.clubRepository.findByClubId(clubId));
            List<PlayerPosition> playersPosition = botPlayersPositions();
            newPlayer.setPlayerPosition(playersPosition.get(i - 1));
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

    public List<Player> sortPlayersBy(String sortPlayersBy, List<Player> sortedPlayers) {
        switch (sortPlayersBy) {
            case "goalkeeping":
                Comparator<Player> compGoalkeepingAsc = Comparator.comparing(Player::getGoalkeeping);
                sortedPlayers.sort(compGoalkeepingAsc.reversed());
                break;
            case "interception":
                Comparator<Player> compDefendingAsc = Comparator.comparing(Player::getInterception);
                sortedPlayers.sort(compDefendingAsc.reversed());
                break;
            case "ballcontrol":
                Comparator<Player> compMidfieldAsc = Comparator.comparing(Player::getBallControl);
                sortedPlayers.sort(compMidfieldAsc.reversed());
                break;
            case "passing":
                Comparator<Player> compPassingAsc = Comparator.comparing(Player::getPassing);
                sortedPlayers.sort(compPassingAsc.reversed());
                break;
            case "attacking":
                Comparator<Player> compAttackingAsc = Comparator.comparing(Player::getAttacking);
                sortedPlayers.sort(compAttackingAsc.reversed());
                break;
        }
        return sortedPlayers;
    }

    //pobiera clubId, liste firstSquad z numerami Id graczy i liste z pozycjami i przyporzadkowuje
//    wyrzucajac nadmiarowe pozycje z poprzednich przegladow
    public void assignPlayerPosition(long clubId, List<String> ids,
                                     List<String> stringPlayerPos, String sortPlayersByPos) {
        //czysci wszystkim graczom z klubu firstsquad i position
        Club club = this.clubRepository.findByClubId(clubId);
        List<Long> idsLongList = new ArrayList<>();
        List<String> stringPlayerPositionList = new ArrayList<>();
        List<Player> allClubPlayers = this.playerRepository.findAllByPlayerClub(club);
        if (sortPlayersByPos != null) {
            sortPlayersBy(sortPlayersByPos, allClubPlayers);
        }
        for (Player player : allClubPlayers
        ) {
            player.setFirstSquadPlayer(false);
            player.setPlayerPosition(PlayerPosition.NoPosition);
        }
// zamiana ids string na long
        for (String i : ids
        ) {
            idsLongList.add(Long.parseLong(i));
        }

        List<Player> playerByIdsList = new ArrayList<>();
        List<Integer> indexPosOfPlayers = new ArrayList<>();
        for (long i : idsLongList
        ) {
            Player player = this.playerRepository.findById(i);
            playerByIdsList.add(player);
            indexPosOfPlayers.add(allClubPlayers.indexOf(player));
            stringPlayerPositionList.add(stringPlayerPos.get(allClubPlayers.indexOf(player)));
        }

        //teraz wybiera ze wszystkich pozycji telko te wg indexuPosOfPlayer i
        // tworzy nowa Liste poprawnych Positions
//        tu problem
//        for (long i : idsLongList
//        ) {
//            Player player=this.playerRepository.findById(i);
//            playerByIdsList.add(player);
//            indexPosOfPlayers.add(allClubPlayers.indexOf(player));
//        }
//        for (int i : indexPosOfPlayers
//        ) {
//            String s=stringPlayerPos.stream().filter(s1 -> s1.)
//            for (int i : indexPosOfPlayers
//            ) {
//                if (stringPlayerPos.indexOf(s) == i) {
//                    stringPlayerPositionList.add(s);
//                }
//            }
//        }
//i teraz ustawia pozycje w playerByIdsList graczom wg stringPlayerPOsitionList
// i dodaje doFirst11 i save w Repo
        for (int i = 0; i < stringPlayerPositionList.size(); i++) {
            playerByIdsList.get(i).setPlayerPosition(PlayerPosition.valueOf(stringPlayerPositionList.get(i)));
            playerByIdsList.get(i).setFirstSquadPlayer(true);
            this.playerRepository.save(playerByIdsList.get(i));
        }
    }
//automatycznie wyznacza First11 (na razie zastosowany do buttonu u AppUsera tylko)
    public List<Player> autoSetUpFirst11(long clubId) {
        Club club = this.clubRepository.findByClubId(clubId);
        List<String> sortPlayersByString = new ArrayList<>(List.of("goalkeeping", "interception", "ballcontrol", "attacking"));
        List<Player> allClubPlayers = this.playerRepository.findAllByPlayerClub(club);
        List<Player> playersWithoutPosition=new ArrayList<>();
        //czysci Position i First11
        for (Player player : allClubPlayers
        ) {
            player.setFirstSquadPlayer(false);
            player.setPlayerPosition(PlayerPosition.NoPosition);
        }
        for (int i = 0; i < sortPlayersByString.size(); i++) {
            sortPlayersBy(sortPlayersByString.get(i), allClubPlayers);
            switch (sortPlayersByString.get(i)) {
                case "goalkeeping":
                    allClubPlayers.get(0).setPlayerPosition(PlayerPosition.GK);
                    allClubPlayers.get(0).setFirstSquadPlayer(true);
                    break;
                case "interception":
                     playersWithoutPosition=
                            allClubPlayers.stream().filter(player -> player.getPlayerPosition().equals(PlayerPosition.NoPosition)).collect(Collectors.toList());
                    playersWithoutPosition.get(0).setPlayerPosition(PlayerPosition.LCB);
                    playersWithoutPosition.get(0).setFirstSquadPlayer(true);
                    playersWithoutPosition.get(1).setPlayerPosition(PlayerPosition.RCB);
                    playersWithoutPosition.get(1).setFirstSquadPlayer(true);
                    playersWithoutPosition.get(2).setPlayerPosition(PlayerPosition.CB);
                    playersWithoutPosition.get(2).setFirstSquadPlayer(true);
                    break;
                case "ballcontrol":
                     playersWithoutPosition=
                            allClubPlayers.stream().filter(player -> player.getPlayerPosition().equals(PlayerPosition.NoPosition)).collect(Collectors.toList());
                    playersWithoutPosition.get(0).setPlayerPosition(PlayerPosition.CM);
                    playersWithoutPosition.get(0).setFirstSquadPlayer(true);
                    playersWithoutPosition.get(1).setPlayerPosition(PlayerPosition.CMD);
                    playersWithoutPosition.get(1).setFirstSquadPlayer(true);
                    playersWithoutPosition.get(2).setPlayerPosition(PlayerPosition.CMA);
                    playersWithoutPosition.get(2).setFirstSquadPlayer(true);
                    playersWithoutPosition.get(3).setPlayerPosition(PlayerPosition.LW);
                    playersWithoutPosition.get(3).setFirstSquadPlayer(true);
                    playersWithoutPosition.get(4).setPlayerPosition(PlayerPosition.RW);
                    playersWithoutPosition.get(4).setFirstSquadPlayer(true);
                    break;
                case "attacking":
                    playersWithoutPosition=
                            allClubPlayers.stream().filter(player -> player.getPlayerPosition().equals(PlayerPosition.NoPosition)).collect(Collectors.toList());
                    playersWithoutPosition.get(0).setPlayerPosition(PlayerPosition.RF);
                    playersWithoutPosition.get(0).setFirstSquadPlayer(true);
                    playersWithoutPosition.get(1).setPlayerPosition(PlayerPosition.LF);
                    playersWithoutPosition.get(1).setFirstSquadPlayer(true);
                    break;
            }
        }
        for (Player p:this.playerRepository.findAllByPlayerClub(club)
                .stream().filter(player -> player.isFirstSquadPlayer()).collect(Collectors.toList())
             ) {
            this.playerRepository.save(p);
        }
        return this.playerRepository.findAllByPlayerClub(club)
                .stream().filter(player -> player.isFirstSquadPlayer()).collect(Collectors.toList());
    }
}
