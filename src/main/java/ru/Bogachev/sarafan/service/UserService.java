package ru.Bogachev.sarafan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.Bogachev.sarafan.domain.Role;
import ru.Bogachev.sarafan.domain.User;
import ru.Bogachev.sarafan.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                             .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean addUser(User user) {
        Optional<User> userFromDb = userRepository
                .findByUsername(user.getUsername());

        if (userFromDb.isPresent()) {
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(user);

        if (!user.getEmail().isEmpty()) {
            String message = String.format(
                    "Hello, %s!\n" +
                            "Welcome to Sarafan. Please, visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(),"Activation code", message);
        }

        return true;
    }

    public boolean activateUser(String code) {
       User user = userRepository.findByActivationCode(code);

        if(user == null) {
            return false;
        }
        user.setActivationCode(null);

        userRepository.save(user);
        return true;
    }
}
