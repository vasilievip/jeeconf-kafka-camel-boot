package com.jeeconf.kafka.sample.monitor;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.ProcessDefinition;

public class RouteMonitor {

    private final MockEndpoint resultEndpoint;

    public RouteMonitor(CamelContext camelContext) throws Exception {
        camelContext.getRouteDefinition("productionRoute").adviceWith((ModelCamelContext) camelContext,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveByType(ProcessDefinition.class)
                                .before()
                                .to("mock:end");
                    }
                });
        resultEndpoint = camelContext.getEndpoint("mock:end", MockEndpoint.class);
    }

    public MockEndpoint getResultEndpoint() {
        return resultEndpoint;
    }
}
