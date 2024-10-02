package com.redhat.cameljobs.aggregation;

import org.apache.camel.Exchange;

import java.util.ArrayList;
import java.util.List;

public class ArrayListAggregationStrategy implements org.apache.camel.AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        List<Object> list;
        if (oldExchange == null) {
            list = new ArrayList<>();
            list.add(newExchange.getIn().getBody());
            newExchange.getIn().setBody(list);
            return newExchange;
        } else {
            list = oldExchange.getIn().getBody(List.class);
            list.add(newExchange.getIn().getBody());
            return oldExchange;
        }
    }
}
