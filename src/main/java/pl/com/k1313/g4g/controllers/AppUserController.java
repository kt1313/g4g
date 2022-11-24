package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.appuser.AppUserRepository;
import pl.com.k1313.g4g.domain.appuser.AppUserService;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.club.ClubRepository;
import pl.com.k1313.g4g.domain.club.ClubService;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/appuser")
public class AppUserController {

    private AppUserService appUserService;
    private AppUserRepository repository;
    private ClubRepository clubRepository;
    private ClubService clubService;

    @Autowired
    public AppUserController(AppUserService appUserService, AppUserRepository repository,
                             ClubRepository clubRepository, ClubService clubService) {
        this.appUserService = appUserService;
        this.repository = repository;
        this.clubRepository = clubRepository;
        this.clubService = clubService;
    }

    @PostMapping
    public String appUserpage(String appusername, String password, Model model) {

        List<String> errors = new ArrayList<>();
        Optional<AppUser> appUser = this.repository.findByAppUserName(appusername);
        if (!appUser.get().getAppUserPassword().equals(password)) {
            errors.add("Check username or passwword");
        }
        if (errors.isEmpty()) {
            String clubname = this.repository.findByAppUserName(appusername).get().getClubname();
            long clubId = this.clubRepository.findByClubName(clubname).get().getClubId();
            Optional<Club> club = this.clubRepository.findByClubName(clubname);
            long leagueId= this.clubRepository.findByClubName(clubname).get().getClubLeague().getId();
            model.addAttribute("appusername", appusername);
            model.addAttribute("clubname", clubname);
            model.addAttribute("clubId", clubId);
            model.addAttribute("leagueId", leagueId);
            return "appuser";
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
    @GetMapping("/registration/confirmed/{appUserName}")
    public String confirmRegistration(@PathVariable String appUserName, Model model) {
        Optional<AppUser> appUser = this.repository.findByAppUserName(appUserName);
        long appUserId = appUser.get().getAppUserId();
        boolean success = this.appUserService.confirmRegistration(appUserId);
        model.addAttribute("success", success);
        model.addAttribute("appUserId", this.repository.findByAppUserName(appUserName).get().getAppUserId());
        return "/registrationConfirmed";
    }

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
            return "appuser";
        } else {
            model.addAttribute("errors", errors);
            return "login";
        }
    }

    @GetMapping("/club/{clubId}")
    public String clubPage(@PathVariable long clubId, Model model) {
        Optional<Club> byId = this.clubRepository.findById(clubId);
        String clubname = byId.get().getClubName();
        String appUser = byId.get().getAppUser().getAppUserName();
        model.addAttribute("clubname", clubname);
        model.addAttribute("club", byId);
        model.addAttribute("appuser", appUser);
        return "club";
    }


}

