package Modelo;

import java.time.LocalDateTime;
import java.util.Date;

public class OrdenModelo {
    private Integer id;
    private Integer usuarioId;
    private Integer estadoId;
    private Date fechaCreacion;

    // Constructor vacío
    public OrdenModelo() {}

    // Constructor completo sin id, ya que suele autogenerarse en la base de datos
    public OrdenModelo(Integer usuarioId, Integer estadoId, Date fechaCreacion) {
        this.usuarioId = usuarioId;
        this.estadoId = estadoId;
        this.fechaCreacion = fechaCreacion; // Asigna la fecha actual
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

