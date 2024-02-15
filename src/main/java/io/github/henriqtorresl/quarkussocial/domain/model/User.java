package io.github.henriqtorresl.quarkussocial.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "USERS")      // se eu não colocasse essa anotação, o Java entenderia que o nome da tabela é User (que é o nome da classe)
@Data   // lombok
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)         // Aqui estou dizendo que esse campo é um auto-increment
    private Long id;
    @Column(name = "name")     // essa anotação é opcional (ela só é obrigatória quando o nome da coluna é diferente do nome da propriedade)
    private String name;
    @Column(name = "age")
    private Integer age;

}