package io.github.henriqtorresl.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "POSTS")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = " post_text ")
    private String postText;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    // como aqui é um relacionamento, o mapeamento é feito de outra forma:
    @ManyToOne  // muitas postagens para um usuario
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist     // essa anotação diz que o metodo abaixo será executado sempre antes de persistir um dado...
    public void prePersist() {
        setDateTime(LocalDateTime.now());
    }
}