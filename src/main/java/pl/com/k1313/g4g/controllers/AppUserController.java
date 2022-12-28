package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.appuser.AppUserRepository;
import pl.com.k1313.g4g.domain.appuser.AppUserService;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.league.League;
import pl.com.k1313.g4g.domain.league.LeagueRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/appuser")
public class AppUserController {

    private AppUserService appUserService;
    private AppUserRepository repository;
    private ClubRepository clubRepository;
    private LeagueRepository leagueRepository;
//    private ClubService clubService;

    @Autowired
    public AppUserController(AppUserService appUserService, AppUserRepository repository,
                             ClubRepository clubRepository, LeagueRepository leagueRepository
//            , ClubService clubService
    ) {
        this.appUserService = appUserService;
        this.repository = repository;
        this.clubRepository = clubRepository;
        this.leagueRepository=leagueRepository;
//        this.clubService = clubService;
    }

    @GetMapping("/club/{clubId}/{appusertimestamp}")
    public String appUserAndClubPage(@PathVariable long clubId, @PathVariable String appusertimestamp, Model model) {
        Club byClubId = this.clubRepository.findByClubId(clubId);
        String clubName = byClubId.getClubName();
        String clubOwnerName = byClubId.getAppUser().getAppUserName();
        String appUserNameByTimeStamp = this.repository.findByTimeStampAppUser(appusertimestamp).getAppUserName();
        AppUser appUser = this.repository.findByTimeStampAppUser(appusertimestamp);
        String appUserName= byClubId.getAppUser().getAppUserName();
long leagueId=byClubId.getClubLeague().getId();

        model.addAttribute("appusertimestamp", appusertimestamp);
        model.addAttribute("appuser", appUser);
        model.addAttribute("appusername", appUserName);
        model.addAttribute("clubOwnerName", clubOwnerName);
        model.addAttribute("appusernamebytimestamp", appUserNameByTimeStamp);
        model.addAttribute("club", byClubId);
        model.addAttribute("clubname", clubName);
        model.addAttribute("clubId", clubId);
        model.addAttribute("leagueid", leagueId);
//        model.addAttribute("leaguerounds", leaguerounds);

        return "appuserandclub";
    }

    @PostMapping("/logged")
    public String appUserPage(String appusername, String password, Model model) {

        List<String> errors = new ArrayList<>();
        Optional<AppUser> appUser = this.repository.findByAppUserName(appusername);
        String appUserTimeStamp = appUser.get().getTimeStampAppUser();
        if (!appUser.get().getAppUserPassword().equals(password)) {
            errors.add("Check username or passwword");
        }
        if (errors.isEmpty()) {
            String clubname = this.repository.findByAppUserName(appusername).get().getClubname();
            long clubId = this.clubRepository.findByClubName(clubname).get().getClubId();
            Optional<Club> club = this.clubRepository.findByClubName(clubname);
            long leagueId = this.clubRepository.findByClubName(clubname).get().getClubLeague().getId();
            League league=this.leagueRepository.findAllById(leagueId);

            model.addAttribute("league", league);
            model.addAttribute("appusertimestamp", appUserTimeStamp);
            model.addAttribute("appusername", appusername);
            model.addAttribute("clubname", clubname);
            model.addAttribute("clubId", clubId);
            model.addAttribute("leagueId", leagueId);
            return "appuserandclub";
        } else return "login";
    }

    @GetMapping("/registration/stepone")
    public String beginRegistrationWizard() {
        return "registrationStepOne";
    }

    @PostMapping("/registration/steptwo")
    public String registrationWizardStepTwo(String appusername,
                                            String clubname,
                                            String email,
                                            String password,
                                            String password_confirm,
                                            Model model) {
        List<String> errors = new ArrayList<>();
        boolean usernameexists = this.repository.findByAppUserName(appusername).isPresent();
        if (usernameexists) {
            errors.add("Username already exists");
        }

        if (!password.equals(password_confirm)) {
            errors.add("Check Password and Password_Confirm typing");
        }

        if (errors.isEmpty()) {
            this.appUserService.createAppUser(appusername, clubname, email, password);
            Optional<AppUser> appUser = this.repository.findByAppUserName(appusername);
            long appuserid = this.repository.findByAppUserName(appusername).get().getAppUserId();
            String appuserclubname = this.repository.findByAppUserName(appusername).get().getClubname();
            boolean success = this.appUserService.confirmRegistration(appuserid);

            model.addAttribute("appuser", appUser);
            model.addAttribute("appuserid", appuserid);
            model.addAttribute("clubname", appuserclubname);
            model.addAttribute("success", success);
            return "registrationConfirmed";
        } else {
            model.addAttribute("errors", errors);
            return "registrationStepOne";
        }
    }

    //do wykorzystania kiedy user jest zalogowany(??)
//    @GetMapping("/registration/confirmed/{appUserName}")
//    public String confirmRegistration(@PathVariable String appUserName, Model model) {
//        Optional<AppUser> appUser = this.repository.findByAppUserName(appUserName);
//        long appUserId = appUser.get().getAppUserId();
//        boolean success = this.appUserService.confirmRegistration(appUserId);
//        model.addAttribute("success", success);
//        model.addAttribute("appUserId", this.repository.findByAppUserName(appUserName).get().getAppUserId());
//        return "/registrationConfirmed";
//    }

    @PostMapping("/login")
    public String loginConfirmed(String appusername, String password, Model model) {

        List<String> errors = new ArrayList<>();
        Optional<AppUser> appUser = this.repository.findByAppUserName(appusername);
        String clubname = appUser.get().getClubname();
        if (appUser.isEmpty() || !appUser.get().getAppUserPassword().equals(password)) {
            errors.add("Wrong password or username");
        }
        if (errors.isEmpty()) {
            boolean loginsuccess = true;
            model.addAttribute("appusername", appusername);
            model.addAttribute("clubname", clubname);
            model.addAttribute("loginsuccess", loginsuccess);
            return "appuserandclub";
        } else {
            model.addAttribute("errors", errors);
            return "login";
        }
    }


}

