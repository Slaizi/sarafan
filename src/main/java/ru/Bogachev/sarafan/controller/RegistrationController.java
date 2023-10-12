package ru.Bogachev.sarafan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.Bogachev.sarafan.domain.User;
import ru.Bogachev.sarafan.domain.dto.CaptchaResponseDto;
import ru.Bogachev.sarafan.service.UserService;

import javax.validation.Valid;
import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private static final String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
    private final UserService userService;
    @Value("${recaptcha.secret}")
    private String secret;
    private final RestTemplate restTemplate;

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user) {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam("passwordConfirm") String passwordConfirm,
            @RequestParam("g-recaptcha-response") String captchaResponse,
            @ModelAttribute("user") @Valid User user,
            BindingResult bindingResult, Model model) {

        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        if(!response.isSuccess()) {
            model.addAttribute("captchaError", "Fill captcha");
        }

        boolean isConfirmEmpty = passwordConfirm.isEmpty();
        if(isConfirmEmpty) {
            model.addAttribute("passwordConfirmError", "Password confirmation cannot be empty");
        }

        if(user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "Password ar e different!");
        }

        if(isConfirmEmpty || bindingResult.hasErrors() || !response.isSuccess()) {
            return "registration";
        }

        if (!userService.addUser(user)) {
            model.addAttribute("usernameError", "User exist!");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(@ModelAttribute("user") User user,
                           Model model, @PathVariable(name = "code") String code) {
        boolean isActive = userService.activateUser(code);

        if (isActive) {
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("messageError", "Activation code is not found!");
        }

        return "login";
    }
}
