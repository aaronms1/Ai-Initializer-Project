package org.dacss.projectinitai.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;

/**
 * <h1>{@link ModelSettingsFactory}</h1>
 * Factory class for creating and processing model settings.
 * Implements the ModelsIface interface for ModelSettings.
 */
public class ModelSettingsFactory implements ModelsIface<ModelSettings> {

    // Base directory for model configurations
    private static final String MODELS_DIR = System.getProperty("user.home") + "/.project-ai-initializer/models/";

    // Name of the model directory
    private final String modelName;

    /**
     * Constructor to initialize ModelSettingsFactory with a specific model name.
     *
     * @param modelName The name of the model directory.
     */
    public ModelSettingsFactory(String modelName) {
        this.modelName = modelName;
    }

    /**
     * <h3>{@link ModelsIface#processModel(ModelsActions)}</h3>
     * Processes the model based on the specified action.
     * If the action is SETTINGS, it reads the configuration from the model's config.json file.
     *
     * @param action The action to be performed (e.g., SETTINGS).
     * @return A Flux emitting the ModelSettings.
     */
    @Override
    public Flux<ModelSettings> processModel(ModelsActions action) {
        if (action == ModelsActions.SETTINGS) {
            return Flux.defer(() -> {
                try {
                    // Construct the path to the config.json file
                    File configFile = new File(MODELS_DIR + modelName + "/config.json");
                    ObjectMapper objectMapper = new ObjectMapper();
                    // Read the settings from the config.json file
                    ModelSettings settings = objectMapper.readValue(configFile, ModelSettings.class);
                    return Flux.just(settings);
                } catch (IOException modelSettingsFactoryExc) {
                    // Log the error and return an empty Flux
                    System.err.println("Error reading model settings: " + modelSettingsFactoryExc.getMessage());
                    return Flux.empty();
                }
            }).onErrorResume(modelSettingsFactoryResumeExc -> {
                // Log the error and return an empty Flux
                System.err.println("Error processing model settings: " + modelSettingsFactoryResumeExc.getMessage());
                return Flux.empty();
            });
        }
        return Flux.empty();
    }
}