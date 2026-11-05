package cl.duoc.mesatech.catalogo.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import cl.duoc.mesatech.catalogo.messaging.CategoriaCreadaEvent;

class RabbitMqConfigTest {
    @Test
    void noRegistraRecursosQueRabbitAdminPuedaDeclarar() {
        new ApplicationContextRunner()
                .withUserConfiguration(RabbitMqConfig.class)
                .withPropertyValues("RABBITMQ_USER=catalogo_publicador", "RABBITMQ_PASSWORD=test-password")
                .run(context -> {
                    assertThat(context.getBeansOfType(Declarable.class)).isEmpty();
                    assertThat(context.getBeansOfType(Queue.class)).isEmpty();
                    assertThat(context.getBeansOfType(Exchange.class)).isEmpty();
                    assertThat(context.getBeansOfType(Binding.class)).isEmpty();
                    assertThat(context.getBean("categoriaConnectionFactory")).isInstanceOf(CachingConnectionFactory.class);
                    assertThat(context.getBean("categoriaRabbitTemplate")).isInstanceOf(RabbitTemplate.class);
                });
    }

    @Test
    void configuraUnConvertidorQueSerializaEventosComoJson() throws Exception {
        CategoriaCreadaEvent event = new CategoriaCreadaEvent(
                UUID.randomUUID().toString(), "CATEGORIA_CREADA", Instant.parse("2026-11-03T23:00:00Z"),
                100L, "Soporte de software");

        Message message = new RabbitMqConfig().rabbitMessageConverter()
                .toMessage(event, new MessageProperties());
        String json = new String(message.getBody(), StandardCharsets.UTF_8);

        assertThat(message.getMessageProperties().getContentType()).contains("json");
        assertThat(json).contains("\"tipoEvento\":\"CATEGORIA_CREADA\"")
                .contains("\"categoriaId\":100")
                .contains("\"nombre\":\"Soporte de software\"")
                .contains("\"fechaEvento\":\"2026-11-03T23:00:00Z\"");
    }
}
