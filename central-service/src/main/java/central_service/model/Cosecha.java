package central_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Cosecha {

    @Id
    private String id;

    private String agricultorId;
    private String producto;
    private Double toneladas;
    private String estado;
    private LocalDateTime fechaRegistro;

    // ✅ Nuevo campo agregado
    private String ubicacion;

    public Cosecha() {
        this.id = UUID.randomUUID().toString();
        this.estado = "REGISTRADA";
        this.fechaRegistro = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getAgricultorId() {
        return agricultorId;
    }

    public void setAgricultorId(String agricultorId) {
        this.agricultorId = agricultorId;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Double getToneladas() {
        return toneladas;
    }

    public void setToneladas(Double toneladas) {
        this.toneladas = toneladas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    // ✅ Métodos getter y setter para ubicación
    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
