package com.redhat.cameljobs.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import com.redhat.cameljobs.policy.*;

public abstract class BaseRoute extends RouteBuilder {

    private final CustomRoutePolicy routePolicy;

    public BaseRoute() {
        this.routePolicy = new CustomRoutePolicy();
    }

    @Override
    public void configure() throws Exception {
        // Apply the route policy to all routes
      //  getContext().addRoutePolicyFactory((routePolicy);

        // Call the abstract method to define the route
        defineRoute();
    }

    /**
     * Abstract method to be implemented by subclasses to define the specific route logic.
     */
    protected abstract void defineRoute();

    /**
     * Helper method to create a route with the ExampleRoutePolicy applied.
     * @param uri The URI for the route's source
     * @return A RouteDefinition with the policy applied
     */
    protected RouteDefinition fromWithPolicy(String uri) {
        return from(uri).routePolicy(routePolicy);
    }
}