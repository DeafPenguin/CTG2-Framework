import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;

public class TesteCliente {

    private static final String ENDERECO_CLIENTE = "http://localhost:8080/";
    private static final String ENDPOINT_CLIENTE = "cliente/";
    private static final String APAGA_TODOS_CLIENTES = "apagaTodos/";
    private static final String LISTA_CLIENTES_VAZIA = "{}";

    @Test
    @DisplayName("Quando pegar todos os clientes sem cadastrar clientes, então a lista deve estar vazia.")
    public void listaTodosClientesComListaVazia(){

        apagaTodosClientesDoServidor();

        pegaTodosClientes()
                .statusCode(HttpStatus.SC_OK)
                .body(new IsEqual<>(LISTA_CLIENTES_VAZIA));
    }

    @Test
    @DisplayName("Quando cadastrar um cliente, então ele deve estar disponível no resultado.")
    public void cadastraCliente(){

        Cliente cadastroCliente = new Cliente("Victor Murilo", 29, 2);

        postaCliente(cadastroCliente)
                .statusCode(HttpStatus.SC_CREATED)
                .body("2.nome", equalTo("Victor Murilo"))
                .body("2.idade", equalTo(29))
                .body("2.id", equalTo(2));
    }

    @Test
    @DisplayName("Quando atualizar um cliente, então ele deve estar disponível e atualizado no resultado.")
    public void atualizaCliente(){

        Cliente cadastroCliente = new Cliente("Victor Murilo", 29, 2);

        postaCliente(cadastroCliente);

        cadastroCliente.setNome("Luana Najara");

        atualizaCliente(cadastroCliente)
                .statusCode(HttpStatus.SC_OK)
                .body("2.nome", equalTo("Luana Najara"))
                .body("2.idade", equalTo(29))
                .body("2.id", equalTo(2));
    }

    @Test
    @DisplayName("Quando deletar um cliente, então ele NÃO deve estar disponível no resultado.")
    public void deletaCliente(){

        Cliente cadastroCliente = new Cliente("Victor Murilo", 29, 2);
        String respostaEsperada = "CLIENTE REMOVIDO: { NOME: Victor Murilo, IDADE: 29, ID: 2 }";

        postaCliente(cadastroCliente);

        apagaCliente(cadastroCliente)
                .statusCode(HttpStatus.SC_OK)
                .assertThat().body(containsString(respostaEsperada))
                .assertThat().body(not(contains("Victor Murilo")));
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
     * Posta cliente para nossa API de teste
     * @param clienteParaPostar
     */
     private ValidatableResponse postaCliente (Cliente clienteParaPostar)  {
        return given()
                .contentType(ContentType.JSON)
                .body(clienteParaPostar)
                .when()
                .post(ENDERECO_CLIENTE + ENDPOINT_CLIENTE)
                .then();
    }

    /**
     * Atualiza cliente na nossa API de teste
     * @param clienteParaAtualizar
     * @return
     */
    private ValidatableResponse atualizaCliente (Cliente clienteParaAtualizar) {
        return given()
                .contentType(ContentType.JSON)
                .body(clienteParaAtualizar).
                when().
                put(ENDERECO_CLIENTE + ENDPOINT_CLIENTE).
                then();
    }

    /**
     * Apaga um cliente em específico da nossa API de teste
     * @param clienteApagar
     * @return
     */
    private ValidatableResponse apagaCliente (Cliente clienteApagar) {
        return  given()
                .contentType(ContentType.JSON)
                .when()
                .delete(ENDERECO_CLIENTE + ENDPOINT_CLIENTE + clienteApagar.getId())
                .then();
    }

    /**
     * Método de apoio para apagar todos os clientes do servidor.
     * Usado para teste apenas.
     * Incluindo como hook para rodar ao final de cada teste e deixar o servidor no mesmo estado em que estava antes.
     * Chamado explicitamente em alguns testes também como preparação
     */
    @AfterEach
    void apagaTodosClientesDoServidor(){

        when()
                .delete(ENDERECO_CLIENTE + ENDPOINT_CLIENTE + APAGA_TODOS_CLIENTES)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .assertThat().body(new IsEqual(LISTA_CLIENTES_VAZIA));
    }
}
