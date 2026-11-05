package cl.duoc.mesatech.solicitudes.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import cl.duoc.mesatech.solicitudes.service.CategoriaService;

@Component
public class CategoriaConsumer {
    private final CategoriaService service;

    public CategoriaConsumer(CategoriaService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumir(CategoriaCreadaEvent event) {
        service.procesar(event);
    }
}
