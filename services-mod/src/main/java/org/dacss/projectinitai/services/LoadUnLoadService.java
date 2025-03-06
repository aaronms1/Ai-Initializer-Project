package org.dacss.projectinitai.services;

import org.dacss.projectinitai.annotations.Bridge;
import org.dacss.projectinitai.loaders.LoadUnLoadLLM;
import org.dacss.projectinitai.loaders.LoadUnLoadActions;
import org.dacss.projectinitai.loaders.LoadersIface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * <h1>{@link LoadUnLoadService}</h1>
 * endpoint service callable by frontend via the {@link Bridge} annotation for loading and unloading models
 * as well as conversations, training, inference, and RAG generation in a non-blocking way to
 * and from the host to the device(GPU).
 */
@Service
@Bridge("load-unload-service")
public class LoadUnLoadService implements LoadersIface {

    private static final Logger log = LoggerFactory.getLogger(LoadUnLoadService.class);
    private final LoadUnLoadLLM loadUnLoadLLM;

    /**
     * <h3>{@link #LoadUnLoadService()}</h3>
     * Default 0-arg constructor.
     */
    public LoadUnLoadService() {
        this.loadUnLoadLLM = new LoadUnLoadLLM();
    }

    /**
     * <h3>{@link #loadUnloadLLM(LoadUnLoadActions, String)}</h3>
     * Load and unload model from the host to the device(GPU).
     *
     * @param action    {@link LoadUnLoadActions} enum for loading and unloading models.
     * @param modelPath {@link String} path to the model.
     * @return {@link Flux} of {@link Object} for loading and unloading models.
     */
    @Override
    public Flux<Object> loadUnloadLLM(LoadUnLoadActions action, String modelPath) {
        Flux<Object> flux;
        try {
            flux = switch (action) {
                case LOAD -> Flux.just((Object) loadUnLoadLLM.loadUnloadLLM(modelPath));
                case UNLOAD -> Flux.just((Object) loadUnLoadLLM.unloadModel(loadUnLoadLLM.getModel()));
            };
        } catch (Exception loadUnLoadServiceExc) {
            log.error("Error loading/unloading model", loadUnLoadServiceExc);
            return Flux.error(loadUnLoadServiceExc);
        } finally {
            log.info("Model {} operation completed", action);
        }
        return flux;
    }
}