package cl.duoc.mesatech.catalogo.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class CategoriaPublisherTest {
    @Mock
    private RabbitTemplate rabbitTemplate;

    private CategoriaPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new CategoriaPublisher(rabbitTemplate, "catalogo.exchange", "categoria.creada",
                Duration.ofSeconds(1));
    }

    @Test
    void publicaEnElExchangeYRutaConfiguradosConMensajePersistente() throws Exception {
        CategoriaCreadaEvent event = new CategoriaCreadaEvent(
                UUID.randomUUID().toString(), "CATEGORIA_CREADA", Instant.now(), 100L, "Soporte de software");
        doAnswer(invocation -> {
            CorrelationData correlationData = invocation.getArgument(4);
            correlationData.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbitTemplate).convertAndSend(
                eq("catalogo.exchange"), eq("categoria.creada"), eq(event),
                any(MessagePostProcessor.class), any(CorrelationData.class));

        publisher.publicar(event);

        ArgumentCaptor<MessagePostProcessor> processorCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(rabbitTemplate).convertAndSend(
                eq("catalogo.exchange"), eq("categoria.creada"), eq(event),
                processorCaptor.capture(), any(CorrelationData.class));
        Message message = processorCaptor.getValue().postProcessMessage(
                new Message("{}".getBytes(java.nio.charset.StandardCharsets.UTF_8), new MessageProperties()));
        assertThat(message.getMessageProperties().getDeliveryMode()).isEqualTo(MessageDeliveryMode.PERSISTENT);
    }

    @Test
    void propagaElRechazoDelBroker() {
        CategoriaCreadaEvent event = new CategoriaCreadaEvent(
                UUID.randomUUID().toString(), "CATEGORIA_CREADA", Instant.now(), 100L, "Soporte de software");
        doAnswer(invocation -> {
            CorrelationData correlationData = invocation.getArgument(4);
            correlationData.getFuture().complete(new CorrelationData.Confirm(false, "sin permisos"));
            return null;
        }).when(rabbitTemplate).convertAndSend(
                eq("catalogo.exchange"), eq("categoria.creada"), eq(event),
                any(MessagePostProcessor.class), any(CorrelationData.class));

        assertThatThrownBy(() -> publisher.publicar(event))
                .isInstanceOf(AmqpException.class)
                .hasMessageContaining("sin permisos");
    }
}
