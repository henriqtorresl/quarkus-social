package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.domain.model.User;
import io.github.henriqtorresl.quarkussocial.domain.repository.FollowerRepository;
import io.github.henriqtorresl.quarkussocial.domain.repository.UserRepository;
import io.github.henriqtorresl.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    Long userId;

    @BeforeEach
    @Transactional
    void setUp() {
        // Usuário padrão dos testes
        User user = new User();
        user.setName("Fulano");
        user.setAge(30);
        this.userRepository.persist(user);
        this.userId = user.getId();


        System.out.println("Executed before any one methods");
    }

    @Test
    @DisplayName("Should return 409 when followerId is equal to userId")
    public void sameUserAsFollowerTest() {
        var body = new FollowerRequest();
        body.setFollowerId(this.userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", this.userId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself!"));

    }

    @Test
    @DisplayName("Should return 404 when userId doesn't exist")
    public void userNotFoundTest() {
        var inexistentUserId = 999;

        var body = new FollowerRequest();
        body.setFollowerId(this.userId);


        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", inexistentUserId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }

}