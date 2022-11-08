package pl.com.k1313.g4g.controllers;

import com.mysql.cj.conf.StringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.com.k1313.g4g.domain.users.AppUserService;

@Controller
@RequestMapping("/appUser")
public class AppUserController {

    private AppUserService appUserService;

    @Autowired
    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/registration/stepone")
    public String beginRegistrationWizard() {
        return "redirect:/registrationStepOne";
    }

    @PostMapping("/registration/steptwo")
    public String registrationWizardStepTwo(String appusername, String email, String password) {
        this.appUserService.createTempAppUser(appusername, email, password);
        return "registrationStepTwo";
    }

}
