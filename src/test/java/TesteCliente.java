import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;

public class TesteCliente {

    private static final String ENDERECO_CLIENTE = "http://localhost:8080/";
    private static final String ENDPOINT_CLIENTE = "cliente/";
    private static final String APAGA_TODOS_CLIENTES = "apagaTodos/";
    private static final String LISTA_CLIENTES_VAZIA = "{}";

    @Test
    @DisplayName("Quando pegar todos os clientes sem cadastrar clientes, " +
            "então a lista deve estar vazia.")
    public void listaTodosClientesComListaVazia(){

        apagaTodosClientesDoServidor();

        pegaTodosClientes()
                .statusCode(200)
                .body(new IsEqual<>(LISTA_CLIENTES_VAZIA));
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
                .post(ENDERECO_CLIENTE+ ENDPOINT_CLIENTE)
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
                .put(ENDERECO_CLIENTE+ ENDPOINT_CLIENTE)
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
                .delete(ENDERECO_CLIENTE+ ENDPOINT_CLIENTE +idClienteParaDeletar)
        .then()
                .statusCode(200)
                .assertThat().body(containsString(respostaEsperada));
    }

    /**
     * Pega todos os clientes cadastrados na API
     * @return lista com todos os clientes wrapped no tipo de resposta do restAssured
     */
    private ValidatableResponse pegaTodosClientes () {
        return  given()
                .contentType(ContentType.JSON)
                .when()
                .get(ENDERECO_CLIENTE)
                .then();
    }

    /**
     * Método de apoio para apagar todos os clientes do servidor.
     * Usado para teste apenas.
     * Incluindo como hook para rodar ao final de cada teste e deixar o servidor no mesmo estado em que estava antes.
     * Chamado explicitamente em alguns testes também como preparação
     */
    @AfterEach
    private void apagaTodosClientesDoServidor(){

        when()
                .delete(ENDERECO_CLIENTE + ENDPOINT_CLIENTE + APAGA_TODOS_CLIENTES)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .assertThat().body(new IsEqual(LISTA_CLIENTES_VAZIA));
    }
}
