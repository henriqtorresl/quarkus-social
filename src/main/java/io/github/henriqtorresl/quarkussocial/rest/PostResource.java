package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.domain.model.Post;
import io.github.henriqtorresl.quarkussocial.domain.model.User;
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

@Path("/users/{userID}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final UserRepository userRepository;
    private final PostRepository repository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository repository) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @GET
    public Response listPosts(@PathParam("userID") Long userId) {
        User user = userRepository.findById(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
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

        return Response.ok(postResponseList).build();
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userID") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setPostText(request.getText());
        post.setUser(user);

        repository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

}