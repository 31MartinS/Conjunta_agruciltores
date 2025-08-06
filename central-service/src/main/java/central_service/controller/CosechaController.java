package central_service.controller;

import central_service.model.Cosecha;
import central_service.model.EstadoRequest;
import central_service.repository.AgricultorRepository;
import central_service.repository.CosechaRepository;
import central_service.service.RabbitMQPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/cosechas")
public class CosechaController {

    private final CosechaRepository cosechaRepository;
    private final AgricultorRepository agricultorRepository;
    private final RabbitMQPublisher publisher;

    public CosechaController(
            CosechaRepository cosechaRepository,
            AgricultorRepository agricultorRepository,
            RabbitMQPublisher publisher
    ) {
        this.cosechaRepository = cosechaRepository;
        this.agricultorRepository = agricultorRepository;
        this.publisher = publisher;
    }

    @PostMapping
    public ResponseEntity<?> registrarCosecha(@RequestBody Cosecha cosecha) {
        // Validar existencia del agricultor
        if (cosecha.getAgricultorId() == null || cosecha.getAgricultorId().isBlank()) {
            return ResponseEntity.badRequest().body("El ID del agricultor es obligatorio");
        }

        // UUID válido (formato)
        if (!cosecha.getAgricultorId().matches("^[a-fA-F0-9\\-]{36}$")) {
            return ResponseEntity.badRequest().body("El ID del agricultor debe ser un UUID válido");
        }

        // Verificar si el agricultor existe
        if (!agricultorRepository.existsById(cosecha.getAgricultorId())) {
            return ResponseEntity.badRequest().body("El agricultor no existe");
        }

        // Validar producto
        if (cosecha.getProducto() == null || cosecha.getProducto().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre del producto es obligatorio");
        }

        // Validar toneladas
        if (cosecha.getToneladas() == null || cosecha.getToneladas() <= 0) {
            return ResponseEntity.badRequest().body("La cantidad de toneladas debe ser mayor a cero");
        }

        // Validar ubicación (opcional, pero si viene debe tener formato básico: "lat°N, long°W")
        if (cosecha.getUbicacion() != null && !cosecha.getUbicacion().matches(".*[NS],.*[EW]$")) {
            return ResponseEntity.badRequest().body("La ubicación GPS no tiene el formato correcto");
        }

        // Crear y guardar cosecha
        Cosecha nueva = new Cosecha();
        nueva.setAgricultorId(cosecha.getAgricultorId());
        nueva.setProducto(cosecha.getProducto());
        nueva.setToneladas(cosecha.getToneladas());
        nueva.setUbicacion(cosecha.getUbicacion());
        cosechaRepository.save(nueva);

        // Publicar evento a RabbitMQ
        Map<String, Object> evento = new HashMap<>();
        evento.put("evento", "nueva_cosecha");
        evento.put("cosecha_id", nueva.getId());
        evento.put("producto", nueva.getProducto());
        evento.put("toneladas", nueva.getToneladas());
        evento.put("timestamp", nueva.getFechaRegistro().toString());

        publisher.publicarEvento(evento);

        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable String id, @RequestBody EstadoRequest request) {
        if (request.getEstado() == null || request.getEstado().isBlank()) {
            return ResponseEntity.badRequest().body("El estado es obligatorio");
        }

        Optional<Cosecha> op = cosechaRepository.findById(id);
        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cosecha c = op.get();
        c.setEstado(request.getEstado());
        cosechaRepository.save(c);

        return ResponseEntity.ok("Estado actualizado");
    }

    @GetMapping
    public List<Cosecha> listarCosechas() {
        return cosechaRepository.findAll();
    }
}
