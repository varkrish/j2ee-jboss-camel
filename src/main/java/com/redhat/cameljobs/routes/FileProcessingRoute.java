package com.redhat.cameljobs.routes;


import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import javax.enterprise.context.ApplicationScoped;
import org.apache.camel.LoggingLevel;
@ApplicationScoped
public class FileProcessingRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {
        // Explicitly configure Camel to load the properties file
        getContext().getPropertiesComponent().setLocation("classpath:application.properties");

        // Route processing files from a local folder
        from("file:{{file.input.dir}}?delete=true")
            .routeId("fileProcessingRoute")
          //  .process(this::onExchange) // Call the onExchange method
            .log(LoggingLevel.INFO, "Processing file: ${file:name}")
            // .process(exchange -> {
            //     GenericFile<?> file = (GenericFile<?>) exchange.getIn().getBody();
            //     // Perform file processing logic here
            //     System.out.println("File " + file.getFileName() + " is being processed.");
            // })
            .to("file:{{file.output.dir}}?fileName=${file:name}.done")
            .log(LoggingLevel.INFO, "File processed and moved to output.");

    }
}