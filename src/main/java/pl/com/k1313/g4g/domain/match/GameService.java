package pl.com.k1313.g4g.domain.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;
import pl.com.k1313.g4g.domain.league.LeagueRepository;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerPosition;
import pl.com.k1313.g4g.domain.player.PlayerRepository;

import java.util.*;

@Service
public class GameService {

    private ClubRepository clubRepository;
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    private LeagueRepository leagueRepository;

    private ClubService clubService;

    @Autowired
    public GameService(ClubRepository clubRepository, ClubService clubService,
                       PlayerRepository playerRepository,
                       GameRepository gameRepository,
                       LeagueRepository leagueRepository) {
        this.clubRepository = clubRepository;
        this.clubService = clubService;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.leagueRepository = leagueRepository;

    }

    //    cały mecz event po evencie z komentarzami
    public List< String> handleGameEngine(Game playGame) throws InterruptedException {
        playGame.setInProgress(true);
        Club hostClub = playGame.getGameClubs().get(0);
        Club guestClub = playGame.getGameClubs().get(1);

        //get both teams values
//        List<Integer> hostClubValues = this.clubService.getClubFirst11Values(hostClub);
//        List<Integer> guestClubValues = this.clubService.getClubFirst11Values(guestClub);
        Club clubAttacking = new Club();
        Club clubDefending = new Club();
        Integer gameMinute = 0;

        TreeMap<Integer, String> gameCommentaryMap = new TreeMap<>();
        while (gameMinute<91) {
            gameMinute++;
            Thread.sleep(10);
            clubAttacking = clubWithGreaterBallPossesion(hostClub, guestClub);
            if (clubAttacking.equals(hostClub)) {
                clubDefending = guestClub;
                playGame.setHostBallPossession(playGame.getHostBallPossession()+1);
            } else {
                clubDefending = hostClub;
                playGame.setGuestBallPossession(playGame.getGuestBallPossession()+1);
            }
//            komentarz o posiadaniu pilki, niech losuje tylko co...czwarty event(wiekszy od 3)
            int ran=randomPickUpCommentary();
            if (ran > 3) {
                gameCommentary(clubAttacking, playGame,1, gameCommentaryMap, gameMinute);

            if (opportunitySucceed()) {
                //komentarz o zawiązaniu akcji
                gameCommentary(clubAttacking, playGame,2, gameCommentaryMap, gameMinute);
                //to poniżej jako jedna metoda, bo zastosowanie też do kontrataku
                opportunityEvent(clubAttacking, clubDefending, playGame, gameCommentaryMap, gameMinute);
            } else {
//                //jesli uda się kontra to wtedy niech sprawdzi szansę na bramkę(opportunityEvent)
//                wazne!!!-------------wazne!!!
//                //wrzucimy sztucznie/tymczasowo  poziom kontrataku
                int teamCA = 30;
                if (randomCAChance(teamCA)) {
                    //komentarz o przejęciu piłki i kontrze
                    gameCommentary(clubDefending, playGame,3, gameCommentaryMap, gameMinute);
                    if (clubDefending.equals(playGame.getHostClub()))
                    {playGame.setHostCounterAttacks(playGame.getHostCounterAttacks()+1);
                    playGame.setHostBallPossession(playGame.getHostBallPossession()+1);}
                    else{playGame.setGuestCounterAttacks(playGame.getGuestCounterAttacks()+1);
                    playGame.setGuestBallPossession(playGame.getGuestBallPossession()+1);}
                    //TUTAJ UWAGA: celowo zamiana teamInDefence z teamOnOpportunity, bo teraz
                    //broniący sie atakują
                    opportunityEvent(clubDefending, clubAttacking, playGame, gameCommentaryMap, gameMinute);
                }
            }}
            if (gameMinute > 90) {
                playGame.setInProgress(false);
                playGame.setGamePlayed(true);
                playGame.setGameStatus(GameStatus.PLAYED);
                this.gameRepository.save(playGame);
            }
        }
        String matchResult = "Koniec meczu. Na tablicy widnieje rezultat " + playGame.getHostScore() + " : " + playGame.getGuestScore();
        gameCommentaryMap.put(gameMinute, matchResult);
        System.out.println("Koniec. Wynik meczu: " + playGame.getHostScore() + " : " + playGame.getGuestScore());
        updateClubsValuesAfterGames(hostClub, guestClub, playGame);
        //teraz z Map zrob List, dodaj minute do commentary
        List<String> commentaryList=new ArrayList<>();
        for (Integer minute: gameCommentaryMap.keySet()
             ) {
            String commentary=gameCommentaryMap.get(minute);
            commentaryList.add(commentary);
        }
        playGame.setGameCommentaryList(commentaryList);
        this.gameRepository.save(playGame);
//        tutaj przeslac gre do ligi i zapisac w repo lige z grami
        System.out.println(playGame.getGameCommentaryList());

        return commentaryList;
    }

    private void updateClubsValuesAfterGames(Club hostClub, Club guestClub, Game playGame) {

        //tutaj niech sprawdzi czy Friendly czy League Game i czy skonczona
        if (playGame.getGameType() == GameType.LG & playGame.isGamePlayed()) {
            hostClub.setClubRounds(hostClub.getClubRounds() + 1);
            guestClub.setClubRounds(guestClub.getClubRounds() + 1);
            int goalsDifference = playGame.getHostScore() - playGame.getGuestScore();
            if (goalsDifference >= 0) {
                hostClub.setClubGoalsDiff(hostClub.getClubGoalsDiff() + goalsDifference);
                guestClub.setClubGoalsDiff(guestClub.getClubGoalsDiff() - goalsDifference);
            } else {
                guestClub.setClubGoalsDiff(guestClub.getClubGoalsDiff() - goalsDifference);
                hostClub.setClubGoalsDiff(hostClub.getClubGoalsDiff() + goalsDifference);
            }
            if (playGame.getHostScore() > playGame.getGuestScore()) {
                hostClub.setClubPoints(hostClub.getClubPoints() + 3);
                hostClub.setClubWins(hostClub.getClubWins() + 1);
                guestClub.setClubLosses(guestClub.getClubLosses() + 1);
            } else if (playGame.getHostScore() == playGame.getGuestScore()) {
                hostClub.setClubPoints(hostClub.getClubPoints() + 1);
                guestClub.setClubPoints(guestClub.getClubPoints() + 1);
                hostClub.setClubDraws(hostClub.getClubDraws() + 1);
                guestClub.setClubDraws(guestClub.getClubDraws() + 1);
            } else {
                guestClub.setClubPoints(guestClub.getClubPoints() + 3);
                guestClub.setClubWins(guestClub.getClubWins() + 1);
                hostClub.setClubLosses(hostClub.getClubLosses() + 1);
            }
        }

        this.clubRepository.save(hostClub);
        this.clubRepository.save(guestClub);
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

    private int randomPickUpCommentary() {
        Random random = new Random();
        return random.nextInt(5);
    }

    private void gameCommentary(Club club, Game playGame, int typeOfCommentary, Map<Integer,
            String> gameCommmentaryList, int gameMinute) {
        Random random = new Random();
        List<String> ballPossesionCommentaryList=new ArrayList<>(List.of("min. Uwijają się jak mrówki i wygrali walkę o piłkę w środku pola piłkarze ",
                "min. Dwoją się i troją na boisku, a może to już mi sie dwoi i troi w oczach, ale nie! Przejęli piłkę ",
                "min. Ale to pograli! Czapki z głów za grę w środku pola dla ",
                "min. Czuć moc w nogach. Dzielą i rządzą na boisku zawodnicy ",
                "min. Im dłużej my przy piłce, tym krócej oni - wprowadzają w grę tę maksymę piłkarze "));
        List<String> chanceCreationCommentaryList=new ArrayList<>(List.of("min. Ruszył teraz na przeciwnika z balem przy nodze grajek zespołu ",
                "min. Minął jednego, dwóch, trzech...! O Hegemonie futbolu! Ma przed sobą tylko bramkarza piłkarz  ",
                "min. I znów okazja! Grają, jakby się nażarli surowego mięsa zawodnicy ",
                "min. Obrońcy mają nogi ciężkie jak z waty, a wiatr niesie atakujących ",
                "min. To skoro było tak dobrze, to dlaczego było tak źle? - zastanawiają sie obrońcy, bo z akcją sunie ",
                "min. Próbują w obronie często zmieniać pozycje, ale nie mogą zadowolić trenera, a tymczasem z kolejnym atakiem sunie "));
        List<String> counterAttackCommentaryList=new ArrayList<>(List.of("min. Oni są jak stal, nieugięci w obronie. Odbiór i mkną z kontrą jak torpeda zawodnicy ",
                "min. Środkowy obrońca gra dziś czołowe skrzypce: odbiór i kontra ",
                "min. Asysta pomocnika, to taka truskawka na torcie... wgnieciona, bo po odbiorze rusza akcja ",
                "min. To nie był błąd piłkarza, to był błąd murawy. Za to mają teraz szansę ",
                "min. Trener przeciwników rozkłada ręce trzymając się za głowę, bo jest strata i będzie szansa dla "));
        List<String> goalCommentaryList=new ArrayList<>(List.of("min. Gooooooooooooooooooool!!!! Stadiony świata!!! Bramka dla ",
                "min. To już ostatnie sekundy zdaje mu się ... szuka szczęścia między nogami bramkarza... Trafił!!! ",
                "min. Aj, Jezus Maria!! a jednak nie! Jeeeeeeest! Bramka dla ",
                "min. Teraz! Teraz! Teraz! Aaaaaaa, jeeeeeest, jest, jest, jest!! Gol dla ",
                "min. Gol dla "));
        switch (typeOfCommentary) {
            case 1:
                String commentaryBallPossesion = gameMinute
                        + ballPossesionCommentaryList.get(random.nextInt(ballPossesionCommentaryList.size()))
                        + club.getClubName() +
                        "\r\n";
                System.out.println(commentaryBallPossesion);
                gameCommmentaryList.put(gameMinute, commentaryBallPossesion);
                break;
            case 2:
                String commentaryCreationChance1 = gameMinute
                        + chanceCreationCommentaryList.get(random.nextInt(chanceCreationCommentaryList.size()))
                        + club.getClubName()
                        + "\r\n";
                System.out.println(commentaryCreationChance1);
                gameCommmentaryList.put(gameMinute, commentaryCreationChance1);
                break;
            case 3:
                String commentaryCA1 = gameMinute
                        + counterAttackCommentaryList.get(random.nextInt(counterAttackCommentaryList.size()))
                        + club.getClubName() +"\r\n";
                System.out.println(commentaryCA1);
                gameCommmentaryList.put(gameMinute, commentaryCA1);
                break;
            case 4:
                String commentaryGoal1 = gameMinute
                        + goalCommentaryList.get(random.nextInt(goalCommentaryList.size()))
                        + club.getClubName()+" "
                        + playGame.getHostScore()+"-"
                        +playGame.getGuestScore()+ "\r\n";
                System.out.println(commentaryGoal1);
                gameCommmentaryList.put(gameMinute, commentaryGoal1);
                break;
            default:
                System.out.println("Piękna dziś pogoda, nieprawdaż?");
        }
    }

    private void opportunityEvent(Club clubAttacking
            , Club clubDefending, Game playGame, TreeMap<Integer,
            String> gameCommentaryList, int gameMinute) {
        Club hostClub = playGame.getGameClubs().get(0);
        Club guestClub = playGame.getGameClubs().get(1);
        List<Club> clubsList = new ArrayList<>(List.of(hostClub, guestClub));
        if (attackSucceedOverDefence(clubAttacking, playGame)) {
            if (clubAttacking.equals(playGame.getHostClub())){playGame.setHostShotsOnGoal(playGame.getHostShotsOnGoal()+1);}
            else{playGame.setGuestShotsOnGoal(playGame.getGuestShotsOnGoal()+1);}
//                System.out.println("MatchServ, opportunityEvent, aatackSucceedOverDef ");
            int forwarderAttack = getForwarderAttack(clubAttacking, hostClub, guestClub);
            int goalkeeperSkill = this.clubService.getClubFirst11Values(clubDefending).get(0);
            if (forwardScoresVsGoalkeeper(goalkeeperSkill, forwarderAttack)) {
                goalEvent(playGame, clubAttacking);
                gameCommentary(clubAttacking, playGame,4, gameCommentaryList, gameMinute);
                //i tu odsieżyc wynik na stronie/ po kazdym evencie
                //i dodac methodMatchCommentary()
            }
        }
    }

    //sparawdza czy akcja się udała porównując atak do defensywy
    private boolean attackSucceedOverDefence(Club clubAttacking, Game playGame) {
        Random random = new Random();
        boolean attackSucceed = false;
        Club hostClub = playGame.getGameClubs().get(0);
        Club guestClub = playGame.getGameClubs().get(1);
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

    public void goalEvent(Game playGame, Club club) {
        // wez z kontrollera
        List<Integer> matchScore = new ArrayList<>();

//        Optional<Game> gameOptional = this.gameRepository.findFirstByInProgress(Boolean.TRUE);
//        Game game = new Game();
//        if (gameOptional.isPresent()) {
//            game = gameOptional.get();
//        }

        Club hostClub = playGame.getGameClubs().get(0);
        Club guestClub = playGame.getGameClubs().get(1);
//tu jest porownywany Optional do Club.......
        if ((club.getClubId()) == (hostClub.getClubId())) {
            playGame.setHostScore(playGame.getHostScore() + 1);
            System.out.println("Match score: Gospodarze: " + playGame.getHostScore() + " Goście: " + playGame.getGuestScore());
        } else if (club.getClubId() == (guestClub.getClubId())) {
            playGame.setGuestScore(playGame.getGuestScore() + 1);
            System.out.println("Match score: Gospodarze: " + playGame.getHostScore() + " Goście: " + playGame.getGuestScore());

        } else {
            throw new IllegalArgumentException("Błędny zespół");
        }
        this.gameRepository.save(playGame);
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

    public int findRoundToPlay(List<List<Game>> rounds, long leagueId) {
        List<Game> allByLeagueId = this.gameRepository.findAllByLeagueId(leagueId);
        List<Integer> leagueRounds = this.leagueRepository.findAllById(leagueId).getLeagueRound();
        Map<Integer, List<Game>> leagueRoundsMap = new TreeMap<>();
        leagueRoundsMap.put(leagueRounds.get(0), rounds.get(0));
        leagueRoundsMap.put(leagueRounds.get(1), rounds.get(1));
        leagueRoundsMap.put(leagueRounds.get(2), rounds.get(2));
        leagueRoundsMap.put(leagueRounds.get(3), rounds.get(3));
        leagueRoundsMap.put(leagueRounds.get(4), rounds.get(4));
        leagueRoundsMap.put(leagueRounds.get(5), rounds.get(5));
        leagueRoundsMap.put(leagueRounds.get(6), rounds.get(6));

        int roundToPlay = 0;

        for (int i = 0; i < leagueRounds.size(); i++) {
            Game game=rounds.get(i).get(0);
            GameStatus gameStatus=game.getGameStatus();
            if (!game.isGamePlayed())
//            if (gameStatus.equals(GameStatus.NOTPLAYED))
            {
                roundToPlay = i + 1;
                break;
            }
        }
//        for (int r : leagueRounds
//        ) {
//            for (Game g : allByLeagueId
//            ) {
//                if (g.getGameStatus().equals(GameStatus.NOTPLAYED)) {
//                    roundToPlay = r;
//                    gamesToPlay.add(g);
//                    if (gamesToPlay.size() > 3) {
//                        break;
//                    }
//                }
//            }
//        }
        return roundToPlay;
    }

    public String checkFirstSquadNumbers(List<String> errors,
                                         List<Player> hostFirsSquadPlayers,
                                         List<Player> guestFirsSquadPlayers) {
       String squadError="";
        if (hostFirsSquadPlayers.size() > 11) {
            squadError = "host>11";
        } else if (hostFirsSquadPlayers.size() < 8) {
            squadError = "host<8";
        } else if (guestFirsSquadPlayers.size() > 11) {
            squadError = "guest>11";
        } else if (guestFirsSquadPlayers.size() < 8) {
            squadError = "guest<8";
        }
        switch (squadError) {
            case "host>11":
                errors.add("Maximum number of players: 11");
                break;
            case "host<8":
                errors.add("Minimum number of players: 8");
                break;
            case "guest>11":
                errors.add("Maximum number of players: 11");
                break;
            case "guest<8":
                errors.add("Minimum number of players: 8");
                break;
        }
        return squadError;
    }
}

