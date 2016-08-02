package com.jeeconf.kafka.sample;

import com.jeeconf.kafka.sample.configs.RouteConfiguration;
import com.jeeconf.kafka.sample.configs.TestBeansContext;
import com.jeeconf.kafka.sample.monitor.RouteMonitor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.kafka.KafkaConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductionRouteTest {

    @Autowired
    CamelContext camelContext;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    RouteConfiguration routeConfiguration;

    @Test
    public void shouldSendAndReceiveMessage() throws Exception {
        String message = "javaee conference";
        RouteMonitor routeMonitor = new RouteMonitor(camelContext);
        producerTemplate.sendBodyAndHeader(routeConfiguration.helloTopicEndpoint(), message, KafkaConstants.KEY, "12345678");

        routeMonitor.getResultEndpoint().expectedBodiesReceived(message);
        routeMonitor.getResultEndpoint().assertIsSatisfied(3000);
    }


    @TestConfiguration
    @Import(TestBeansContext.class)
    public static class TestConfigurationImporter {}
}