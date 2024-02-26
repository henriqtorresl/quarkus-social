package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.domain.model.Follower;
import io.github.henriqtorresl.quarkussocial.domain.model.Post;
import io.github.henriqtorresl.quarkussocial.domain.model.User;
import io.github.henriqtorresl.quarkussocial.domain.repository.FollowerRepository;
import io.github.henriqtorresl.quarkussocial.domain.repository.PostRepository;
import io.github.henriqtorresl.quarkussocial.domain.repository.UserRepository;
import io.github.henriqtorresl.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
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
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;
    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    // Garantindo que vou criar um usuário antes dos testes
    @BeforeEach     // Essa anotação me diz que esse metodo é sempre o primeiro a ser executado
    @Transactional  // como eu uso o metodo "persist" do UsuarioRepository eu preciso dessa anotação
    public void setUP() {
        // Usuário Padrão dos testes
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);
        this.userRepository.persist(user);
        this.userId = user.getId();

        // Postagem do usuário padrão
        Post post = new Post();
        post.setPostText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        // Usuário que não segue ninguém
        var userNotFollower = new User();
        userNotFollower.setName("Ciclano");
        userNotFollower.setAge(25);
        this.userRepository.persist(userNotFollower);
        this.userNotFollowerId = userNotFollower.getId();

        // Usuário seguidor
        var userFollower = new User();
        userFollower.setName("Bacano");
        userFollower.setAge(33);
        this.userRepository.persist(userFollower);
        this.userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        this.followerRepository.persist(follower);


        System.out.println("Executed before any one methods");
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

    @Test
    @DisplayName("Should return 404 when trying to make a post for an inexistent user")
    public void postForAnnexistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some Text");

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParams("userId", inexistentUserId)
        .when()
                .post()
        .then()
                .statusCode(404);
    }

    // Testando cada um dos retornos do método list post:
    // Todas as vezes que eu for implementar os testes de um método, é melhor vizualisar todos os cenários que deverão ser testados...
    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest() {
        var inexistentUserId = 999;

        given()
                .pathParams("userId", inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when followerId header is not present")
    public void listPostFollowerNotFoundTest() {
        given()
                .pathParams("userId", this.userId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("Should return 400 when follower doesn't exist")
    public void listPostFollowerDoesntExistTest() {
        var inexistentFollowerId = 999;

        given()
                .pathParams("userId", this.userId)
                .header("followerId", inexistentFollowerId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @DisplayName("Should return 403 when follower isn't follower")
    public void listPostNotAFollowerTest() {
        given()
                .pathParams("userId", this.userId)
                .header("followerId", this.userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("Should list posts")
    public void listPostTest() {
        given()
                .pathParams("userId", this.userId)
                .header("followerId", this.userFollowerId)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

}