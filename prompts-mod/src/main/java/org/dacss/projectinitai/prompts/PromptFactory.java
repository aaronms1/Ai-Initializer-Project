package org.dacss.projectinitai.prompts;

import reactor.core.publisher.Mono;

/**
 * <h1>{@link PromptFactory}</h1>
 * Factory class for creating instances of {@link Prompt}.
 */
public class PromptFactory {

    /**
     * <h3>{@link #createPrompt(String, int)}</h3>
     *
     * @param input The input text for the prompt.
     * @param maxTokens The maximum number of tokens for the prompt.
     * @return A {@link Mono} emitting the created {@link Prompt} instance.
     */
    public Mono<Prompt> createPrompt(String input, int maxTokens) {
        return Mono.just(new Prompt(input, maxTokens));
    }
}