package org.dacss.projectinitai.services;

import org.dacss.projectinitai.annotations.Bridge;
import org.dacss.projectinitai.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * <h1>{@link ModelsService}</h1>
 * Service class to handle model processing actions.
 */
@Service
@Bridge("models-service")
public class ModelsService {

    private static final Logger logger = LoggerFactory.getLogger(ModelsService.class);

    /**
     * Default constructor for ModelsService.
     */
    public ModelsService() {}

    /**
     * <h3>{@link ModelsIface#processModel(ModelsActions)}</h3>
     * Processes the model based on the given action and model handler.
     *
     * @param action the action to be performed on the model
     * @param modelHandler the handler to process the model
     * @param <T> the type of the model
     * @return a Flux containing the result of the model processing
     * @throws IOException if an I/O error occurs
     */
    public <T> Flux<T> processModel(ModelsActions action, ModelsIface<T> modelHandler) throws IOException {
        return modelHandler.processModel(action)
            .onErrorResume(modelsServiceExc -> {
                // Log the error and return an empty Flux
                logger.error("Error processing model: {}", modelsServiceExc.getMessage());
                return Flux.empty();
            })
            .doFinally(signal -> logger.info("ModelsService processModel"));
    }
}