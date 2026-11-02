package cl.duoc.mesatech.solicitudes.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import cl.duoc.mesatech.solicitudes.service.SolicitudService;

@Component
public class SolicitudConsumer {
    private final SolicitudService service;

    public SolicitudConsumer(SolicitudService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumir(SolicitudCreadaEvent event) {
        service.crearDesdeEvento(event);
    }
}
