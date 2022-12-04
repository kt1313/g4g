package pl.com.k1313.g4g.domain.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerPosition;
import pl.com.k1313.g4g.domain.player.PlayerRepository;

import java.util.*;

@Service
public class GameService {

    private ClubRepository clubRepository;
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    private ClubService clubService;

    @Autowired
    public GameService(ClubRepository clubRepository, ClubService clubService,
                       PlayerRepository playerRepository,
                       GameRepository gameRepository) {
        this.clubRepository = clubRepository;
        this.clubService = clubService;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    //    cały mecz event po evencie z komentarzami
    public HashMap<Integer, String> handleMatchEngine(List<Club> gameClubs) throws InterruptedException {
        Game newGame = new Game(gameClubs,true);
        this.gameRepository.save(newGame);
        Club hostClub=gameClubs.get(0);
        Club guestClub=gameClubs.get(1);
        //get both teams values
        List<Integer> hostClubValues = this.clubService.getClubFirst11Values(hostClub);
        List<Integer> guestClubValues = this.clubService.getClubFirst11Values(guestClub);
        Club clubAttacking = new Club();
        Club clubDefending=new Club();
        Integer gameMinute = 0;

        HashMap<Integer, String> gameCommentaryList = new HashMap<>();
        while (newGame.isInProgress()) {
            gameMinute++;
            Thread.sleep(100);
            clubAttacking = clubWithGreaterBallPossesion(hostClub, guestClub);
            if (clubAttacking.equals(hostClub)) {
                clubDefending = guestClub;
            } else {
                clubDefending = hostClub;
            }
//            komentarz o posiadaniu pilki, niech losuje tylko co...drugi event(wiekszy od 2)
            if (randomAboutCommentary() > 2) {
                gameCommentary(clubAttacking, 1, gameCommentaryList, gameMinute);
            }
            if (opportunitySucceed()) {
                //komentarz o zawiązaniu akcji
                gameCommentary(clubAttacking, 2, gameCommentaryList, gameMinute);
                //to poniżej jako jedna metoda, bo zastosowanie też do kontrataku
                opportunityEvent(clubAttacking, clubDefending, hostClub, guestClub, gameCommentaryList, gameMinute);
            } else {
//                //jesli uda się kontra to wtedy niech sprawdzi szansę na bramkę(opportunityEvent)
//                wazne!!!-------------wazne!!!
//                //wrzucimy sztucznie/tymczasowo  poziom kontrataku
                int teamCA = 30;
                if (randomCAChance(teamCA)) {
                    //komentarz o przejęciu piłki i kontrze
                    gameCommentary(clubDefending, 3, gameCommentaryList, gameMinute);
                    //TUTAJ UWAGA: celowo zamiana teamInDefence z teamOnOpportunity, bo teraz
                    //broniący sie atakują
                    opportunityEvent(clubDefending, clubAttacking, hostClub, guestClub, gameCommentaryList, gameMinute);
                }
            }
            if (gameMinute > 90) {
                newGame.setInProgress(false);
            }
        }
        String matchResult = "Koniec meczu. Na tablicy widnieje wynik" + newGame.getHostScore() + " : " + newGame.getGuestScore();
        gameCommentaryList.put(gameMinute, matchResult);
        System.out.println("Koniec. Wynik meczu: " + newGame.getHostScore() + " : " + newGame.getGuestScore());
//        int hostTeamMid = hostClub.getMidfield();
//        int hostTeamAtt = hostClub.getAttack();
//        int hostTeamDef = hostClub.getDefence();
//        int hostTeamGoalkpr = hostClub.getGoalkeeperSkill();
//        int guestTeamMid = guestTeam.getMidfield();
//        int guestTeamAtt = guestTeam.getAttack();
//        int guestTeamDef = guestTeam.getDefence();
//        int guestTeamGoalkpr = guestTeam.getGoalkeeperSkill();
//        System.out.println("Stats:  ");
//        System.out.println("HostTeam: Midfield: " + hostTeamMid + " Attack: " + hostTeamAtt + " Def: " + hostTeamDef + " Goalkpr: " + hostTeamGoalkpr);
//        System.out.println("GuestTeam: Midfield: " + guestTeamMid + " Attack: " + guestTeamAtt + " Def: " + guestTeamDef + " Goalkpr: " + guestTeamGoalkpr);

        return gameCommentaryList;
    }



    //sprawdza posiadanie, na jego podstawie losuje kto ma akcję
    private Club clubWithGreaterBallPossesion(Club hostClub, Club guestClub) {
        Random random = new Random();
        int chance;
        double hostClubMidfieldDouble = this.clubService.getClubFirst11Values(hostClub).get(1);
        double guestClubMidfieldDouble = this.clubService.getClubFirst11Values(guestClub).get(1);


        double totalMidfieldDouble = hostClubMidfieldDouble + guestClubMidfieldDouble;
        int totalMidfield = (int) totalMidfieldDouble;

        double hostClubMidPartDouble = (hostClubMidfieldDouble / totalMidfield) * 100;
        int hostClubMidfield = (int) hostClubMidPartDouble;

        double guestClubMidPartDouble = (guestClubMidfieldDouble / totalMidfield) * 100;
        int guestClubMidfield = (int) guestClubMidPartDouble;

        chance = random.nextInt(100) + 1;
        if (chance >= hostClubMidfield) {
            return guestClub;
        } else {
            return hostClub;
        }
    }

    private int randomAboutCommentary() {
        Random random = new Random();
        return random.nextInt(4);
    }

    private void gameCommentary(Club club, int typeOfCommentary, HashMap<Integer,
            String> gameCommmentaryList, int gameMinute) {
        switch (typeOfCommentary) {
            case 1:
                String commentaryBallPossesion1 = gameMinute
                        + "min. Uwijają się jak mrówki i wygrali walkę o piłkę w środku pola piłkarze "
                        + club.getClubName() + "\r\n";
                System.out.println(commentaryBallPossesion1);
                gameCommmentaryList.put(gameMinute, commentaryBallPossesion1);
                break;
            case 2:
                String commentaryCreationChance1 = gameMinute
                        + "min. Ruszył teraz na przeciwnika z balem przy nodze grajek zespołu "
                        + club.getClubName() + "\r\n";
                System.out.println(commentaryCreationChance1);
                gameCommmentaryList.put(gameMinute, commentaryCreationChance1);
                break;
            case 3:
                String commentaryCA1 = gameMinute
                        + "min. Oni są jak stal, nieugięci w obronie. Odbiór i mkną z kontrą jak torpeda zawodnicy "
                        + club.getClubName() + "\r\n";
                System.out.println(commentaryCA1);
                gameCommmentaryList.put(gameMinute, commentaryCA1);
                break;
            case 4:
                String commentaryGoal1 = gameMinute
                        + "min. Gooooooooooooooooooool!!!! Stadiony świata!!! Bramka dla "
                        + club.getClubName() + "\r\n";
                System.out.println(commentaryGoal1);
                gameCommmentaryList.put(gameMinute, commentaryGoal1);
                break;
            default:
                System.out.println("Piękna dziś pogoda, nieprawdaż?");
        }
    }

    private void opportunityEvent(Club clubAttacking
            , Club clubDefending, Club hostClub, Club guestClub, HashMap<Integer,
            String> gameCommentaryList, int gameMinute) {
        List<Club> clubsList=new ArrayList<>(List.of(hostClub, guestClub));
        if (attackSucceedOverDefence(clubAttacking, hostClub, guestClub)) {
//                System.out.println("MatchServ, opportunityEvent, aatackSucceedOverDef ");
            int forwarderAttack = getForwarderAttack(clubAttacking, hostClub, guestClub);
            int goalkeeperSkill=this.clubService.getClubFirst11Values(guestClub).get(3);
            if (forwardScoresVsGoalkeeper(goalkeeperSkill, forwarderAttack)) {
                goalEvent(clubsList,clubAttacking);
                gameCommentary(clubAttacking, 4, gameCommentaryList, gameMinute);
                //i tu odsieżyc wynik na stronie/ po kazdym evencie
                //i dodac methodMatchCommentary()
            }
        }
    }

    //sparawdza czy akcja się udała porównując atak do defensywy
    private boolean attackSucceedOverDefence(Club clubAttacking, Club hostClub, Club guestClub) {
        Random random = new Random();
        boolean attackSucceed = false;
        if (clubAttacking.equals(hostClub)) {
            double sumAttackDefence = this.clubService.getClubFirst11Values(hostClub).get(3) +
                    this.clubService.getClubFirst11Values(guestClub).get(1);
            double attackPartDouble = ((this.clubService.getClubFirst11Values(hostClub).get(3) / sumAttackDefence) * 100);
            int attackPart = (int) attackPartDouble;
            double defPartDouble = ((this.clubService.getClubFirst11Values(guestClub).get(1) / sumAttackDefence) * 100);
            int defPart = (int) defPartDouble;
            int succeed = random.nextInt(100) + 1;
//                System.out.println("MatchServ, attackSucceedOverDef, random:"+succeed+" attackPart: "
//                        + attackPart+" defPart: "+defPart);
            if (succeed <= attackPart) {
                attackSucceed = true;
            } else {
                attackSucceed = false;
            }
        } else {
            if (clubAttacking.equals(guestClub)) {
                double sumAttackDefence = this.clubService.getClubFirst11Values(guestClub).get(3)
                        + this.clubService.getClubFirst11Values(hostClub).get(1);
                double attackPartDouble = this.clubService.getClubFirst11Values(guestClub).get(3) / sumAttackDefence * 100;
                int attackPart = (int) attackPartDouble;
                double defPartDouble = this.clubService.getClubFirst11Values(hostClub).get(1) / sumAttackDefence * 100;
                int defPart = (int) defPartDouble;
                int succeed = random.nextInt(100) + 1;
                if (succeed <= attackPart) {
                    attackSucceed = true;
                } else {
                    attackSucceed = false;
                }
            } else {
                System.out.println("Atakujacy to nie host ani guest");
            }
        }
        return attackSucceed;
    }

    private Integer getForwarderAttack(Club clubAttacking, Club hostClub, Club guestClub) {
        int strikerAttack = 0;
        if (clubAttacking.equals(hostClub)) {
            strikerAttack = this.playerRepository.findAll().stream()
                    .filter(Player::isFirstSquadPlayer)
                    .filter(player -> player.getPlayerPosition().equals(PlayerPosition.LF)
                            || player.getPlayerPosition().equals(PlayerPosition.CF)
                            || player.getPlayerPosition().equals(PlayerPosition.RF))
                    .findFirst().get().getAttacking();
        } else if (clubAttacking.equals(guestClub)) {
            //tu zakladamy sredni atak defaultowego zespołu
            strikerAttack = 60;
        } else {
            System.out.println("Atakujacy to nie host ani guest");
        }
        return strikerAttack;
    }

    private boolean forwardScoresVsGoalkeeper(Integer goalkeeperSkill, int strikerAttack) {
        Random random = new Random();
        int goalChance = goalkeeperSkill + strikerAttack;
        int goal = random.nextInt(goalChance);
        if ((goal * 0.7) > goalkeeperSkill) {
            return true;
        } else {
            return false;
        }
    }

    public void goalEvent(List<Club> clubList, Club club) {
        // wez z kontrollera
        List<Integer> matchScore = new ArrayList<>();

        Optional<Game> gameOptional=this.gameRepository.findFirstByInProgress(Boolean.TRUE);
        Game game = new Game();
        if (gameOptional.isPresent()){
            game=gameOptional.get();
        }

        Optional<Club> hostClub = game.getGameClubs().stream().findFirst();
        Optional<Club> guestClub = game.getGameClubs().stream().findFirst();
//tu jest porownywany Optional do Club.......
        if ((club.getClubId()) == (hostClub.get().getClubId())) {
            game.setHostScore(game.getHostScore() + 1);
            System.out.println("Match score: Gospodarze: " + game.getHostScore() + " Goście: " + game.getGuestScore());
        } else if (club.getClubId() == (guestClub.get().getClubId())) {
            game.setGuestScore(game.getGuestScore() + 1);
            System.out.println("Match score: Gospodarze: " + game.getHostScore() + " Goście: " + game.getGuestScore());

        } else {
            throw new IllegalArgumentException("Błędny zespół");
        }
        this.gameRepository.save(game);
    }

    private boolean opportunitySucceed() {
        Random random = new Random();
        int succeed = random.nextInt(2);
        if (succeed == 0) {
            return true;
        } else {
            return false;
        }
    }
    private boolean randomCAChance(int teamCA) {
        Random random = new Random();
        // musi byc wyliczony poziom kontrataku drużyny
        //zakładamy na potrzeby testu: 30 na 100 max
        if (random.nextInt(100) < teamCA) {
            return true;
        } else return false;
    }
}
