package org.dacss.projectinitai.models;

import reactor.core.publisher.Flux;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * <h1>{@link ListLocalModels}</h1>
 * Class to list all the models available locally in the '/home/${USER}/.project-ai-initializer/models/' directory.
 */
public class ListLocalModels implements ModelsIface<String> {

    private static final String MODELS_DIR = System.getProperty("user.home") + "/.project-ai-initializer/models/";

    /**
     * Default 0-arg constructor.
     */
    ListLocalModels() {}

    /**
     * <h3>{@link ModelsIface#processModel(ModelsActions)}</h3>
     * Processes the model based on the given action.
     *
     * @param action the action to be performed on the model
     * @return a Flux containing the list of model names if the action is LIST, otherwise an empty Flux
     */
    @Override
    public Flux<String> processModel(ModelsActions action) {
        if (action == ModelsActions.LIST) {
            return Flux.defer(() -> {
                File directory = new File(MODELS_DIR);
                if (directory.exists() && directory.isDirectory()) {
                    String[] files = directory.list();
                    if (files != null) {
                        List<String> fileList = Arrays.asList(files);
                        return Flux.fromIterable(fileList);
                    }
                }
                return Flux.empty();
            }).onErrorResume(listLocalModelsExc -> {
                // Log the error and return an empty Flux
                System.err.println("Error listing models: " + listLocalModelsExc.getMessage());
                return Flux.empty();
            });
        }
        return Flux.empty();
    }
}