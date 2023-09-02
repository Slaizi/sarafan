package ru.Bogachev.sarafan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.Bogachev.sarafan.domain.Message;
import ru.Bogachev.sarafan.domain.User;
import ru.Bogachev.sarafan.repository.MessageRepository;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MessageRepository messageRepository;

    @GetMapping("/")
    public String greeting(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                       Model model) {
        var messages = filter.isEmpty() ? messageRepository.findAll() : messageRepository.findByTag(filter);
        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "text") String text,
            @RequestParam(name = "tag") String tag, Model model) {
        Message message = Message.builder()
                                 .text(text)
                                 .tag(tag)
                                 .author(user)
                                 .build();
        messageRepository.save(message);
        return "redirect:/main";
    }
}
