package com.jeeconf.kafka.sample.configs;

import org.springframework.beans.factory.annotation.Value;

public class ProductionRouteConfiguration implements RouteConfiguration {

    @Value("${service.helloTopic.URI}")
    private String helloTopicURI;

    @Override
    public String helloTopicEndpoint() {
        return helloTopicURI;
    }

    @Override
    public String toString() {
        return "ProductionRouteConfiguration{" +
                "helloTopicEndpoint='" + helloTopicURI + '\'' +
                '}';
    }
}
