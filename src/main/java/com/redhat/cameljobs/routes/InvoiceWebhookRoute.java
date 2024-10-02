package com.redhat.cameljobs.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;

import com.redhat.cameljobs.aggregation.ArrayListAggregationStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InvoiceWebhookRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Define the REST configuration for the Camel Rest DSL
        restConfiguration()
            .component("servlet")
            .port("8080")
            .bindingMode(RestBindingMode.auto);

        // Define the REST endpoint
        rest("/invoice")
            .get("/{invoice_id}")   // Define a GET request with invoice_id as parameter
            .to("direct:processInvoice");

                    // Define a REST endpoint to expose metrics
        rest("/metrics")
        .get()  // Define a GET request for metrics
        .to("micrometer:metrics");


        // Mock DB fetch function to get invoice items
        from("direct:processInvoice")
            .process(exchange -> {
                String invoiceId = exchange.getIn().getHeader("invoice_id", String.class);
                System.out.println("Invoice id = " + invoiceId);

                if (invoiceId == null || invoiceId.isEmpty()) {
                    exchange.getIn().setBody("Missing or invalid invoice_id parameter");
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                    return;
                }

                // Mock fetching the invoice items from DB
                List<Map<String, Object>> invoiceItems = mockInvoiceItems(invoiceId);
                exchange.getIn().setBody(invoiceItems);
            })
            .split(body())
                .to("seda:processItem")
            .end()
            .aggregate(constant(true), new ArrayListAggregationStrategy())  // Aggregate the results
                .completionSize(exchangeProperty(Exchange.SPLIT_SIZE))      // Complete when all splits are processed
            .marshal().json(JsonLibrary.Jackson)
            .log("Received message: ${body}") // Log the message body
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json")); // Set the response as JSON

        // Process individual invoice items using Seda
        from("seda:processItem?concurrentConsumers=5")
            .to("micrometer:counter:invoices.processed?tags=type=item") // Increment counter for processed invoice items
            .process(exchange -> {
                long startTime = System.nanoTime(); // Start timing

                Map<String, Object> invoiceItem = exchange.getIn().getBody(Map.class);
                // Mock fetching individual invoice item details
                Map<String, Object> itemDetails = mockInvoiceItemDetails((String) invoiceItem.get("item_id"));
                exchange.getIn().setBody(itemDetails);
                System.out.println(itemDetails);

                long duration = System.nanoTime() - startTime; // Calculate duration
                exchange.getContext().createProducerTemplate()
                        .sendBody("micrometer:timer:invoices.processing.time?tags=type=item", duration); // Record processing time
            });
    }

    // Mocked method to simulate fetching invoice items from DB
    private List<Map<String, Object>> mockInvoiceItems(String invoiceId) {
        List<Map<String, Object>> invoiceItems = new ArrayList<>();

        // Example mock items for the invoice
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("item_id", "item" + i);
            item.put("invoice_id", invoiceId);
            invoiceItems.add(item);
        }

        return invoiceItems;
    }

    // Mocked method to simulate fetching invoice item details
    private Map<String, Object> mockInvoiceItemDetails(String itemId) {
        Map<String, Object> itemDetails = new HashMap<>();
        itemDetails.put("item_id", itemId);
        itemDetails.put("description", "Description for " + itemId);
        itemDetails.put("quantity", 10);
        itemDetails.put("price", 100);
        return itemDetails;
    }
}
