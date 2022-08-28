package a.gleb.reactivearticlesapp.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Only for test Swagger-UI
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {

    /**
     * Register CORS settings, for Swagger.
     *
     * @param registry CORS configuration {@link CorsRegistry}
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("*")
                .maxAge(3600);
    }
}
