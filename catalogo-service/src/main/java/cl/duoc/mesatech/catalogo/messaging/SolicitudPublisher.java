package cl.duoc.mesatech.catalogo.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.rabbitmq.legacy-solicitud", name = "enabled", havingValue = "true")
public class SolicitudPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public SolicitudPublisher(
            @Qualifier("solicitudRabbitTemplate") RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.legacy-solicitud.exchange}") String exchange,
            @Value("${app.rabbitmq.legacy-solicitud.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publicar(SolicitudCreadaEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
