package ru.Bogachev.sarafan.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.Bogachev.sarafan.domain.User;


@Repository
public interface UserRepository extends CrudRepository <User, Long> {
    User findByUsername(String username);
}
