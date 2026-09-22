package cl.duoc.mesatech.catalogo.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.duoc.mesatech.catalogo.dto.ItemCatalogoRequest;
import cl.duoc.mesatech.catalogo.model.ItemCatalogo;
import cl.duoc.mesatech.catalogo.repository.CatalogoRepository;

@Service
public class CatalogoService {
    private final CatalogoRepository repository;
    public CatalogoService(CatalogoRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true) public List<ItemCatalogo> listar() { return repository.findByActivoTrueOrderByTipoAscNombreAsc(); }
    @Transactional public ItemCatalogo crear(ItemCatalogoRequest r) { return repository.save(new ItemCatalogo(r.codigo(), r.nombre(), r.tipo(), r.activo())); }
    @Transactional public ItemCatalogo actualizar(Long id, ItemCatalogoRequest r) {
        ItemCatalogo item = obtener(id); item.setCodigo(r.codigo()); item.setNombre(r.nombre()); item.setTipo(r.tipo()); item.setActivo(r.activo());
        return repository.save(item);
    }
    @Transactional public void eliminar(Long id) { repository.delete(obtener(id)); }
    private ItemCatalogo obtener(Long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Elemento no encontrado")); }
}
