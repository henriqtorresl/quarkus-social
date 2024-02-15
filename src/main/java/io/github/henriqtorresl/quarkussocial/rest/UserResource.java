package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.domain.model.User;
import io.github.henriqtorresl.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)       // Aqui estou dizendo que vou consumir/receber objetos JSON
@Produces(MediaType.APPLICATION_JSON)       // Aqui estou dizendo que vou enviar objetos JSON
public class UserResource {

    @POST
    @Transactional          // sempre que for fazer alguma operação que não seja de leitura no banco, devo colocar essa anotação
    public Response createUser(CreateUserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        user.persist();         // como o user é um panache entity base, ele possui todos os metodos de relacionamento com o banco

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers() {
        PanacheQuery<PanacheEntityBase> query = User.findAll();

        return Response.ok(query.list()).build();
    }

    @PUT
    @Path("{id}")       // users/id
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        User user = User.findById(id);

        if (user != null) {
            user.setName(userData.getName());
            user.setAge(userData.getAge());

            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("{id}")     
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = User.findById(id);

        if (user != null) {
            user.delete();

            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

}