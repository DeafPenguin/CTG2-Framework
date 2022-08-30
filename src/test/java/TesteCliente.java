import io.restassured.http.ContentType;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class TesteCliente {

    String enderecoAPICliente = "http://localhost:8080/";
    String endpointCliente = "cliente/";

    @Test
    @DisplayName("Quando pegar todos os clientes sem cadastrar clientes, " +
            "então a lista deve estar vazia.")
    public void pegaTodosClientes(){

        String respostaEsperada = "{}";

        given()
                .contentType(ContentType.JSON)
        .when()
                .get(enderecoAPICliente)
        .then()
                .statusCode(200)
                .assertThat().body(new IsEqual<>(respostaEsperada));
    }

    @Test
    @DisplayName("Quando cadastrar um cliente, " +
            "então ele deve estar disponível no resultado.")
    public void cadastraCliente(){

        String clienteParaCadastrar = "{\n" +
                "    \"nome\": \"Murilo\",\n" +
                "    \"idade\": 29,\n" +
                "    \"id\": 2\n" +
                "}";

        String respostaEsperada = "{\"2\":{\"nome\":\"Murilo\",\"idade\":29,\"id\":2,\"risco\":0}}";

        given()
                .contentType(ContentType.JSON)
                .body(clienteParaCadastrar)
        .when()
                .post(enderecoAPICliente+endpointCliente)
        .then()
                .statusCode(201)
                .assertThat().body(containsString(respostaEsperada));
    }

    @Test
    @DisplayName("Quando atualizar um cliente, " +
            "então ele deve estar disponível e atualizado no resultado.")
    public void atualizaCliente(){

        String clienteParaAtualizar = "{\n" +
                "    \"nome\": \"Luana\",\n" +
                "    \"idade\": 29,\n" +
                "    \"id\": 2\n" +
                "}";

        String respostaEsperada = "{\"2\":{\"nome\":\"Luana\",\"idade\":29,\"id\":2,\"risco\":0}}";

        given()
                .contentType(ContentType.JSON)
                .body(clienteParaAtualizar)
        .when()
                .put(enderecoAPICliente+endpointCliente)
        .then()
                .statusCode(200)
                .assertThat().body(containsString(respostaEsperada));
    }

    @Test
    @DisplayName("Quando deletar um cliente, " +
            "então ele NÃO deve estar disponível no resultado.")
    public void deletaCliente(){

        String idClienteParaDeletar = "2";
        String respostaEsperada = "CLIENTE REMOVIDO: { NOME: Luana, IDADE: 29, ID: 2 }";

        given()
                .contentType(ContentType.JSON)
        .when()
                .delete(enderecoAPICliente+endpointCliente+idClienteParaDeletar)
        .then()
                .statusCode(200)
                .assertThat().body(containsString(respostaEsperada));
    }
}
