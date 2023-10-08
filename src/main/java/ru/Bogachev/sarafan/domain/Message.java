package ru.Bogachev.sarafan.domain;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank(message = "Please fill the message")
    @Length(max = 2048, message = "Message to long (more than 2kB)")
    String text;
    @Length(max = 255, message = "Message to long (more than 255)")
    String tag;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    User author;
    String filename;

    public String getAuthorName () {
        return author != null ? author.getUsername() : "<none>";
    }

}
