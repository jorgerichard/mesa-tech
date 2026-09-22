package cl.duoc.mesatech.solicitudes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import cl.duoc.mesatech.solicitudes.dto.CambiarEstadoRequest;
import cl.duoc.mesatech.solicitudes.model.EstadoSolicitud;
import cl.duoc.mesatech.solicitudes.model.Solicitud;
import cl.duoc.mesatech.solicitudes.repository.SolicitudRepository;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {
    @Mock SolicitudRepository repository;
    SolicitudService service;

    @BeforeEach void setUp() { service = new SolicitudService(repository); }

    @Test
    void impideResolverSiNoEstaEnProceso() {
        Solicitud solicitud = solicitudEn(EstadoSolicitud.ASIGNADA);
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.cambiarEstado(1L, new CambiarEstadoRequest(EstadoSolicitud.RESUELTA, "Listo"), "Operador"));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        assertEquals(EstadoSolicitud.ASIGNADA, solicitud.getEstado());
    }

    @Test
    void permiteResolverDesdeEnProceso() {
        Solicitud solicitud = solicitudEn(EstadoSolicitud.EN_PROCESO);
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(repository.save(solicitud)).thenReturn(solicitud);

        service.cambiarEstado(1L, new CambiarEstadoRequest(EstadoSolicitud.RESUELTA, "Solución aplicada"), "Operador");

        assertEquals(EstadoSolicitud.RESUELTA, solicitud.getEstado());
        assertEquals(1, solicitud.getHistorial().size());
    }

    private Solicitud solicitudEn(EstadoSolicitud estado) {
        Solicitud s = new Solicitud();
        s.setTitulo("Prueba"); s.setDescripcion("Prueba"); s.setCategoria("SOFTWARE");
        s.setPrioridad("MEDIA"); s.setSolicitanteId("cliente"); s.setSolicitanteNombre("Cliente");
        s.setEstado(estado); return s;
    }
}
