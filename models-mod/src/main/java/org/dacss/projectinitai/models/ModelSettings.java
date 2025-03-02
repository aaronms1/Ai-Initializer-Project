package org.dacss.projectinitai.models;

/**
 * <h1>{@link ModelSettings}</h1>
 * This class represents the settings for a model, including the API key, model type, and local model path.
 */
public class ModelSettings {
    private String apiKey;
    private String modelType;
    private String localModelPath;

    /**
     * <h3>{@link #ModelSettings(String, String, String)}</h3>
     *
     * @param apiKey The API key for the model.
     * @param modelType The type of the model (e.g., remote, local).
     * @param localModelPath The local path to the model, if applicable.
     */
    public ModelSettings(String apiKey, String modelType, String localModelPath) {
        this.apiKey = apiKey;
        this.modelType = modelType;
        this.localModelPath = localModelPath;
    }

    /**
     * Gets the API key for the model.
     *
     * @return The API key.
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets the API key for the model.
     *
     * @param apiKey The new API key.
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Gets the type of the model.
     *
     * @return The model type.
     */
    public String getModelType() {
        return modelType;
    }

    /**
     * Sets the type of the model.
     *
     * @param modelType The new model type.
     */
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    /**
     * Gets the local path to the model.
     *
     * @return The local model path.
     */
    public String getLocalModelPath() {
        return localModelPath;
    }

    /**
     * Sets the local path to the model.
     *
     * @param localModelPath The new local model path.
     */
    public void setLocalModelPath(String localModelPath) {
        this.localModelPath = localModelPath;
    }
}