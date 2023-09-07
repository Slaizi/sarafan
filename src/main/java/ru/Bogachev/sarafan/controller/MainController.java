package ru.Bogachev.sarafan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.Bogachev.sarafan.domain.Message;
import ru.Bogachev.sarafan.domain.User;
import ru.Bogachev.sarafan.repository.MessageRepository;
import ru.Bogachev.sarafan.utilsPath.MyPath;

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

    @GetMapping("/main")
    public String main(@RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                       @AuthenticationPrincipal User user,
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
            @RequestParam(name = "text") String text,
            @RequestParam(name = "tag") String tag,
            @RequestParam(name = "file") MultipartFile file) throws IOException {
        String fileName = null;
        if (file != null && !file.isEmpty() && !file.getOriginalFilename().isEmpty()) {
            String absoluteOutputDir = myPath.myPathForFile();
            Files.createDirectories(Paths.get(absoluteOutputDir));

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(absoluteOutputDir + "/" + resultFileName));

            fileName = resultFileName;
        }

        Message message = Message.builder()
                                 .text(text)
                                 .tag(tag)
                                 .author(user)
                                 .filename(fileName)
                                 .build();
        messageRepository.save(message);
        return "redirect:/main";
    }
}
