package ru.Bogachev.sarafan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.Bogachev.sarafan.domain.Message;
import ru.Bogachev.sarafan.domain.User;
import ru.Bogachev.sarafan.repository.MessageRepository;
import ru.Bogachev.sarafan.utilsPath.MyPath;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MessageRepository messageRepository;
    private final MyPath myPath;

    @GetMapping("/")
    public String greeting(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "greeting";
    }
    @GetMapping("/login")
    public String login(@ModelAttribute("user") User user,
                        @RequestParam(name = "error", required = false) String error, Model model) {
        if(error != null) model.addAttribute("errorMessage", "Authentication error: Invalid username or password");
        return "login";
    }

    @GetMapping("/main")
    public String mainPage(@RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                       @AuthenticationPrincipal User user,
                       @ModelAttribute("message") Message message,
                       Model model) {
        model.addAttribute("user", user);
        var messages = filter.isEmpty() ? messageRepository.findAll() : messageRepository.findByTag(filter);
        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @ModelAttribute("message") @Valid Message message,
            BindingResult bindingResult,
            Model model,
            @RequestParam(name = "file") MultipartFile file) throws IOException {
        message.setAuthor(user);

        if(bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            var messages = messageRepository.findAll();
            model.addAttribute("messages", messages);
            return "main";
        } else {
            if (file != null && !file.isEmpty() && !file.getOriginalFilename().isEmpty()) {
                String absoluteOutputDir = myPath.myPathForFile();
                Files.createDirectories(Paths.get(absoluteOutputDir));

                String uuidFile = UUID.randomUUID().toString();
                String resultFileName = uuidFile + "." + file.getOriginalFilename();

                file.transferTo(new File(absoluteOutputDir + "/" + resultFileName));

                message.setFilename(resultFileName);
            }
            messageRepository.save(message);
            return "redirect:/main";
        }
    }
}
