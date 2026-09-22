package cl.duoc.mesatech.catalogo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "items_catalogo", uniqueConstraints = @UniqueConstraint(columnNames = {"codigo", "tipo"}))
public class ItemCatalogo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 60)
    private String codigo;
    @Column(nullable = false, length = 120)
    private String nombre;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TipoCatalogo tipo;
    @Column(nullable = false)
    private boolean activo;

    protected ItemCatalogo() {}
    public ItemCatalogo(String codigo, String nombre, TipoCatalogo tipo, boolean activo) {
        this.codigo = codigo; this.nombre = nombre; this.tipo = tipo; this.activo = activo;
    }
    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoCatalogo getTipo() { return tipo; }
    public void setTipo(TipoCatalogo tipo) { this.tipo = tipo; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
