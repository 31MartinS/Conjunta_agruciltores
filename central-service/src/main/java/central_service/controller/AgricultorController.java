package central_service.controller;

import central_service.model.Agricultor;
import central_service.repository.AgricultorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agricultores")
public class AgricultorController {

    private final AgricultorRepository agricultorRepository;

    public AgricultorController(AgricultorRepository agricultorRepository) {
        this.agricultorRepository = agricultorRepository;
    }

    @GetMapping
    public List<Agricultor> listar() {
        return agricultorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        return agricultorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Agricultor a) {
        if (a.getNombre() == null || a.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre del agricultor es obligatorio");
        }
        if (a.getDocumento() == null || a.getDocumento().isBlank()) {
            return ResponseEntity.badRequest().body("El documento del agricultor es obligatorio");
        }
        return ResponseEntity.ok(agricultorRepository.save(a));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable String id, @RequestBody Agricultor datos) {
        return agricultorRepository.findById(id)
                .map(a -> {
                    if (datos.getNombre() == null || datos.getNombre().isBlank()) {
                        return ResponseEntity.badRequest().body("El nombre no puede estar vacío");
                    }
                    if (datos.getDocumento() == null || datos.getDocumento().isBlank()) {
                        return ResponseEntity.badRequest().body("El documento no puede estar vacío");
                    }

                    a.setNombre(datos.getNombre());
                    a.setDocumento(datos.getDocumento());
                    return ResponseEntity.ok(agricultorRepository.save(a));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        if (!agricultorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        agricultorRepository.deleteById(id);
        return ResponseEntity.ok("Eliminado");
    }
}
