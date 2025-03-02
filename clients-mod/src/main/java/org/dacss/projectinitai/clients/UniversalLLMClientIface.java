package org.dacss.projectinitai.clients;

import reactor.core.publisher.Flux;

/**
 * <h1>{@link UniversalLLMClientIface}</h1>
 * Functional interface for handling client requests.
 */
@FunctionalInterface
public interface UniversalLLMClientIface {

    /**
     * <h3>{@link #handleClient(Flux)}</h3>
     * Handles client requests by processing the given messages.
     *
     * @param messages A {@link Flux} stream of messages to be processed.
     * @return A {@link Flux} stream of responses.
     */
    Flux<Object> handleClient(Flux<Object> messages);
}