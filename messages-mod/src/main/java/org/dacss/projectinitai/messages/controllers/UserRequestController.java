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
 * <h1>{@link UserRequestController}</h1>
 * A Universal controller for handling user requests sent to the LLM (Large Language Model).
 */
@Controller
public class UserRequestController {

    private static Mono<UniversalLLMClientIface> llmClient;
    private static Sinks.Many<Object> userRequestSink = Sinks.many().unicast().onBackpressureBuffer();

    /**
     * <h3>{@link #UserRequestController(LLMClientFactory, WebClient.Builder)}</h3>
     *
     * @param llmClientFactory Factory to create LLM clients.
     * @param webClientBuilder Builder for WebClient instances.
     */
    @Autowired
    public UserRequestController(LLMClientFactory llmClientFactory, WebClient.Builder webClientBuilder) {
        // FIXME: clientType should not be hardcoded
        UserRequestController.llmClient = llmClientFactory.createClient("huggingface", webClientBuilder);
    }

    /**
     * <h3>{@link #sendUserRequestToLLM(Flux)}</h3>
     * Handles user requests sent to the LLM.
     *
     * @param message Flux stream of user messages.
     * @return Flux stream of responses from the LLM.
     */
    @MessageMapping("user.request")
    public static Flux<Object> sendUserRequestToLLM(Flux<Object> message) {
        return message
                .doOnNext(userRequestSink::tryEmitNext)
                .thenMany(userRequestSink.asFlux())
                .flatMap(msg -> llmClient.flatMapMany(client -> client.handleClient(Flux.just(msg))))
                .onErrorResume(userRequestExc -> {
                    if (userRequestExc instanceof ModelException || userRequestExc instanceof TranslateException) {
                        return Flux.just("An error occurred while processing the user request with the model.");
                    }
                    return Flux.error(userRequestExc);
                });
    }

    /**
     * <h3>{@link #getRequestStream()}</h3>
     *
     * @return Flux stream of user requests.
     */
    public Flux<Object> getRequestStream() {
        return userRequestSink.asFlux();
    }
}
