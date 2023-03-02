package pl.com.k1313.g4g.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.appuser.AppUserRepository;
import pl.com.k1313.g4g.domain.appuser.AppUserService;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.league.LeagueRepository;

import java.util.ArrayList;
import java.util.List;
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
    public void testLoginOK() throws Exception {
        Optional<AppUser> appUser = Optional.of(
                new AppUser("user", "FC Team", "imejl@com.pl", "password", "2023-01-18-2100"));
        String clubname = appUser.get().getClubname();

        Mockito.when(this.repository.findByAppUserName(appUser.get().getAppUserName())).thenReturn(appUser);

        String postContent = "appusername=user&password=password";
        MockHttpServletRequestBuilder request =
                post("/appuser/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content(postContent);


        mockMvc.perform(request)
                .andExpect(view().name("appuserandclub"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("FC Team")))
                .andExpect(model().attribute("appusername",appUser.get().getAppUserName()))
                .andExpect(model().attribute("clubname",clubname));

        Mockito.verify(repository, Mockito.times(1)).findByAppUserName(appUser.get().getAppUserName());
    }

    @Test
    public void testRegistration() throws Exception {
        Optional<AppUser> appUser = Optional.of(
                new AppUser("user1", "FC Team", "email@com.pl", "password", "2023-01-18-2100"));
        String password_confirm = appUser.get().getAppUserPassword();
        long appuserid=1;
        long appUserId=1;
        String appuserclubname="FC Team";

        Mockito.when(this.repository.findByAppUserName(appUser.get().getAppUserName())).thenReturn(appUser);
        Mockito.when(this.appUserService.createAppUser
                (appUser.get().getAppUserName(), appuserclubname,appUser.get().getUserEmail(),appUser.get().getAppUserPassword())).thenReturn(appUser.get());
//Mockito.when(this.repository.findByAppUserName(appUser.get().getAppUserName()).isPresent()).thenReturn(true);
        //        Mockito.when(this.repository.findByAppUserName(appUser.get().getAppUserName()).get().getAppUserId()).thenReturn(1L);
//        doReturn(appUserId).when(this.repository).findByAppUserName(appUser.get().getAppUserName()).get().getAppUserId();
        Mockito.when(this.repository.findByAppUserId(appUserId)).thenReturn(appUser.get());

        String postContent =
                "appusername=user2&clubname=FC+Team&email=email%40com.pl&password=password&password_confirm=password";
        MockHttpServletRequestBuilder request =
                post("/appuser/registration/steptwo")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content(postContent);

        mockMvc.perform(request)
                .andExpect(view().name("registrationConfirmed"))
                .andExpect(model().attribute("appuser", appUser))
                .andExpect(model().attribute("appuserid", appuserid))
                .andExpect(model().attribute("clubname", appuserclubname))
                .andExpect(model().attribute("success", this.appUserService.confirmRegistration(appUserId)));



    }

}
