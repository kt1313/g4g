package pl.com.k1313.g4g.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.appuser.AppUserRepository;
import pl.com.k1313.g4g.domain.appuser.AppUserService;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.league.LeagueRepository;

import java.util.Optional;

@WebMvcTest(AppUserController.class)
public class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserService appUserService;
    @MockBean
    private AppUserRepository repository;
    @MockBean
    private ClubRepository clubRepository;
    @MockBean
    private LeagueRepository leagueRepository;

    @Test
    public void testAppUserPage() throws Exception {

        Optional<AppUser> appUserOptional = Optional.of(new AppUser("k1313", "FC Stare Lisy", 2));
        long clubId = 2;
        String appusertimestamp = "2023-01-18-2100";

        Mockito.when(this.clubRepository.findByClubId(clubId)).thenReturn();

        mockMvc.perform(get("/appuser/club/{clubId}/{appusertimestamp}"))
                .andExpect(status().isOk())
                .andExpect(view().name("appuserandclub"))
                .andExpect(content().string(containsString("FC Stare Lisy")))
        ;
    }

}
