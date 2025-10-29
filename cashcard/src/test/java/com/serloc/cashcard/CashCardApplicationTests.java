package com.serloc.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashCardApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    void userDetailsServiceShouldLoadSarah() {
        var sarah = userDetailsService.loadUserByUsername("sarah1");
        assertThat(sarah).isNotNull();
        assertThat(sarah.getAuthorities().toString()).contains("ROLE_CARD-OWNER");
    }

	@Test
	void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","abc123")
                .getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
	}

    @Test
    void shouldNotReturnACashCardWithUnknownId(){
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","abc123")
                .getForEntity("/cashcards/1000", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void shouldCreateANewCashCard(){
        //We send a post to the server to test that creates a new CashCard and add the ID
        CashCard newCashCard = new CashCard(null, 250.00,null);
        ResponseEntity<Void> createResponse = restTemplate.withBasicAuth("sarah1", "abc123")
                .postForEntity("/cashcards", newCashCard, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //The location is where we can find the CashCard info with a GET
        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("sarah1", "abc123")
                .getForEntity(locationOfNewCashCard, String.class);
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
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","abc123")
                .getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<CashCard> cashCardsList = documentContext.read("$");
        assertThat(cashCardsList).isNotNull();

    }

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested(){
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","abc123")
                .getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Extracting the list as a JSON
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int cashCardCount = documentContext.read("$.length()");
        assertThat(cashCardCount).isEqualTo(3);
        List ids = documentContext.read("$..id", List.class);
        assertThat(ids).containsExactlyInAnyOrder(99,100,101);

        List amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00);
    }

    @Test
    void shouldReturnAPageOfCashCards(){
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","abc123").getForEntity("/cashcards?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);
        Number id = documentContext.read("$[0].id");
        assertThat(id).isEqualTo(100);
    }
    @Test
    void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","abc123")
                .getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactly(1.00, 123.45, 150.00);
    }

    @Test
    void shouldNotReturnACashCardWhenUsingBadCredentials() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("BAD-USER", "abc123")
                .getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        response = restTemplate
                .withBasicAuth("sarah1", "BAD-PASSWORD")
                .getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectUsersWhoAreNotCardOwners() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("hank-owns-no-cards", "qrs456")
                .getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/102", String.class); // kumar2's data
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnExistingCashCard(){
        CashCard cashCardToUpdate = new CashCard(null,19.99,null);
        HttpEntity<CashCard> request = new HttpEntity<>(cashCardToUpdate);
        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "abc123")
                .exchange("/cashcards/99", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        //Get the modified card to check if it changed
        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/99", String.class);

        //check if the modified card exists
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);


        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);

        //Check if the amount is changed
        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(19.99);
    }

    @Test
    void shouldNotUpdateACashCardThatNotExist(){
        CashCard unknownCashCard = new CashCard(null, 19.99, null);
        HttpEntity<CashCard> request = new HttpEntity<>(unknownCashCard);
        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "abc123")
                .exchange("/cashcards/202", HttpMethod.PUT, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotUpdateACashCardThatIsOwnedBySomeoneElse(){
        CashCard unknownOwnerCashCard = new CashCard(null, 19.99, null);
        HttpEntity<CashCard> request = new HttpEntity<>(unknownOwnerCashCard);
        ResponseEntity<Void> response = restTemplate.withBasicAuth("hank-owns-no-cards", "qrs456")
                .exchange("/cashcards/99", HttpMethod.PUT, request, Void.class);

        //Its forbidden because hanks has not OWNER role in SecurityConfig
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingCashCard(){
        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1","abc123")
                .exchange("/cashcards/99", HttpMethod.DELETE,null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/99", String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteANonExistingCard(){
        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "abc123")
                .exchange("/cashcards/999999", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    void shouldNotAllowDeletionOfCashCardsTheyDoNotOwn() {
        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "abc123")
                .exchange("/cashcards/102", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }



}
