package cl.duoc.mesatech.solicitudes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.mesatech.solicitudes.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByEventId(String eventId);
}
