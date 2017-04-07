package io.capitalone.interview.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;


/**
 * Created by aseem80 on 4/4/17.
 */

@Configuration
@PropertySource(value = { "classpath:application.properties" })
@ComponentScan(basePackages = "io.capitalone.interview")
public class CodingAssignmentAppConfig {

    @Bean(destroyMethod="close")
    public Client getClient() {
        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        return client;
    }

}
