package cl.duoc.mesatech.solicitudes.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "solicitudes")
public class Solicitud {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, updatable = false, length = 36)
    private String eventId;
    @Column(nullable = false, length = 120)
    private String titulo;
    @Column(nullable = false, length = 2000)
    private String descripcion;
    @Column(nullable = false)
    private String categoria;
    @Column(nullable = false)
    private String prioridad;
    @Column(nullable = false)
    private String solicitanteId;
    @Column(nullable = false)
    private String solicitanteNombre;
    private String operadorId;
    private String operadorNombre;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private EstadoSolicitud estado;
    @Column(nullable = false, updatable = false)
    private Instant fechaCreacion;
    @Column(nullable = false)
    private Instant fechaActualizacion;
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha ASC")
    private List<HistorialEstado> historial = new ArrayList<>();

    @PrePersist void prePersist() {
        fechaCreacion = Instant.now(); fechaActualizacion = fechaCreacion;
        if (estado == null) estado = EstadoSolicitud.CREADA;
    }
    @PreUpdate void preUpdate() { fechaActualizacion = Instant.now(); }

    public void registrarCambio(EstadoSolicitud anterior, EstadoSolicitud nuevo, String usuario, String comentario) {
        historial.add(new HistorialEstado(this, anterior, nuevo, usuario, comentario));
    }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    public String getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(String solicitanteId) { this.solicitanteId = solicitanteId; }
    public String getSolicitanteNombre() { return solicitanteNombre; }
    public void setSolicitanteNombre(String solicitanteNombre) { this.solicitanteNombre = solicitanteNombre; }
    public String getOperadorId() { return operadorId; }
    public void setOperadorId(String operadorId) { this.operadorId = operadorId; }
    public String getOperadorNombre() { return operadorNombre; }
    public void setOperadorNombre(String operadorNombre) { this.operadorNombre = operadorNombre; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public Instant getFechaActualizacion() { return fechaActualizacion; }
    public List<HistorialEstado> getHistorial() { return historial; }
}
