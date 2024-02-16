package io.github.henriqtorresl.quarkussocial.rest.dto;

import io.github.henriqtorresl.quarkussocial.domain.model.Follower;
import lombok.Data;

@Data
public class FollowerResponse {
    private Long id;
    private String name;

    public FollowerResponse() {

    }

    public FollowerResponse(Follower follower) {
         this(follower.getId(), follower.getFollower().getName());  // esse "this" referencia o construtor de baixo...
    }

    public FollowerResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}