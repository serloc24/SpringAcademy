package com.serloc.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashCardApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

	@Test
	void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
	}

    @Test
    void shouldNotReturnACashCardWithUnknownId(){
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    void shouldCreateANewCashCard(){
        //We send a post to the server to test that creates a new CashCard and add the ID
        CashCard newCashCard = new CashCard(null, 250.00);
        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", newCashCard, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //The location is where we can find the CashCard info with a GET
        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Assert to check the response content
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");
        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);

    }

    @Test
    void shouldReturnAListOfCashCards(){
        //First assert that exists cash cards
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<CashCard> cashCardsList = documentContext.read("$");
        assertThat(cashCardsList).isNotNull();

    }

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested(){
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Extracting the list as a JSON
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int cashCardCount = documentContext.read("$.length");
        assertThat(cashCardCount).isEqualTo(3);
        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99,100,101);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 100.0, 150.00);
    }

}
