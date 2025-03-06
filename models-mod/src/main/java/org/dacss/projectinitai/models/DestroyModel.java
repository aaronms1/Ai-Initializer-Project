package org.dacss.projectinitai.models;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;

/**
 * <h1>{@link DestroyModel}</h1>
 * Class to delete local models or clones from the base directory '/home/${USER}/.project-ai-initializer/models/'
 */
public class DestroyModel implements ModelsIface<String> {

    private static final String MODELS_DIR = System.getProperty("user.home") + "/.project-ai-initializer/models/";
    private final String modelName;

    /**
     * <h3>{@link #DestroyModel(String)}</h3>
     * Constructor to initialize the model name.
     *
     * @param modelName the name of the model or clone to be deleted
     */
    public DestroyModel(String modelName) {
        this.modelName = modelName;
    }

    /**
     * <h3>{@link ModelsIface#processModel(ModelsActions)}</h3>
     * Processes the model based on the given action.
     *
     * @param action the action to be performed on the model
     * @return a Flux containing the name of the deleted model if the action is DELETE, otherwise an empty Flux
     */
    @Override
    public Flux<String> processModel(ModelsActions action) {
        if (action == ModelsActions.DESTROY) {
            String targetDirPath = MODELS_DIR + "clones/" + modelName;
            File targetDir = new File(targetDirPath);

            if (targetDir.exists() && targetDir.isDirectory()) {
                return Mono.fromCallable(() -> {
                    deleteDirectory(targetDir);
                    return targetDirPath;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(destroyModelExc -> {
                    // Log the error and return an empty Mono
                    System.err.println("Error deleting directory: " + destroyModelExc.getMessage());
                    return Mono.empty();
                })
                .flux();
            }
            return Flux.empty();
        }
        return Flux.empty();
    }

    /**
     * <h3>{@link #deleteDirectory(File)}</h3>
     * Recursively deletes a directory and its contents.
     *
     * @param directory the directory to be deleted
     * @throws IOException if an I/O error occurs
     */
    private void deleteDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        }
        if (!directory.delete()) {
            throw new IOException("Failed to delete directory: " + directory.getAbsolutePath());
        }
    }
}