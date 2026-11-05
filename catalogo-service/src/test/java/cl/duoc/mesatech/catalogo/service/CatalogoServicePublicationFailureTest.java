package cl.duoc.mesatech.catalogo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.test.util.ReflectionTestUtils;

import cl.duoc.mesatech.catalogo.dto.ItemCatalogoRequest;
import cl.duoc.mesatech.catalogo.messaging.CategoriaCreadaEvent;
import cl.duoc.mesatech.catalogo.messaging.CategoriaPublisher;
import cl.duoc.mesatech.catalogo.model.ItemCatalogo;
import cl.duoc.mesatech.catalogo.model.TipoCatalogo;
import cl.duoc.mesatech.catalogo.repository.CatalogoRepository;

@ExtendWith(MockitoExtension.class)
class CatalogoServicePublicationFailureTest {
    @Mock
    private CatalogoRepository repository;

    @Mock
    private CategoriaPublisher categoriaPublisher;

    @InjectMocks
    private CatalogoService service;

    @Test
    void noOcultaElErrorDePublicacionDespuesDeGuardar() {
        ItemCatalogo savedItem = new ItemCatalogo(
                "CATEGORIA_ERROR_TEST", "Soporte de software", TipoCatalogo.CATEGORIA, true);
        ReflectionTestUtils.setField(savedItem, "id", 100L);
        when(repository.save(any(ItemCatalogo.class))).thenReturn(savedItem);
        AmqpException publishFailure = new AmqpException("Broker no disponible");
        org.mockito.Mockito.doThrow(publishFailure).when(categoriaPublisher)
                .publicar(any(CategoriaCreadaEvent.class));

        assertThatThrownBy(() -> service.crear(new ItemCatalogoRequest(
                "CATEGORIA_ERROR_TEST", "Soporte de software", TipoCatalogo.CATEGORIA, true)))
                .isSameAs(publishFailure);

        InOrder inOrder = inOrder(repository, categoriaPublisher);
        inOrder.verify(repository).save(any(ItemCatalogo.class));
        inOrder.verify(categoriaPublisher).publicar(any(CategoriaCreadaEvent.class));
        assertThat(savedItem.getId()).isEqualTo(100L);
    }
}
