package central_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Agricultor {

    @Id
    private String id;
    private String nombre;
    private String documento;

    public Agricultor() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() { return id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocumento() { return documento; }

    public void setDocumento(String documento) { this.documento = documento; }
}
