package com.jeeconf.kafka.sample.configs;

import com.netflix.config.ConfigurationManager;

public class EmbeddedKafkaTestConfiguration implements RouteConfiguration {

    @Override
    public String helloTopicEndpoint() {
        return "kafka:" + ConfigurationManager.getConfigInstance().getProperty("kafka.brokerList") +
                "?" +
                "topic=helloTopic" +
                "&" +
                "groupId=testConsumer" +
                "&" +
                "autoOffsetReset=earliest" +
                "&" +
                "keyDeserializer=org.apache.kafka.common.serialization.StringDeserializer" +
                "&" +
                "valueDeserializer=org.apache.kafka.common.serialization.StringDeserializer" +
                "&" +
                "autoCommitIntervalMs=100" +
                "&" +
                "sessionTimeoutMs=30000" +
                "&" +
                "autoCommitEnable=true";
    }
}
