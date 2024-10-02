package com.redhat.cameljobs.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import com.redhat.cameljobs.aggregation.ArrayListAggregationStrategy;
import com.redhat.cameljobs.services.InvoiceService;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InvoiceWebhookRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Global exception handling
        onException(Exception.class)
            .handled(true)
            .logStackTrace(true)
            .log("Unexpected exception occurred: ${exception.class}: ${exception.message}")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
            .setBody(simple("{ \"error\": \"An unexpected error occurred: ${exception.message}\" }"))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        // Specific exception handling
        onException(IllegalArgumentException.class)
            .handled(true)
            .log("Invalid input: ${exception.message}")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setBody(simple("{ \"error\": \"Invalid input: ${exception.message}\" }"))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        // Define the REST configuration for the Camel Rest DSL
        restConfiguration()
            .component("servlet")
            .port("8080")
            .bindingMode(RestBindingMode.auto);

        // Define the REST endpoint
        rest("/invoice")
            .get("/{invoice_id}")
            .to("direct:processInvoice");

        // Define a REST endpoint to expose metrics
        rest("/metrics")
            .get()
            .to("micrometer:metrics");

        // Process invoice items and aggregate results
        from("direct:processInvoice")
            .doTry()
                //We can use .process .pollEnrich or .bean to fetch data. They look similiar but they work differently
                //.pollEnrich().simple("sql:select * from invoice where invoice_id=${header.queueName}")
                // .process(exchange -> {
                //     String invoiceId = exchange.getIn().getHeader("invoice_id", String.class);
                //     System.out.println("Invoice id = " + invoiceId);

                //     if (invoiceId == null || invoiceId.isEmpty()) {
                //         exchange.getIn().setBody("Missing or invalid invoice_id parameter");
                //         exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                //         return;
                //     }

                //     // Mock fetching the invoice items from DB
                //     List<Map<String, Object>> invoiceItems = mockInvoiceItems(invoiceId);
                //     exchange.getIn().setBody(invoiceItems);
                // })
                .bean(InvoiceService.class, "getInvoiceItems(${header.invoice_id})")
                .split(body()).parallelProcessing()
                    .to("seda:processItem")
                .end()
                .aggregate(constant(true), new ArrayListAggregationStrategy())
                    .completionSize(exchangeProperty(Exchange.SPLIT_SIZE))
                .marshal().json(JsonLibrary.Jackson)
                .log("Processed invoice: ${body}")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("Error processing invoice: ${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setBody(simple("{ \"error\": \"Failed to process invoice: ${exception.message}\" }"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .endDoTry()
            .doCatch(Exception.class)
                .log("Processing complete for invoice ${header.invoice_id}")
            .end();

        // Process individual invoice items using Seda
        from("seda:processItem?concurrentConsumers=5")
            .doTry()
                .to("micrometer:counter:invoices.processed?tags=type=item")
                .bean(InvoiceService.class, "getInvoiceItemDetails(${body[item_id]})")
                .log("Processed item details: ${body}")
            .doCatch(Exception.class)
                .log("Error processing invoice item: ${exception.message}")
                .to("micrometer:counter:invoices.errors?tags=type=item")
                .setBody(simple("{ \"error\": \"Failed to process invoice item: ${exception.message}\" }"))
            .end();
    }
}