package com.jeeconf.kafka.sample.configs;

import com.jeeconf.kafka.embedded.EmbeddedKafkaCluster;
import org.springframework.beans.factory.annotation.Autowired;

public class EmbeddedKafkaTestConfiguration implements RouteConfiguration {

    @Autowired
    private EmbeddedKafkaCluster kafkaCluster;

    @Override
    public String helloTopicEndpoint() {
        return ("kafka:" + kafkaCluster.getBrokerList()) +
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
