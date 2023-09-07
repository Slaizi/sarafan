package ru.Bogachev.sarafan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.Bogachev.sarafan.domain.Role;
import ru.Bogachev.sarafan.domain.User;
import ru.Bogachev.sarafan.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    private final UserRepository userRepository;
    @GetMapping
    public String userList (Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "userList";
    }
    @GetMapping("{user}")
    public String userEditForm (@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }
    @PostMapping
    public String userSave(
            @RequestParam("username") String username,
            @RequestParam("userId") User user,
            @RequestParam(value = "roles", required = false) List<String> roles
    ) {
        user.setUsername(username);

        if (roles == null) {
            user.setRoles(Collections.emptySet());
        } else {
            Set<Role> userRoles = roles.stream()
                                       .map(Role::valueOf)
                                       .collect(Collectors.toSet());
            user.setRoles(userRoles);
        }

        userRepository.save(user);

        return "redirect:/user";
    }

}
