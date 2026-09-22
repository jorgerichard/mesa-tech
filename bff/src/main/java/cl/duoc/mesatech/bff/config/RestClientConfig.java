package cl.duoc.mesatech.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean("solicitudesClient")
    RestClient solicitudesClient(@Value("${app.services.solicitudes-url}") String url) {
        return RestClient.builder().baseUrl(url).build();
    }

    @Bean("catalogoClient")
    RestClient catalogoClient(@Value("${app.services.catalogo-url}") String url) {
        return RestClient.builder().baseUrl(url).build();
    }
}
