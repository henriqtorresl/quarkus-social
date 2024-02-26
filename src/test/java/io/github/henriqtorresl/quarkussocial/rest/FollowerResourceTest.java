package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.domain.model.Follower;
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
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        // Usuário padrão dos testes
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);
        this.userRepository.persist(user);
        this.userId = user.getId();

        // Usuário seguidor
        var follower = new User();
        follower.setName("Ciclano");
        follower.setAge(25);
        this.userRepository.persist(follower);
        this.followerId = follower.getId();

        // Cria um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);

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
    @DisplayName("Should return 404 on follow a user when userId doesn't exist")
    public void userNotFoundWhenTryingToFollowTest() {
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

    @Test
    @DisplayName("Should follow a user")
    public void followUserTest() {
        var body = new FollowerRequest();
        body.setFollowerId(this.followerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", this.userId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on list user followers and userId doesn't exist")
    public void userNotFoundListingFollowersTest() {
        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list a user's followers")
    public void listFollowersTest() {
        var response = given()
                            .contentType(ContentType.JSON)
                            .pathParam("userId", this.userId)
                    .when()
                            .get()
                    .then()
                            .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());
    }

    @Test
    @DisplayName("Should return 404 on unfollow user and userId doesn't exist")
    public void userNotFoundWhenUnfollowingAUserTest() {
        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", this.followerId)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should unfollow an user")
    public void unfollowUserTest() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", this.userId)
                .queryParam("followerId", this.followerId)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

}