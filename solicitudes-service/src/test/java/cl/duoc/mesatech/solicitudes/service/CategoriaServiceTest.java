package cl.duoc.mesatech.solicitudes.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.mesatech.solicitudes.messaging.CategoriaConsumer;
import cl.duoc.mesatech.solicitudes.messaging.CategoriaCreadaEvent;
import cl.duoc.mesatech.solicitudes.model.Categoria;
import cl.duoc.mesatech.solicitudes.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {
    @Mock CategoriaRepository repository;
    CategoriaService service;

    @BeforeEach
    void setUp() {
        service = new CategoriaService(repository);
    }

    @Test
    void errorDePersistenciaSePropagaParaNoConfirmarElMensaje() {
        CategoriaCreadaEvent event = new CategoriaCreadaEvent(
                "evento-error", "CATEGORIA_CREADA", Instant.parse("2026-11-05T13:47:00Z"), 7L, "Error");
        RuntimeException persistenceError = new RuntimeException("Error de base de datos");
        when(repository.existsByEventId(event.eventId())).thenReturn(false);
        doThrow(persistenceError).when(repository).saveAndFlush(org.mockito.ArgumentMatchers.any(Categoria.class));

        CategoriaConsumer consumer = new CategoriaConsumer(service);
        assertThrows(RuntimeException.class, () -> consumer.consumir(event));

        verify(repository).saveAndFlush(org.mockito.ArgumentMatchers.any(Categoria.class));
    }
}
