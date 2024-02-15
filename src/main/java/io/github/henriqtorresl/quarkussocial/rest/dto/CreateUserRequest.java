package io.github.henriqtorresl.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data       // essa annotation é um conglomerado de varias annotations do lombok (@Getter, @Setter ...)
public class CreateUserRequest {

    // Após adicionar essas annotations eu consigo usar o validator pra validar esses campos...
    @NotBlank(message = "Name is Required")       // verifica tanto se a string possui o valor null ou vazio
    private String name;
    @NotNull(message = "Age is Required")
    private Integer age;
}