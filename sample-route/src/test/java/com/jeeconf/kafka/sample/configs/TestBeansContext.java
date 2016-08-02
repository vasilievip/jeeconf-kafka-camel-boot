package com.jeeconf.kafka.sample.configs;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestBeansContext {

    @Bean
    @ConditionalOnProperty(prefix = "embedded.kafka", name = "enabled", havingValue = "false", matchIfMissing = true)
    public RouteConfiguration queueRouteConfiguration() {
        return new QueueTestConfiguration();
    }

    @Bean
    @ConditionalOnProperty(prefix = "embedded.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RouteConfiguration kafkaRouteConfiguration() {
        return new EmbeddedKafkaTestConfiguration();
    }

    @Bean
    public ProducerTemplate testProducer(CamelContext camelContext) {
        return camelContext.createProducerTemplate();
    }
}
