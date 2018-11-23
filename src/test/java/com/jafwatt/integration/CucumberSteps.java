package com.jafwatt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.jafwatt.TestApplication;
import com.jafwatt.model.Customer;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@ActiveProfiles("INTEGRATION_TESTS")
@ContextConfiguration
public class CucumberSteps {

    @LocalServerPort
    int randomServerPort;

    private Customer originalCustomer;
    private Customer returnedCustomer;

    @Value("${api.path}")
    private String apiPath;

    @Value("${third-party-web-service.port}")
    private int thirdPartyWebServicePort;

    @Value("${third-party-web-service.path}")
    private String thirdPartyWebServicePath;

    @Before
    public void before() {

        /* Make sure we're not carrying over any data between scenarios */
        originalCustomer = null;
        returnedCustomer = null;

        /* Set the base URL for calls to our web service which will be
           running locally but with a random port assigned by Spring
        */
        RestAssured.baseURI = String.format("http://localhost:%d", randomServerPort);

        /* Mock the 3rd party web service */
        WireMockServer wireMockServer = new WireMockServer(thirdPartyWebServicePort);
        wireMockServer.start();
        configureFor("localhost", thirdPartyWebServicePort);
        stubFor(get(thirdPartyWebServicePath).willReturn(aResponse().withStatus(200)));
    }

    @Given("the application is running")
    public void the_application_is_running() {

        /* Call the Spring Actuator health endpoint */
        RestAssured.when().get("/actuator/health").then().statusCode(200);
    }

    @When("I call the REST endpoint to create a new customer")
    public void i_call_the_REST_endpoint_to_create_a_new_customer() throws IOException {

        originalCustomer = new Customer();
        originalCustomer.setName("A B Sample");

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(new ObjectMapper().writeValueAsString(originalCustomer));

        Response response = request.post(apiPath);

        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        returnedCustomer = new ObjectMapper().readValue(response.body().asString(), Customer.class);
    }

    @Then("I get a successful response")
    public void i_get_a_successful_response() {
        Assert.assertNotNull(returnedCustomer);
        Assert.assertNotNull(returnedCustomer.getId());
        Assert.assertNotNull(returnedCustomer.getCreated());
        Assert.assertEquals(originalCustomer.getReference(), returnedCustomer.getReference());
        Assert.assertEquals(originalCustomer.getName(), returnedCustomer.getName());
    }
}
