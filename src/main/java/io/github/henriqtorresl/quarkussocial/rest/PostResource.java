package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.domain.model.Post;
import io.github.henriqtorresl.quarkussocial.domain.model.User;
import io.github.henriqtorresl.quarkussocial.domain.repository.FollowerRepository;
import io.github.henriqtorresl.quarkussocial.domain.repository.PostRepository;
import io.github.henriqtorresl.quarkussocial.domain.repository.UserRepository;
import io.github.henriqtorresl.quarkussocial.rest.dto.CreatePostRequest;
import io.github.henriqtorresl.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final UserRepository userRepository;
    private final PostRepository repository;
    private final FollowerRepository followerRepository;

    @Inject
    public PostResource(
            UserRepository userRepository,
            PostRepository repository,
            FollowerRepository followerRepository
    ) {
        this.userRepository = userRepository;
        this.repository = repository;
        this.followerRepository = followerRepository;
    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId
    ) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        if (followerId == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the header followerId")
                    .build();
        }

        User follower = userRepository.findById(followerId);
        if (follower == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Inexistent followerId")
                    .build();
        }

        boolean follows = followerRepository.follows(follower, user);
        if (!follows) { // se o usuario(follower) não segue ele(user), então não pode ver seus posts
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("You can't see these posts")
                    .build();
        }

        // o primeiro parametro "user" é um campo que diz respeito a minha @Entity de Post
        // eu estou procurando por posts que possuam o parametro user == ao segundo parametro user...
        var query = repository.find("user",                              // vou procurar por posts a partir do parametro user
                Sort.by("dateTime", Sort.Direction.Descending),         // ordenar pela data por ordem decrescente
                user);                                                         // valor atribuido ao primeiro parametro
        var list = query.list();

        // convertendo para o tipo PostResponse:
        var postResponseList = list.stream()
                .map( p -> PostResponse.fromEntity(p))      // tambem poderia fazer dessa forma: .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response
                .ok(postResponseList)
                .build();
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);

        if (user == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        Post post = new Post();
        post.setPostText(request.getText());
        post.setUser(user);

        repository.persist(post);

        return Response
                .status(Response.Status.CREATED)
                .build();
    }

}