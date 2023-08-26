package ru.Bogachev.sarafan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.Bogachev.sarafan.domain.Message;
import ru.Bogachev.sarafan.repository.MessageRepository;

@Controller
@RequiredArgsConstructor
public class GreetingController {
    private final MessageRepository messageRepository;

    @GetMapping("/greeting")
    public String greeting (
            @RequestParam(name = "name", required = false, defaultValue = "World") String name,
            Model model
    ) {
            model.addAttribute("name", name);
            return "greeting";
    }

    @GetMapping("/")
    public String main (@ModelAttribute("message") Message message, Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "main";
    }
    @PostMapping("/")
    public String add (@ModelAttribute(name = "message") Message message, Model model) {
        messageRepository.save(message);
        return "redirect:/";
    }
    @PostMapping("/filter")
    public String filter (
            @ModelAttribute(name = "message") Message message,
            @RequestParam(name = "filter") String filter, Model model) {

        if(filter.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("messages", messageRepository.findByTag(filter));
        return "main";
    }
}
