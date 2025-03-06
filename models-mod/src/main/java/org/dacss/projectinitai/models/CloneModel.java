package org.dacss.projectinitai.models;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * <h1>{@link CloneModel}</h1>
 * Class to clone local models from the base directory '/home/${USER}/.project-ai-initializer/models/'
 */
public class CloneModel implements ModelsIface<String> {

    private static final String MODELS_DIR = System.getProperty("user.home") + "/.project-ai-initializer/models/";
    private final String sourceLLM;

    /**
     * <h3>{@link #CloneModel(String)}</h3>
     * Constructor to initialize the source LLM.
     *
     * @param sourceLLM the name of the source LLM file to be cloned
     */
    public CloneModel(String sourceLLM) {
        this.sourceLLM = sourceLLM;
    }

    /**
     * <h3>{@link ModelsIface#processModel(ModelsActions)}</h3>
     * Processes the model based on the given action.
     *
     * @param action the action to be performed on the model
     * @return a Flux containing the name of the cloned model if the action is CLONE, otherwise an empty Flux
     */
    @Override
    public Flux<String> processModel(ModelsActions action) {
        if (action == ModelsActions.CLONE) {
            return Mono.fromCallable(() -> {
                String modelName = sourceLLM.substring(0, sourceLLM.lastIndexOf('.'));
                String destDirPath = MODELS_DIR + "clones/" + modelName;
                File destDir = new File(destDirPath);

                if (!destDir.exists() && !destDir.mkdirs()) {
                    throw new IOException("Failed to create destination directory: " + destDirPath);
                }

                File sourceFile = new File(MODELS_DIR + sourceLLM);
                File destFile = new File(destDirPath + "/" + sourceLLM);

                if (sourceFile.exists() && sourceFile.isFile()) {
                    try (FileChannel sourceChannel = FileChannel.open(sourceFile.toPath(), StandardOpenOption.READ);
                         FileChannel destChannel = FileChannel.open(destFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

                        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                        return destFile.getAbsolutePath();
                    }
                }
                return null;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorResume(cloneModelExc -> {
                // Log the error and return an empty Mono
                System.err.println("Error cloning model: " + cloneModelExc.getMessage());
                return Mono.empty();
            })
            .flux();
        }
        return Flux.empty();
    }
}