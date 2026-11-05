package cl.duoc.mesatech.solicitudes.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.mesatech.solicitudes.model.Solicitud;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findBySolicitanteIdOrderByFechaCreacionDesc(String solicitanteId);
}
