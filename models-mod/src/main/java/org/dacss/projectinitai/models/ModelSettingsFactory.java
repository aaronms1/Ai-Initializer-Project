package org.dacss.projectinitai.models;

import reactor.core.publisher.Mono;

/**
 * <h1>{@link ModelSettingsFactory}</h1>
 * Factory class for creating instances of {@link ModelSettings}.
 */
public class ModelSettingsFactory {

    /**
     * <h3>{@link #createModelSettings(String, String)}</h3>
     *
     * @param modelType The type of the model (e.g., remote, local).
     * @param localModelPath The local path to the model, if applicable.
     * @return A {@link Mono} emitting the created {@link ModelSettings} instance.
     */
    public Mono<ModelSettings> createModelSettings(String modelType, String localModelPath) {
        return Mono.just(new ModelSettings(modelType, localModelPath));
    }
}