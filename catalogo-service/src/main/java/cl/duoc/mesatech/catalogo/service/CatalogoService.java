package cl.duoc.mesatech.catalogo.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.mesatech.catalogo.dto.ItemCatalogoRequest;
import cl.duoc.mesatech.catalogo.messaging.CategoriaCreadaEvent;
import cl.duoc.mesatech.catalogo.messaging.CategoriaPublisher;
import cl.duoc.mesatech.catalogo.model.ItemCatalogo;
import cl.duoc.mesatech.catalogo.model.TipoCatalogo;
import cl.duoc.mesatech.catalogo.repository.CatalogoRepository;

@Service
public class CatalogoService {
    private static final Logger log = LoggerFactory.getLogger(CatalogoService.class);

    private final CatalogoRepository repository;
    private final CategoriaPublisher categoriaPublisher;

    public CatalogoService(CatalogoRepository repository, CategoriaPublisher categoriaPublisher) {
        this.repository = repository;
        this.categoriaPublisher = categoriaPublisher;
    }

    @Transactional(readOnly = true) public List<ItemCatalogo> listar() { return repository.findByActivoTrueOrderByTipoAscNombreAsc(); }

    public ItemCatalogo crear(ItemCatalogoRequest r) {
        ItemCatalogo item = repository.save(new ItemCatalogo(r.codigo(), r.nombre(), r.tipo(), r.activo()));
        if (item.getTipo() == TipoCatalogo.CATEGORIA) {
            CategoriaCreadaEvent event = new CategoriaCreadaEvent(
                    UUID.randomUUID().toString(),
                    "CATEGORIA_CREADA",
                    Instant.now(),
                    item.getId(),
                    item.getNombre());
            try {
                categoriaPublisher.publicar(event);
            } catch (RuntimeException exception) {
                log.error("No se pudo publicar el evento de categoría creada para el item {}", item.getId(), exception);
                throw exception;
            }
        }
        return item;
    }

    @Transactional public ItemCatalogo actualizar(Long id, ItemCatalogoRequest r) {
        ItemCatalogo item = obtener(id); item.setCodigo(r.codigo()); item.setNombre(r.nombre()); item.setTipo(r.tipo()); item.setActivo(r.activo());
        return repository.save(item);
    }
    @Transactional public void eliminar(Long id) { repository.delete(obtener(id)); }
    private ItemCatalogo obtener(Long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Elemento no encontrado")); }
}
