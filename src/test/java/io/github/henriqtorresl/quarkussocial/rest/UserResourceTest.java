package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserResourceTest {

    @Test
    @DisplayName("should create an user successfully")        // essa anotação especifica o que o teste faz
    public void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(30);

        // Montando o teste da requisição:
        var response = given()         // criando o cenário
                                .contentType(ContentType.JSON)      // definindo o tipo do conteúdo
                                .body(user)                         // passando um body pra requisição
                        .when()         // executando
                                .post("/users")                             // verbo HTTP
                        .then()         // faz a verificação final
                                .extract()
                                .response();

        // É esperado que essa requisição retorne o status 201
        assertEquals(201, response.statusCode());                // espero que retorne o status 201
        assertNotNull(response.jsonPath().getString("id"));         // espero que o id não venha nulo

        System.out.println("\nNome do usuário inserido no banco de teste: " + response.jsonPath().getString("name") +"\n");
        System.out.println("\nIdade do usuário inserido no banco de teste: " + response.jsonPath().getString("age") + "\n");

    }

}