package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.domain.model.Follower;
import io.github.henriqtorresl.quarkussocial.domain.repository.FollowerRepository;
import io.github.henriqtorresl.quarkussocial.domain.repository.UserRepository;
import io.github.henriqtorresl.quarkussocial.rest.dto.FollowerPerUserResponse;
import io.github.henriqtorresl.quarkussocial.rest.dto.FollowerRequest;
import io.github.henriqtorresl.quarkussocial.rest.dto.FollowerResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private final FollowerRepository repository;
    private final UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response teste(@PathParam("userId") Long userId, FollowerRequest request) {

        if (userId.equals(request.getFollowerId())) {   // o usuario não pode se seguir
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself!").build();
        }

        var user = userRepository.findById(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var follower = userRepository.findById(request.getFollowerId());
        boolean follows = repository.follows(follower, user);   // verifica se ja esta seguindo...

        if (!follows) {     // se ele não segue eu sigo e se ele segue eu não faço nada e retorno OK do mesmo jeito
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            repository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {
        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = repository.findByUser(userId);

        FollowerPerUserResponse responseObject = new FollowerPerUserResponse();
        responseObject.setFollowersCount(list.size());

        var followersList = list.stream().map(FollowerResponse::new).collect(Collectors.toList());

        responseObject.setContent(followersList);

        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userId") Long userId,
            @QueryParam("followerId") Long followerId
    ) {
        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        repository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}