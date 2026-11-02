package cl.duoc.mesatech.catalogo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    DirectExchange solicitudesExchange(@Value("${app.rabbitmq.exchange}") String name) {
        return new DirectExchange(name, true, false);
    }

    @Bean
    Queue solicitudesQueue(@Value("${app.rabbitmq.queue}") String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    Binding solicitudesBinding(Queue solicitudesQueue, DirectExchange solicitudesExchange,
            @Value("${app.rabbitmq.routing-key}") String routingKey) {
        return BindingBuilder.bind(solicitudesQueue).to(solicitudesExchange).with(routingKey);
    }

    @Bean
    Jackson2JsonMessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
