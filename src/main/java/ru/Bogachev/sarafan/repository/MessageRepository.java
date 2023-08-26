package ru.Bogachev.sarafan.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.Bogachev.sarafan.domain.Message;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByTag(String tag);
}
