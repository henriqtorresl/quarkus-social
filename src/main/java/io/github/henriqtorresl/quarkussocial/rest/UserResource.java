package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.domain.model.User;
import io.github.henriqtorresl.quarkussocial.domain.repository.UserRepository;
import io.github.henriqtorresl.quarkussocial.rest.dto.CreateUserRequest;
import io.github.henriqtorresl.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)       // Aqui estou dizendo que vou consumir/receber objetos JSON
@Produces(MediaType.APPLICATION_JSON)       // Aqui estou dizendo que vou enviar objetos JSON
public class UserResource {

    private final UserRepository repository;
    private final Validator validator;

    // injeção de dependências:
    @Inject                     // essa annotation pode ser usada tanto no construtor quanto nas propriedades
    public UserResource(UserRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional          // sempre que for fazer alguma operação que não seja de leitura no banco, devo colocar essa anotação
    public Response createUser(CreateUserRequest userRequest) {
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

        // se existe alguma violação:
        if (!violations.isEmpty()) {
            ResponseError responseError = ResponseError.createFromValidation(violations);

            return Response.status(400).entity(responseError).build();
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        //user.persist();   -->  como o user é um panache entity base, ele possui todos os metodos de relacionamento com o banco

        repository.persist(user);       // Agora eu uso o panache repository então uso o método do repository

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers() {
        PanacheQuery<User> query = repository.findAll();

        return Response.ok(query.list()).build();
    }

    @PUT
    @Path("{id}")       // users/id
    @Transactional        // Essa annotation faz com que tudo seja executado no banco como uma TRANSACTION, ou seja, após tudo dar certo ele realiza um COMMIT de todas as alterações para o banco
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        User user = repository.findById(id);

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
        User user = repository.findById(id);

        if (user != null) {
            repository.delete(user);

            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

}