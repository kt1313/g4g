package pl.com.k1313.g4g.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.web.servlet.MockMvc;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.appuser.AppUserRepository;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.league.League;
import pl.com.k1313.g4g.domain.league.LeagueRepository;
import pl.com.k1313.g4g.domain.player.Player;
import pl.com.k1313.g4g.domain.player.PlayerPosition;
import pl.com.k1313.g4g.domain.player.PlayerRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ClubControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeagueRepository leagueRepository;

    @MockBean
    private ClubRepository clubRepository;
    @MockBean
    private AppUserRepository appUserRepository;
    @MockBean
    private PlayerRepository playerRepository;

    @Test
    public void basic() throws Exception {
        AppUser user1 = new AppUser("User1", "Team1", 1);
        AppUser user2 = new AppUser("User2", "Team2", 2);
        Club club1 = new Club(user1, "Team1");
        Club club2 = new Club(user2, "Team2");
        List<Club> leagueteams = new ArrayList<>(List.of(club1, club2));
        League league = new League("1", leagueteams);

        long leagueId = league.getId();
        String appusertimestamp = "date&time";

        Mockito.when(this.leagueRepository.findAllById(league.getId())).thenReturn(league);

        mockMvc.perform(get("/league/{leagueId}/{appusertimestamp}"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("league"))
                .andExpect(view().name("league"))
                .andExpect(content().string(containsString("Team2")));
    }

    @Test
    public void firstSquadHandling(){
        AppUser user1 = new AppUser("User1", "Team1", 1);
        Club club1 = new Club(user1, "Team1");
        long id=1;
        String appusertimestamp = "date&time";
        Player player1=new Player( "Ttt", "Kkk", 22, club1, PlayerPosition.GK, true);
        Player player2=new Player("Ggg", "Hhh", 23, club1, PlayerPosition.LF, true);
        List<Player> playerList=new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);

        Mockito.when(this.clubRepository.findByClubId(club1.getClubId())).thenReturn(club1);
        Mockito.when(this.appUserRepository.findByClubId(club1.getClubId())).thenReturn(user1);
        Mockito.when(this.appUserRepository.findByTimeStampAppUser(appusertimestamp)).thenReturn(user1);
        Mockito.when(this.playerRepository.findAllByPlayerClub(club1)).thenReturn(playerList);
//
//
//        Mockito.when(guestService.findAll()).thenReturn(Arrays.asList(guest));
//
//        mockMvc.perform(get("/guests"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("guests"))
//                .andExpect(view().name("guests"))
//                .andExpect(content().string(containsString("1986-11-13")));
//    }
    }
}
