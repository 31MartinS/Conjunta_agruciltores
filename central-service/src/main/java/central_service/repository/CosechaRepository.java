package central_service.repository;

import central_service.model.Cosecha;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CosechaRepository extends JpaRepository<Cosecha, String> {
}
