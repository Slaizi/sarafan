package ru.Bogachev.sarafan.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.Bogachev.sarafan.domain.User;

import java.util.Optional;


@Repository
public interface UserRepository extends CrudRepository <User, Long> {
    Optional <User> findByUsername(String username);

    User findByActivationCode(String code);
}
