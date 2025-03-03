package org.dacss.projectinitai.downloaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * <h1>{@link SearchModels}</h1>
 * Search models to download from Hugging Face.
 */
public class SearchModels {

    private static final Logger log = LoggerFactory.getLogger(SearchModels.class);
    private static final String BASE_URL = "https://huggingface.co/api/models?search=";
    private static final WebClient webClient = WebClient.create();

    /**
     * <h3>{@link #SearchModels()}</h3>
     * Default 0-arg constructor.
     */
    public SearchModels() {
    }

    /**
     * <h3>{@link #searchModels(String)}</h3>
     * @param query search query
     * @return Flux<Object> of search results
     */
    public static Flux<Object> searchModels(String query) {
        String searchUrl = BASE_URL + query;

        return webClient.get()
                .uri(searchUrl)
                .retrieve()
                .bodyToFlux(Object.class)
                .doOnNext(model -> log.info("Received model: {}", model))
                .doOnError(e -> log.error("Error occurred while searching models: ", e));
    }
}