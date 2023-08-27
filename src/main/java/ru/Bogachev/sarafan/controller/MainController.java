package ru.Bogachev.sarafan.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.Bogachev.sarafan.domain.Message;
import ru.Bogachev.sarafan.repository.MessageRepository;

import org.slf4j.Logger;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final MessageRepository messageRepository;

    @GetMapping("/")
    public String greeting () {
            return "greeting";
    }

    @GetMapping("/main")
    public String main (@ModelAttribute("message") Message message, Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "main";
    }
    @PostMapping("/main")
    public String add (@ModelAttribute(name = "message") Message message, Model model) {
        messageRepository.save(message);
        return "redirect:/main";
    }
    @PostMapping("/filter")
    public String filter (
            @ModelAttribute(name = "message") Message message,
            @RequestParam(name = "filter") String filter, Model model) {
        logger.info("Filter value: {}", filter);

        if(filter.isEmpty()) {
            return "redirect:/main";
        }
        List<Message> filteredMessages = messageRepository.findByTag(filter);
        logger.info("Filtered messages: {}", filteredMessages);

        model.addAttribute("messages", messageRepository.findByTag(filter));
        return "main";
    }
}
