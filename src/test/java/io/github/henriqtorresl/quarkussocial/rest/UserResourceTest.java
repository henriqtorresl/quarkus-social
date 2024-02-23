package io.github.henriqtorresl.quarkussocial.rest;

import io.github.henriqtorresl.quarkussocial.rest.dto.CreateUserRequest;
import io.github.henriqtorresl.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)   // Com essa antação na minha classe eu posso usar a antação de Order() nos meus metodos
class UserResourceTest {

    @TestHTTPResource("/users")     // url da api...
    URL apiURL;

    // garantindo que  metodo que testa o post vai ser executado antes do metodo que testa o list atraves da anotação @Order(1)
    @Test
    @DisplayName("Should create an user successfully")        // essa anotação especifica o que o teste faz
    @Order(1)   // primeiro que vai ser executado
    public void createUserTest() {
        var user = new CreateUserRequest();                   // objeto que é mandado no body da requisição de post
        user.setName("Fulano");
        user.setAge(30);

        // Montando o teste da requisição:
        var response = given()         // criando o cenário
                                .contentType(ContentType.JSON)      // definindo o tipo do conteúdo
                                .body(user)                         // passando um body pra requisição
                        .when()         // executando
                                .post(apiURL)                             // verbo HTTP
                        .then()         // faz a verificação final
                                .extract()
                                .response();

        // É esperado que essa requisição retorne o status 201
        assertEquals(201, response.statusCode());                // espero que retorne o status 201
        assertNotNull(response.jsonPath().getString("id"));         // espero que o id não venha nulo

        System.out.println("\nNome do usuário inserido no banco de teste: " + response.jsonPath().getString("name") +"\n");
        System.out.println("\nIdade do usuário inserido no banco de teste: " + response.jsonPath().getString("age") + "\n");
    }

    @Test
    @DisplayName("Should return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest() {
        var user = new CreateUserRequest();
        user.setName(null);                     // criando um objeto que não é valido
        user.setAge(null);

        var response = given()
                        .contentType(ContentType.JSON)
                        .body(user)
                .when()
                        .post(apiURL)
                .then()
                        .extract()
                        .response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Errors", response.jsonPath().getString("message"));         // espero que o conteudo do meu objeto no atributo "message" seja igual a "Validation Error"

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
//        assertEquals("Age is Required", errors.get(0).get("message"));        -> Pode ser que o objeto venha trocado o name pode acabar vindo na posição 0 e ai o test iria falhar, por isso eu deixei o assertNotNull
//        assertEquals("Name is Required", errors.get(1).get("message"));
    }

    @Test
    @DisplayName("Should list all users")
    @Order(3)   // garantindo que  metodo que testa o post vai ser executado antes do metodo que testa o list
    public void listAllUsersTest() {
        // OBS: Esse metodo não insere nenhum usuario no meu banco de test (banco em memoria), então se eu rodar esse teste isolado, ele irá falhar!
        // Para inserir um usuário no meu banco de test eu tenho que rodar o teste do metodo POST...

        given()
                .contentType(ContentType.JSON)
        .when()
                .get(apiURL)
        .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));                 // verifica o tamanho do array de objetos do response
    }

}