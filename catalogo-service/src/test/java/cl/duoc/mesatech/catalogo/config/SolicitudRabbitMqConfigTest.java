package cl.duoc.mesatech.catalogo.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import cl.duoc.mesatech.catalogo.controller.SolicitudPublicacionController;
import cl.duoc.mesatech.catalogo.messaging.SolicitudPublisher;

class SolicitudRabbitMqConfigTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(
                    RabbitMqConfig.class,
                    SolicitudRabbitMqConfig.class,
                    SolicitudPublisher.class,
                    SolicitudPublicacionController.class)
            .withPropertyValues(
                    "RABBITMQ_USER=catalogo_publicador",
                    "RABBITMQ_PASSWORD=category-test-password");

    @Test
    void mantieneDeshabilitadoElFlujoLegadoPorDefecto() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(SolicitudPublisher.class);
            assertThat(context).doesNotHaveBean(SolicitudPublicacionController.class);
            assertThat(context).doesNotHaveBean("solicitudRabbitTemplate");
            assertThat(context).hasBean("categoriaRabbitTemplate");
        });
    }

    @Test
    void usaUnaConexionIndependienteCuandoSeHabilitaElFlujoLegado() {
        contextRunner
                .withPropertyValues(
                        "app.rabbitmq.legacy-solicitud.enabled=true",
                        "app.rabbitmq.legacy-solicitud.exchange=mesatech.exchange",
                        "app.rabbitmq.legacy-solicitud.routing-key=solicitud.creada",
                        "SOLICITUD_RABBITMQ_HOST=legacy-rabbit",
                        "SOLICITUD_RABBITMQ_PORT=5673",
                        "SOLICITUD_RABBITMQ_USER=solicitudes_publicador",
                        "SOLICITUD_RABBITMQ_PASSWORD=legacy-test-password",
                        "SOLICITUD_RABBITMQ_VHOST=mesatech")
                .run(context -> {
                    assertThat(context).hasSingleBean(SolicitudPublisher.class);
                    assertThat(context).hasSingleBean(SolicitudPublicacionController.class);
                    assertThat(context.getBeansOfType(Declarable.class)).isEmpty();

                    RabbitTemplate categoriaTemplate = context.getBean("categoriaRabbitTemplate", RabbitTemplate.class);
                    RabbitTemplate solicitudTemplate = context.getBean("solicitudRabbitTemplate", RabbitTemplate.class);
                    assertThat(solicitudTemplate.getConnectionFactory())
                            .isInstanceOf(CachingConnectionFactory.class)
                            .isNotSameAs(categoriaTemplate.getConnectionFactory());

                    CachingConnectionFactory solicitudFactory = context.getBean(
                            "solicitudConnectionFactory", CachingConnectionFactory.class);
                    assertThat(solicitudFactory.getVirtualHost()).isEqualTo("mesatech");
                });
    }
}
