package cl.duoc.mesatech.solicitudes.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import cl.duoc.mesatech.solicitudes.model.Categoria;
import cl.duoc.mesatech.solicitudes.repository.CategoriaRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:solicitudes;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.rabbitmq.host=localhost",
        "spring.rabbitmq.port=5672",
        "spring.rabbitmq.virtual-host=mesatech",
        "spring.rabbitmq.username=solicitudes_consumidor",
        "spring.rabbitmq.password=test-password",
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "app.rabbitmq.queue=solicitudes.catalogo.queue"
})
class CategoriaConsumerIntegrationTest {
    private static final Instant FECHA_EVENTO = Instant.parse("2026-11-05T13:47:00.973781223Z");

    @Autowired CategoriaConsumer consumer;
    @Autowired CategoriaRepository repository;
    @Autowired Jackson2JsonMessageConverter converter;
    @Autowired ApplicationContext applicationContext;

    @BeforeEach
    void limpiarCategorias() {
        repository.deleteAll();
    }

    @Test
    void consumeYGuardaCategoriaCompleta() {
        consumer.consumir(evento("evento-1", 7L, "Categoría RabbitMQ exitosa"));

        Categoria guardada = repository.findById(7L).orElseThrow();
        assertEquals("evento-1", guardada.getEventId());
        assertEquals("CATEGORIA_CREADA", guardada.getTipoEvento());
        assertEquals(FECHA_EVENTO.truncatedTo(ChronoUnit.MICROS), guardada.getFechaEvento());
        assertEquals("Categoría RabbitMQ exitosa", guardada.getNombre());
    }

    @Test
    void conservaElCategoriaIdOriginalDeCatalogo() {
        consumer.consumir(evento("evento-2", 73L, "Infraestructura"));

        assertTrue(repository.existsById(73L));
        assertFalse(repository.existsById(1L));
    }

    @Test
    void eventIdReprocesadoNoInsertaOtraCategoria() {
        consumer.consumir(evento("evento-3", 8L, "Primera"));
        consumer.consumir(evento("evento-3", 9L, "Segunda"));

        assertEquals(1, repository.count());
        assertTrue(repository.existsById(8L));
        assertFalse(repository.existsById(9L));
    }

    @Test
    void eventoDuplicadoSeCompletaSinNuevaInsercion() {
        CategoriaCreadaEvent event = evento("evento-4", 10L, "Duplicada");

        consumer.consumir(event);
        consumer.consumir(event);

        assertEquals(1, repository.count());
    }

    @Test
    void deserializaJsonConInstantYTypeIdDelPaqueteDeCatalogo() {
        String json = """
                {
                  "eventId": "f21103fb-f9ff-48c6-b359-1ff4fbdf97ef",
                  "tipoEvento": "CATEGORIA_CREADA",
                  "fechaEvento": "2026-11-05T13:47:00.973781223Z",
                  "categoriaId": 7,
                  "nombre": "Categoría RabbitMQ exitosa"
                }
                """;
        MessageProperties properties = new MessageProperties();
        properties.setHeader("__TypeId__", "cl.duoc.mesatech.catalogo.messaging.CategoriaCreadaEvent");
        Message message = new Message(json.getBytes(StandardCharsets.UTF_8), properties);

        Object converted = converter.fromMessage(message);

        CategoriaCreadaEvent event = assertInstanceOf(CategoriaCreadaEvent.class, converted);
        assertEquals(FECHA_EVENTO, event.fechaEvento());
        assertEquals(7L, event.categoriaId());
    }

    @Test
    void noDeclaraExchangeQueueNiBinding() {
        assertTrue(applicationContext.getBeansOfType(Declarable.class).isEmpty());
        assertTrue(applicationContext.getBeansOfType(Exchange.class).isEmpty());
        assertTrue(applicationContext.getBeansOfType(Queue.class).isEmpty());
        assertTrue(applicationContext.getBeansOfType(Binding.class).isEmpty());
    }

    private CategoriaCreadaEvent evento(String eventId, Long categoriaId, String nombre) {
        return new CategoriaCreadaEvent(eventId, "CATEGORIA_CREADA", FECHA_EVENTO, categoriaId, nombre);
    }
}
