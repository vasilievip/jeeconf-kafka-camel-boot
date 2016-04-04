package com.jeeconf.kafka.sample.configs;

public class QueueTestConfiguration implements RouteConfiguration {

    @Override
    public String helloTopicEndpoint() {
        return "seda:helloTopic";
    }
}
