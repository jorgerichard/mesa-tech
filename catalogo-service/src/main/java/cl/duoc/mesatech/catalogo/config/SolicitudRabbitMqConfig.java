package cl.duoc.mesatech.catalogo.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration
@ConditionalOnProperty(prefix = "app.rabbitmq.legacy-solicitud", name = "enabled", havingValue = "true")
public class SolicitudRabbitMqConfig {
    @Bean("solicitudConnectionFactory")
    CachingConnectionFactory solicitudConnectionFactory(
            @Value("${SOLICITUD_RABBITMQ_HOST}") String host,
            @Value("${SOLICITUD_RABBITMQ_PORT:5672}") int port,
            @Value("${SOLICITUD_RABBITMQ_USER}") String username,
            @Value("${SOLICITUD_RABBITMQ_PASSWORD}") String password,
            @Value("${SOLICITUD_RABBITMQ_VHOST:mesatech}") String virtualHost) {
        Assert.hasText(host, "SOLICITUD_RABBITMQ_HOST must be configured when the legacy publisher is enabled");
        Assert.hasText(username, "SOLICITUD_RABBITMQ_USER must be configured when the legacy publisher is enabled");
        Assert.hasText(password, "SOLICITUD_RABBITMQ_PASSWORD must be configured when the legacy publisher is enabled");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirmType(ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean("solicitudRabbitTemplate")
    RabbitTemplate solicitudRabbitTemplate(
            @Qualifier("solicitudConnectionFactory") CachingConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter rabbitMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }
}
