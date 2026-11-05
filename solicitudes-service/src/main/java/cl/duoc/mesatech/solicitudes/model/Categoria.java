package cl.duoc.mesatech.solicitudes.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "categorias", uniqueConstraints = @UniqueConstraint(
        name = "uk_categorias_event_id", columnNames = "event_id"))
public class Categoria {
    @Id
    @Column(name = "categoria_id", nullable = false, updatable = false)
    private Long categoriaId;

    @Column(name = "event_id", nullable = false, updatable = false, length = 36)
    private String eventId;

    @Column(name = "tipo_evento", nullable = false, updatable = false, length = 80)
    private String tipoEvento;

    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private Instant fechaEvento;

    @Column(nullable = false, updatable = false, length = 120)
    private String nombre;

    protected Categoria() {
    }

    public Categoria(String eventId, String tipoEvento, Instant fechaEvento, Long categoriaId, String nombre) {
        this.eventId = eventId;
        this.tipoEvento = tipoEvento;
        this.fechaEvento = fechaEvento;
        this.categoriaId = categoriaId;
        this.nombre = nombre;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public Instant getFechaEvento() {
        return fechaEvento;
    }

    public String getNombre() {
        return nombre;
    }
}
