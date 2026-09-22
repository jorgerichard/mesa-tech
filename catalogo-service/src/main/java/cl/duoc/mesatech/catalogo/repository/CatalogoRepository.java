package cl.duoc.mesatech.catalogo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.mesatech.catalogo.model.*;

public interface CatalogoRepository extends JpaRepository<ItemCatalogo, Long> {
    List<ItemCatalogo> findByActivoTrueOrderByTipoAscNombreAsc();
}
