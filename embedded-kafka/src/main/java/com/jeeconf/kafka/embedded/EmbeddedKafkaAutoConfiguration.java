package com.jeeconf.kafka.embedded;

import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@ConditionalOnProperty(prefix = "embedded.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(CamelAutoConfiguration.class)
public class EmbeddedKafkaAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public EmbeddedZookeeper embeddedZookeeper() {
        int zookeeperPort = TestUtils.getAvailablePort();
        return new EmbeddedZookeeper(zookeeperPort);
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public EmbeddedKafkaCluster embeddedKafkaCluster(EmbeddedZookeeper embeddedZookeeper) {
        List<Integer> kafkaPorts = new ArrayList<Integer>();
        // -1 for any available port
        // number of ports == number of brokers
        kafkaPorts.add(TestUtils.getAvailablePort());
        EmbeddedKafkaCluster embeddedKafkaCluster = new EmbeddedKafkaCluster(embeddedZookeeper.getConnection(),
                new Properties(), kafkaPorts);
        try {
            embeddedZookeeper.startup();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        logger.debug("Embedded Zookeeper connection: " + embeddedZookeeper.getConnection());
        embeddedKafkaCluster.startup();
        logger.debug("Embedded Kafka cluster broker list: " + embeddedKafkaCluster.getBrokerList());
        return embeddedKafkaCluster;
    }
}
