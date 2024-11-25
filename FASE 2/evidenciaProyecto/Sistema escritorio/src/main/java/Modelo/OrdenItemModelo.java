package Modelo;
import java.util.Date;

public class OrdenItemModelo {
    private Integer id;
    private Integer ordenId;
    private Integer productoId;
    private Integer cantidad;
    private Date fechaCreacion;

    // Constructor vacío
    public OrdenItemModelo() {}

    // Constructor completo
    public OrdenItemModelo(Integer id, Integer ordenId, Integer productoId, Integer cantidad, Date fechaCreacion) {
        this.id = id;
        this.ordenId = ordenId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(Integer ordenId) {
        this.ordenId = ordenId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
