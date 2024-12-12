package ca.gbc.orderservice.stub;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class InventoryClientStub {

    /**
     * This methos sets up a stub for the GET request made to the inventory-service.
     * It will respond with an HTTP 200 status and a body containing "true", indicating
     * that the requested item is in stock
     * @param skuCode
     * @param quantity
     */

    public static void stubInventoryCall(String skuCode, Integer quantity){

        // Using WireMock's stubFor method to cock an HTTP GET request to the inventory-service
        // We define that any GET request with the specified URL pattern will return a response
        // With a 200 status code, a Content-Type header of "application/json", and a body of "true"

        stubFor(get(urlEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(aResponse()
                        .withStatus(200) // the HTTP status code to return (200 ok).
                        .withHeader("Content-Type", "application/json")// Response header to specify JSON content.
                        .withBody("true"))); // The mock response body indicating the item is in stock
    }
}
