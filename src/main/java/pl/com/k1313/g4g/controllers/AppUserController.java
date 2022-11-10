package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.com.k1313.g4g.domain.appuser.AppUser;
import pl.com.k1313.g4g.domain.appuser.AppUserRepository;
import pl.com.k1313.g4g.domain.appuser.AppUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/appuser")
public class AppUserController {

    private AppUserService appUserService;
    private AppUserRepository repository;

    @Autowired
    public AppUserController(AppUserService appUserService, AppUserRepository repository) {
        this.appUserService = appUserService;
        this.repository = repository;
    }

    @GetMapping("/registration/stepone")
    public String beginRegistrationWizard() {
        return "registrationStepOne";
    }

    @PostMapping("/registration/steptwo")
    public String registrationWizardStepTwo(String appusername,
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
            this.appUserService.createTempAppUser(appusername, email, password);
            long appuserid = this.repository.findByAppUserName(appusername).get().getAppUserId();
            System.out.println("appuserid= "+appuserid);
            model.addAttribute("appuserid", appuserid);
            return "registrationConfirmed";
        } else {
            model.addAttribute("errors", errors);
            return "registrationStepOne";
        }
    }

    @GetMapping("/logged")
    public String loginConfirmed( String appusername, String password, Model model) {

        List<String> errors = new ArrayList<>();
        Optional<AppUser> appUser = this.repository.findByAppUserName(appusername);

        if (appUser.isEmpty() || !appUser.get().getAppUserPassword().equals(password)) {
            errors.add("Wrong password or username");
        }
        if (errors.isEmpty()) {
            boolean loginsuccess = true;
            model.addAttribute("appusername", appusername);
            model.addAttribute("loginsuccess", loginsuccess);
            return "appuser";
        } else {
            model.addAttribute("errors", errors);
            return "login";
        }
    }
}
