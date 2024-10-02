package com.redhat.cameljobs.policy;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;

public class CustomRoutePolicy extends RoutePolicySupport {

    @Override
    public void onInit(Route route) {
        super.onInit(route);
        System.out.println("Route " + route.getId() + " initialized");
    }

    @Override
    public void onRemove(Route route) {
        super.onRemove(route);
        System.out.println("Route " + route.getId() + " removed");
    }

    @Override
    public void onStart(Route route) {
        super.onStart(route);
        System.out.println("Route " + route.getId() + " started");
    }

    @Override
    public void onStop(Route route) {
        super.onStop(route);
        System.out.println("Route " + route.getId() + " stopped");
    }

    @Override
    public void onSuspend(Route route) {
        super.onSuspend(route);
        System.out.println("Route " + route.getId() + " suspended");
    }

    @Override
    public void onResume(Route route) {
        super.onResume(route);
        System.out.println("Route " + route.getId() + " resumed");
    }

    @Override
    public void onExchangeBegin(Route route, Exchange exchange) {
        super.onExchangeBegin(route, exchange);
        System.out.println("Exchange began on route " + route.getId());
    }

    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        super.onExchangeDone(route, exchange);
        System.out.println("Exchange completed on route " + route.getId());
    }
}