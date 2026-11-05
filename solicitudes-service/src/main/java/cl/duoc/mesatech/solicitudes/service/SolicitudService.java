package cl.duoc.mesatech.solicitudes.service;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.mesatech.solicitudes.dto.*;
import cl.duoc.mesatech.solicitudes.model.*;
import cl.duoc.mesatech.solicitudes.repository.SolicitudRepository;

@Service
public class SolicitudService {
    private static final Map<EstadoSolicitud, Set<EstadoSolicitud>> TRANSICIONES = Map.of(
            EstadoSolicitud.CREADA, EnumSet.of(EstadoSolicitud.ASIGNADA, EstadoSolicitud.CANCELADA),
            EstadoSolicitud.ASIGNADA, EnumSet.of(EstadoSolicitud.EN_PROCESO, EstadoSolicitud.CANCELADA),
            EstadoSolicitud.EN_PROCESO, EnumSet.of(EstadoSolicitud.RESUELTA, EstadoSolicitud.CANCELADA),
            EstadoSolicitud.RESUELTA, EnumSet.of(EstadoSolicitud.CERRADA, EstadoSolicitud.EN_PROCESO),
            EstadoSolicitud.CERRADA, EnumSet.noneOf(EstadoSolicitud.class),
            EstadoSolicitud.CANCELADA, EnumSet.noneOf(EstadoSolicitud.class));

    private final SolicitudRepository repository;
    public SolicitudService(SolicitudRepository repository) { this.repository = repository; }

    @Transactional
    public Solicitud crear(CrearSolicitudRequest request, String userId, String userName) {
        return guardar(request, userId, userName, null);
    }

    private Solicitud guardar(CrearSolicitudRequest request, String userId, String userName, String eventId) {
        Solicitud s = new Solicitud();
        s.setEventId(eventId);
        s.setTitulo(request.titulo()); s.setDescripcion(request.descripcion());
        s.setCategoria(request.categoria()); s.setPrioridad(request.prioridad());
        s.setSolicitanteId(userId); s.setSolicitanteNombre(userName); s.setEstado(EstadoSolicitud.CREADA);
        s.registrarCambio(null, EstadoSolicitud.CREADA, userName, "Solicitud creada");
        return repository.save(s);
    }

    @Transactional(readOnly = true)
    public List<SolicitudV1Response> mias(String userId) {
        return repository.findBySolicitanteIdOrderByFechaCreacionDesc(userId).stream().map(SolicitudV1Response::from).toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitudV1Response> todas() {
        return repository.findAll().stream().map(SolicitudV1Response::from).toList();
    }

    @Transactional(readOnly = true)
    public SolicitudV1Response detalleV1(Long id, String userId, boolean puedeVerTodas) {
        return SolicitudV1Response.from(autorizada(id, userId, puedeVerTodas));
    }

    @Transactional(readOnly = true)
    public SolicitudV2Response detalleV2(Long id, String userId, boolean puedeVerTodas) {
        return SolicitudV2Response.from(autorizada(id, userId, puedeVerTodas));
    }

    @Transactional
    public SolicitudV2Response asignar(Long id, String operadorId, String operadorNombre) {
        Solicitud s = obtener(id);
        if (s.getEstado() != EstadoSolicitud.CREADA) throw conflicto("Solo una solicitud CREADA puede asignarse");
        s.setOperadorId(operadorId); s.setOperadorNombre(operadorNombre);
        cambiar(s, EstadoSolicitud.ASIGNADA, operadorNombre, "Solicitud asignada");
        return SolicitudV2Response.from(s);
    }

    @Transactional
    public SolicitudV2Response cambiarEstado(Long id, CambiarEstadoRequest request, String usuario) {
        Solicitud s = obtener(id);
        if (!TRANSICIONES.getOrDefault(s.getEstado(), Set.of()).contains(request.estado())) {
            throw conflicto("Transición no permitida: " + s.getEstado() + " -> " + request.estado());
        }
        cambiar(s, request.estado(), usuario, request.comentario());
        return SolicitudV2Response.from(s);
    }

    private void cambiar(Solicitud s, EstadoSolicitud nuevo, String usuario, String comentario) {
        EstadoSolicitud anterior = s.getEstado();
        s.setEstado(nuevo); s.registrarCambio(anterior, nuevo, usuario, comentario);
        repository.save(s);
    }

    private Solicitud autorizada(Long id, String userId, boolean puedeVerTodas) {
        Solicitud s = obtener(id);
        if (!puedeVerTodas && !s.getSolicitanteId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede consultar solicitudes de otro usuario");
        }
        return s;
    }
    private Solicitud obtener(Long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada")); }
    private ResponseStatusException conflicto(String mensaje) { return new ResponseStatusException(HttpStatus.CONFLICT, mensaje); }
}
