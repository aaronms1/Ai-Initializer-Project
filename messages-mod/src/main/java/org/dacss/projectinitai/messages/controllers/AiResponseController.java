package org.dacss.projectinitai.messages.controllers;

import org.dacss.projectinitai.clients.LLMClientFactory;
import org.dacss.projectinitai.clients.UniversalLLMClientIface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import com.langchain4j.LangChainClient;
import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.translate.TranslateException;

/**
 * <h1>{@link AiResponseController}</h1>
 * A Universal controller for handling AI responses from the LLM (Large Language Model).
 */
@Controller
public class AiResponseController {

    private static Mono<UniversalLLMClientIface> llmClient;
    private static Sinks.Many<Object> aiResponseSink = Sinks.many().unicast().onBackpressureBuffer();

    /**
     * <h3>{@link #AiResponseController(LLMClientFactory, WebClient.Builder)}</h3>
     *
     * @param llmClientFactory Factory to create LLM clients.
     * @param webClientBuilder Builder for WebClient instances.
     */
    @Autowired
    public AiResponseController(LLMClientFactory llmClientFactory, WebClient.Builder webClientBuilder) {
        // FIXME: clientType should not be hardcoded
        AiResponseController.llmClient = llmClientFactory.createClient("huggingface", webClientBuilder);
    }

    /**
     * <h3>{@link #receiveAiResponseFromLLM(Flux)}</h3>
     * Handles AI responses received from the LLM.
     *
     * @param message Flux stream of AI messages.
     * @return Flux stream of responses from the LLM.
     */
    @MessageMapping("ai-response")
    public static Flux<Object> receiveAiResponseFromLLM(Flux<Object> message) {
        return message
                .doOnNext(aiResponseSink::tryEmitNext)
                .thenMany(aiResponseSink.asFlux())
                .flatMap(msg -> llmClient.flatMapMany(client -> client.handleClient(Flux.just(msg))))
                .onErrorResume(aiResponseExc -> {
                    if (aiResponseExc instanceof ModelException || aiResponseExc instanceof TranslateException) {
                        return Flux.just("An error occurred while processing the AI response with the model.");
                    }
                    return Flux.error(aiResponseExc);
                });
    }

    /**
     * <h3>{@link #getResponseStream()}</h3>
     *
     * @return Flux stream of AI responses.
     */
    public Flux<Object> getResponseStream() {
        return aiResponseSink.asFlux();
    }
}
