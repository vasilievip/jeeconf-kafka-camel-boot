package com.jeeconf.kafka.sample;

import com.jeeconf.kafka.sample.configs.RouteConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
        exclude = {ProjectInfoAutoConfiguration.class}
)
public class ProductionRouteContext {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RouteConfiguration configuration;

    @Bean
    public RouteBuilder productionRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(configuration.helloTopicEndpoint())
                        .process(exchange -> {
                            logger.info("------------------ BEGIN OF RECEIVED MESSAGE --------------");
                            logger.info("Hello " + exchange.getIn().getBody().toString() + "!");
                            logger.info("Message ID " + exchange.getIn().getHeader(KafkaConstants.KEY));
                            logger.info("------------------- END OF RECEIVED MESSAGE ---------------");
                        })
                        .end()
                        .setId("productionRoute");
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ProductionRouteContext.class, args);
    }
}
