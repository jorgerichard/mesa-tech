package cl.duoc.mesatech.solicitudes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.mesatech.solicitudes.messaging.CategoriaCreadaEvent;
import cl.duoc.mesatech.solicitudes.model.Categoria;
import cl.duoc.mesatech.solicitudes.repository.CategoriaRepository;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void procesar(CategoriaCreadaEvent event) {
        if (repository.existsByEventId(event.eventId())) {
            return;
        }
        repository.saveAndFlush(new Categoria(
                event.eventId(),
                event.tipoEvento(),
                event.fechaEvento(),
                event.categoriaId(),
                event.nombre()));
    }
}
