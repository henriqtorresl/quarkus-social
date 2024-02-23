package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.domain.model.User;
import io.github.henriqtorresl.quarkussocial.domain.repository.UserRepository;
import io.github.henriqtorresl.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)     // essa antação me permite definir qual a API (o path) que estou testando, dessa forma eu não preciso injetar a url com o @TestHTTPResource
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    Long userId;

    // Garantindo que vou criar um usuário antes dos testes
    @BeforeEach     // Essa anotação me diz que esse metodo é sempre o primeiro a ser executado
    @Transactional  // como eu uso o metodo "persist" do UsuarioRepository eu preciso dessa anotação
    public void setUP() {
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);
        userRepository.persist(user);
        userId = user.getId();
    }

    // Para que esse teste funcione eu preciso criar um usuário para associar um post a ele
    @Test
    @DisplayName("Should create a post for a user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some Text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParams("userId", this.userId)
        .when()
                .post()         // aqui eu não preciso mais passar a url pois eu apontei pro PostResource na antação @TestHTTPEndpoint
        .then()
                .statusCode(201);
    }
}