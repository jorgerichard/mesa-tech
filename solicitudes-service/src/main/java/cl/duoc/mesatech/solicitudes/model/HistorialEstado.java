package cl.duoc.mesatech.solicitudes.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "historial_estados")
public class HistorialEstado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JsonIgnore
    private Solicitud solicitud;
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estadoAnterior;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private EstadoSolicitud estadoNuevo;
    @Column(nullable = false)
    private String usuario;
    private String comentario;
    @Column(nullable = false)
    private Instant fecha;

    protected HistorialEstado() {}
    public HistorialEstado(Solicitud solicitud, EstadoSolicitud anterior, EstadoSolicitud nuevo, String usuario, String comentario) {
        this.solicitud = solicitud; this.estadoAnterior = anterior; this.estadoNuevo = nuevo;
        this.usuario = usuario; this.comentario = comentario; this.fecha = Instant.now();
    }
    public Long getId() { return id; }
    public EstadoSolicitud getEstadoAnterior() { return estadoAnterior; }
    public EstadoSolicitud getEstadoNuevo() { return estadoNuevo; }
    public String getUsuario() { return usuario; }
    public String getComentario() { return comentario; }
    public Instant getFecha() { return fecha; }
}
