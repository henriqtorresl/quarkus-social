package io.github.henriqtorresl.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {

    // Após adicionar essas annotations eu consigo usar o validator pra validar esses campos...
    @NotBlank(message = "Name is Required")       // verifica tanto se a string possui o valor null ou vazio
    private String name;
    @NotNull(message = "Age is Required")
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}