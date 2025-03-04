package org.dacss.projectinitai.starter.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * <h1>{@link WebFluxConfig}</h1>
 * This class configures the Spring WebFlux application, including CORS settings, static resource handling,
 * and routing for HTML fallback and frontend serving.
 */
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    /**
     * <h3>{@link #addCorsMappings(CorsRegistry)}</h3>
     * Configures CORS mappings for the application.
     *
     * @param registry the CORS registry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:30320", "https://api.openai.com", "https://api.us-south.assistant.watson.cloud.ibm.com", "https://api.cognitive.microsoft.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }

    /**
     * <h3>{@link #staticResourceRouter()}</h3>
     * Configures the router function to serve static resources from the `frontend` directory.
     *
     * @return a RouterFunction for serving static resources
     */
    @Bean
    public RouterFunction<ServerResponse> staticResourceRouter() {
        return RouterFunctions.resources("/**", new ClassPathResource("frontend/"));
    }

    /**
     * <h3>{@link #htmlFallbackRouter()}</h3>
     * Configures the router function to serve the `index.html` file for any unmatched routes that do not start with `/api`.
     *
     * @return a RouterFunction for serving the HTML fallback
     */
    @Bean
    public RouterFunction<ServerResponse> htmlFallbackRouter() {
        return RouterFunctions.route()
                .GET("/{path:^(?!api).*$}", request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                        .bodyValue(new ClassPathResource("starter-mod/src/main/frontend/index.html")))
                .build();
    }
}
