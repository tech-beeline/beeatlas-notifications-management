package ru.beeline.fdmnotificationsmanagement.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
@Configuration
public class RabbitConfig {

    @Autowired
    RestTemplate restTemplate;

    @Value("${spring.rabbitmq.token-url}")
    private String tokenUrl;


    @Value("${spring.rabbitmq.username}")
    private String userName;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${queue.tech-capability.name}")
    private String queueName;

    @Value("${spring.rabbitmq.template.exchange}")
    private String topicExchangeName;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingName;

    @Value("${spring.rabbitmq.host}")
    private String connectFactoryName;

    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    DirectExchange directExchange(){
        return new DirectExchange(topicExchangeName);
    }
    @Bean
    Binding binding(Queue queue, DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).with(routingName);
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "username=" + userName + "&password=" + password +
                "&client_id=producer&grant_type=password";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);


        String accessToken = (String) response.getBody().get("access_token");
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectFactoryName);
        cachingConnectionFactory.setUsername(userName);
        cachingConnectionFactory.setPassword(accessToken);
        cachingConnectionFactory.setVirtualHost(virtualHost);
        return cachingConnectionFactory;
    }


    @Bean
    MessageConverter messageConverter() {
        return new SimpleMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
