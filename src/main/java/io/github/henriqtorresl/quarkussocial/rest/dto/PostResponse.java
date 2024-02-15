package io.github.henriqtorresl.quarkussocial.rest.dto;

import io.github.henriqtorresl.quarkussocial.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private String text;
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post) {
        var response = new PostResponse();

        response.setText(post.getPostText());
        response.setDateTime(post.getDateTime());

        return response;
    }
}