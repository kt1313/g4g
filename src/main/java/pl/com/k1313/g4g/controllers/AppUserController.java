package pl.com.k1313.g4g.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.com.k1313.g4g.domain.appuser.AppUserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/appuser")
public class AppUserController {

    private AppUserService appUserService;

    @Autowired
    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
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
        if (!password.equals(password_confirm)) {
            errors.add("Check Password and Password_Confirm typing");
        }

        if (errors.isEmpty()) {
            this.appUserService.createTempAppUser(appusername, email, password);
            return "registrationConfirmed";
        } else {
            model.addAttribute("errors", errors);
            return "registrationStepOne";
        }
    }

}
