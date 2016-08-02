package com.jeeconf.kafka.embedded;

import com.netflix.config.ConfigurationManager;
import org.apache.camel.CamelContext;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.*;

@Configuration
@ConditionalOnProperty(prefix = "embedded.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(CamelAutoConfiguration.class)
public class EmbeddedKafkaAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public EmbeddedZookeeper embeddedZookeeper() {
        int zookeeperPort = TestUtils.getAvailablePort();
        EmbeddedZookeeper embeddedZookeeper = new EmbeddedZookeeper(zookeeperPort);
        try {
            embeddedZookeeper.startup();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return embeddedZookeeper;
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

        logger.debug("Embedded Zookeeper connection: " + embeddedZookeeper.getConnection());
        embeddedKafkaCluster.startup();
        logger.debug("Embedded Kafka cluster broker list: " + embeddedKafkaCluster.getBrokerList());
        return embeddedKafkaCluster;
    }


    @Configuration
    @ConditionalOnClass(ConfigurationManager.class)
    public static class ConfigSettingsSetup {

        @Bean
        public EmbeddedKafkaInfo embeddedKafkaInfo(EmbeddedKafkaCluster embeddedKafkaCluster) {

            ConfigurationManager.getConfigInstance().setProperty("kafka.brokerList", embeddedKafkaCluster.getBrokerList());

            return new EmbeddedKafkaInfo(embeddedKafkaCluster.getBrokerList(), embeddedKafkaCluster.getZkConnection());
        }

        public static class EmbeddedKafkaInfo {
            public final String brokerList;
            public final String zookeeperConnect;

            public EmbeddedKafkaInfo(String brokerList, String zookeeperConnect) {
                this.brokerList = brokerList;
                this.zookeeperConnect = zookeeperConnect;
            }

            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder("EmbeddedKafkaInfo{");
                sb.append("brokerList='").append(brokerList).append('\'');
                sb.append(", zookeeperConnect='").append(zookeeperConnect).append('\'');
                sb.append('}');
                return sb.toString();
            }
        }
    }

    /**
     * Additional configuration to ensure that {@link org.apache.camel.CamelContext} beans
     * depend-on the embedded kafka bean and configuration was setup into archaius.
     */
    @Configuration
    @ConditionalOnClass({CamelContext.class, ConfigurationManager.class})
    protected static class KafkaCamelDependencyConfiguration
            implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            List<String> camelContexts = Arrays.asList(beanFactory
                    .getBeanNamesForType(CamelContext.class, true, true));
            camelContexts.stream().forEach(
                    beanName -> setupDependsOn(beanFactory, beanName)
            );
        }

        private void setupDependsOn(ConfigurableListableBeanFactory beanFactory, String beanName) {
            BeanDefinition bean = beanFactory.getBeanDefinition(beanName);
            Set<String> dependsOn = new LinkedHashSet<String>(asList(bean.getDependsOn()));
            dependsOn.add("embeddedKafkaInfo");
            bean.setDependsOn(dependsOn.toArray(new String[dependsOn.size()]));
        }

        private List<String> asList(String[] array) {
            return (array == null ? Collections.<String>emptyList() : Arrays.asList(array));
        }

    }
}
