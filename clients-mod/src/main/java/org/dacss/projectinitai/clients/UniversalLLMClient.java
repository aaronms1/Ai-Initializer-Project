package org.dacss.projectinitai.clients;

import org.dacss.projectinitai.models.ModelSettings;
import org.dacss.projectinitai.prompts.PromptFactory;
import reactor.core.publisher.Flux;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * <h1>{@link UniversalLLMClient}</h1>
 * Implementation of {@link UniversalLLMClientIface} for handling client requests.
 */
public class UniversalLLMClient implements UniversalLLMClientIface {

    private final WebClient webClient;
    private final ModelSettings modelSettings;
    private final String uri;
    private final PromptFactory promptFactory;

    /**
     * <h3>{@link #UniversalLLMClient(WebClient, ModelSettings, String, PromptFactory)}</h3>
     *
     * @param webClient The {@link WebClient} instance for making HTTP requests.
     * @param modelSettings The settings for the model.
     * @param uri The URI for the model endpoint.
     * @param promptFactory The factory for creating prompts.
     */
    public UniversalLLMClient(WebClient webClient, ModelSettings modelSettings, String uri, PromptFactory promptFactory) {
        this.webClient = webClient;
        this.modelSettings = modelSettings;
        this.uri = uri;
        this.promptFactory = promptFactory;
    }

    /**
     * <h3>{@link UniversalLLMClientIface#handleClient(Flux)}</h3>
     * Handles client requests by sending them to the LLM and returning the responses.
     *
     * @param messages A {@link Flux} stream of messages to be processed.
     * @return A {@link Flux} stream of responses from the LLM.
     */
    @Override
    public Flux<Object> handleClient(Flux<Object> messages) {
        return messages.flatMap(message ->
            promptFactory.createPrompt(message.toString(), 150)
                .flatMapMany(prompt -> webClient.post()
                        .uri(uri)
                        .header("Authorization", "Bearer " + modelSettings.getApiKey())
                        .bodyValue(prompt.toString())
                        .retrieve()
                        .bodyToFlux(Object.class))
        );
    }
}