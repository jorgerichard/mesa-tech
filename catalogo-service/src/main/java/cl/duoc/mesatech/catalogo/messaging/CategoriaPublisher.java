package cl.duoc.mesatech.catalogo.messaging;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CategoriaPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;
    private final Duration confirmTimeout;

    public CategoriaPublisher(
            @Qualifier("categoriaRabbitTemplate") RabbitTemplate rabbitTemplate,
            @Value("${RABBITMQ_EXCHANGE:catalogo.exchange}") String exchange,
            @Value("${RABBITMQ_ROUTING_KEY:categoria.creada}") String routingKey,
            @Value("${app.rabbitmq.confirm-timeout:10s}") Duration confirmTimeout) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.confirmTimeout = confirmTimeout;
    }

    public void publicar(CategoriaCreadaEvent event) {
        CorrelationData correlationData = new CorrelationData(event.eventId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event, message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        }, correlationData);

        try {
            CorrelationData.Confirm confirm = correlationData.getFuture()
                    .get(confirmTimeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!confirm.isAck()) {
                throw new AmqpException("RabbitMQ rechazó el evento de categoría: " + confirm.getReason());
            }
            if (correlationData.getReturned() != null) {
                throw new AmqpException("RabbitMQ no enroutó el evento de categoría: "
                        + correlationData.getReturned().getReplyText());
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AmqpException("Interrumpida la espera de confirmación de RabbitMQ", exception);
        } catch (ExecutionException exception) {
            throw new AmqpException("No se pudo confirmar la publicación del evento de categoría",
                    exception.getCause());
        } catch (TimeoutException exception) {
            throw new AmqpException("Expiró la espera de confirmación de RabbitMQ", exception);
        }
    }
}
