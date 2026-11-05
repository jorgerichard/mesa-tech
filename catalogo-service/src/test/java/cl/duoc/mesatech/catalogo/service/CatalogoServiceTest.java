package cl.duoc.mesatech.catalogo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import cl.duoc.mesatech.catalogo.dto.ItemCatalogoRequest;
import cl.duoc.mesatech.catalogo.messaging.CategoriaCreadaEvent;
import cl.duoc.mesatech.catalogo.messaging.CategoriaPublisher;
import cl.duoc.mesatech.catalogo.model.TipoCatalogo;
import cl.duoc.mesatech.catalogo.repository.CatalogoRepository;

@DataJpaTest
@Import(CatalogoService.class)
class CatalogoServiceTest {
    @Autowired
    private CatalogoService service;

    @Autowired
    private CatalogoRepository repository;

    @MockitoBean
    private CategoriaPublisher categoriaPublisher;

    @Test
    void crearCategoriaGuardaAntesDePublicarEventoConIdGenerado() {
        doAnswer(invocation -> {
            CategoriaCreadaEvent event = invocation.getArgument(0);
            assertThat(event.categoriaId()).isNotNull();
            assertThat(repository.findById(event.categoriaId())).isPresent();
            return null;
        }).when(categoriaPublisher).publicar(any(CategoriaCreadaEvent.class));

        var item = service.crear(new ItemCatalogoRequest(
                "CATEGORIA_TEST", "Soporte de software", TipoCatalogo.CATEGORIA, true));

        assertThat(repository.findById(item.getId())).contains(item);
        var eventCaptor = org.mockito.ArgumentCaptor.forClass(CategoriaCreadaEvent.class);
        verify(categoriaPublisher).publicar(eventCaptor.capture());
        CategoriaCreadaEvent event = eventCaptor.getValue();
        assertThat(event.categoriaId()).isEqualTo(item.getId());
        assertThat(event.nombre()).isEqualTo("Soporte de software");
        assertThat(event.tipoEvento()).isEqualTo("CATEGORIA_CREADA");
        assertThat(UUID.fromString(event.eventId()).toString()).isEqualTo(event.eventId());
        assertThat(event.fechaEvento()).isNotNull();
    }

    @Test
    void crearPrioridadNoPublicaEventoDeCategoria() {
        service.crear(new ItemCatalogoRequest("PRIORIDAD_TEST", "Alta", TipoCatalogo.PRIORIDAD, true));

        verifyNoInteractions(categoriaPublisher);
    }
}
