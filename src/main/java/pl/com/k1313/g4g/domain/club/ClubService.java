package pl.com.k1313.g4g.domain.club;

import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.league.LeagueRepository;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerPosition;
import pl.com.k1313.g4g.domain.player.PlayerRepository;
import pl.com.k1313.g4g.domain.player.PlayerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ClubService {
    public ClubService(ClubRepository clubRepository, PlayerRepository playerRepository,
                       PlayerService playerService, LeagueRepository leagueRepository
//            , AppUserService appUserService
    ) {
        this.clubRepository = clubRepository;
        this.playerRepository = playerRepository;
        this.leagueRepository = leagueRepository;
        this.playerService = playerService;
//        this.appUserService=appUserService;
    }

    //    private AppUserService appUserService;
    private PlayerService playerService;
    private LeagueRepository leagueRepository;
    private PlayerRepository playerRepository;
    private ClubRepository clubRepository;

    //
    public Club clubCreation(AppUser appUser, String clubname) {
        Club newClub = new Club(appUser, clubname);
        this.clubRepository.save(newClub);
        System.out.println(" user club" + this.clubRepository.findByClubId(newClub.getClubId()));
        return newClub;
    }

    public Club botClubCreation() {
        Club newClub = new Club();
        newClub.setClubName(botClubNameCreation());
        newClub.setAppUser(new AppUser("Teddy Bot", newClub.getClubName()));
        this.clubRepository.save(newClub);
        setBotClubPlayers(newClub.getClubId());
//        newClub.setClubFirst11(this.playerService.botPlayersCreation(newClub.getClubId()));
//        this.clubRepository.save(newClub);
        return newClub;
    }

    private void setBotClubPlayers(long clubId) {
        this.playerService.botPlayersCreation(clubId);
        this.clubRepository.save(this.clubRepository.findByClubId(clubId));
    }

    public String botClubNameCreation() {
        Random r = new Random();
        List clubNames = new ArrayList<>(List.of
                ("FC BigDaddy", "A-Team", "Strongmen FC", "L-losers", "Handycaps Club", "WeWinOnWednesday United",
                        "NeverSeeYourGoal CF", "FC True Team", "WeWillChewYourMeat Utd", "FC GiveUp"));
        String clubName = (String) clubNames.get(r.nextInt(10));
        return clubName;
    }

    public List<Integer> getClubFirst11Values(Club club) {

        List<Player> first11Players = this.playerRepository.
                findAllByPlayerClubAndFirstSquadPlayer(club, Boolean.TRUE);
        List<Integer> formationsValues = getFirst11FormationsValues(first11Players);
        int def = formationsValues.get(0);
        int mid = formationsValues.get(1);
        int att = formationsValues.get(2);
        Integer goalkeeperSkill = getGoalkeeperSkills(club);
        return new ArrayList<>(List.of(goalkeeperSkill, def, mid, att));
    }

    public List<Integer> getFirst11FormationsValues(List<Player> first11Players) {

        int first11Attack = 0;
        int first11Defence = 0;
        int first11Midfield = 0;


        //dla każdego sprawdza czy jest w ataku, pomocy czy obronie lub bramkarz
        // i w zależności od tego sumuje procent jego umiejętności attacking
        for (Player player : first11Players
        ) {
            if (player.getPlayerPosition().equals(PlayerPosition.LF) ||
                    (player.getPlayerPosition().equals(PlayerPosition.CF)) ||
                    (player.getPlayerPosition().equals(PlayerPosition.RF))) {
                first11Attack += player.getAttacking();
                first11Midfield += (player.getBallControl() * 0.35 + player.getPassing() * 0.35);
                first11Defence += player.getInterception() * 0.25;
            }
            if (player.getPlayerPosition().equals(PlayerPosition.LW) ||
                    (player.getPlayerPosition().equals(PlayerPosition.CMA)) ||
                    (player.getPlayerPosition().equals(PlayerPosition.CM)) ||
                    (player.getPlayerPosition().equals(PlayerPosition.CMD)) ||
                    (player.getPlayerPosition().equals(PlayerPosition.RW))) {
                first11Attack += (player.getAttacking() * 0.5);
                first11Midfield += (player.getBallControl() * 0.5 + player.getPassing() * 0.5);
                first11Defence += player.getInterception() * 0.5;
            }
            if (player.getPlayerPosition().equals(PlayerPosition.LWB) ||
                    (player.getPlayerPosition().equals(PlayerPosition.LCB)) ||
                    (player.getPlayerPosition().equals(PlayerPosition.CB)) ||
                    (player.getPlayerPosition().equals(PlayerPosition.RCB)) ||
                    (player.getPlayerPosition().equals(PlayerPosition.RWB))) {
                first11Attack += (player.getAttacking() * 0.25);
                first11Midfield += (player.getBallControl() * 0.25 + player.getPassing() * 0.25);
                first11Defence += player.getInterception() * 0.75;
            }
            if (player.getPlayerPosition().equals(PlayerPosition.GK)) {
                first11Attack += (player.getAttacking() * 0.1);
                first11Midfield += (player.getBallControl() * 0.1 + player.getPassing() * 0.1);
                first11Defence += player.getInterception() * 0.5;
            }
        }
        List<Integer> formationsValues = new ArrayList<Integer>(List.of(first11Defence, first11Midfield, first11Attack
        ));
        System.out.println("TeamServ, calculateFirst11FormVal, Wartość formacji: "
                + " Attack: " + formationsValues.get(2)
                + " Mid: " + formationsValues.get(1)
                + " Def: " + formationsValues.get(0)
        )
        ;

        return formationsValues;
    }


    public List<Player> findFirst11Players(Club club) {
        List<Player> first11Players = this.playerRepository.findAllByPlayerClub(club).stream()
                .filter(Player::isFirstSquadPlayer)
                .collect(Collectors.toList());
        return first11Players;
    }

    private Integer getGoalkeeperSkills(Club club) {
        return this.playerRepository
                .findFirstByPlayerClubAndFirstSquadPlayerTrueAndPlayerPosition
                        (club, PlayerPosition.GK).getGoalkeeping();//tutaj sprawdz czy potrzebny 3ci argument?
    }

}
