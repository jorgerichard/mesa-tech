package cl.duoc.mesatech.catalogo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import cl.duoc.mesatech.catalogo.model.*;
import cl.duoc.mesatech.catalogo.repository.CatalogoRepository;

@SpringBootApplication
public class CatalogoApplication {
    public static void main(String[] args) { SpringApplication.run(CatalogoApplication.class, args); }

    @Bean
    CommandLineRunner seed(CatalogoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new ItemCatalogo("SOPORTE_SOFTWARE", "Soporte de software", TipoCatalogo.CATEGORIA, true));
                repository.save(new ItemCatalogo("SOPORTE_HARDWARE", "Soporte de hardware", TipoCatalogo.CATEGORIA, true));
                repository.save(new ItemCatalogo("BAJA", "Baja", TipoCatalogo.PRIORIDAD, true));
                repository.save(new ItemCatalogo("MEDIA", "Media", TipoCatalogo.PRIORIDAD, true));
                repository.save(new ItemCatalogo("ALTA", "Alta", TipoCatalogo.PRIORIDAD, true));
            }
        };
    }
}
