package io.github.henriqtorresl.quarkussocial.rest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreatePostRequest {
    private String text;
}