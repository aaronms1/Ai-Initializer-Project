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
 * Backend hilla endpoint service for loading and unloading models.
 */
@Service
@Bridge("load-unload-service")
public class LoadUnLoadService implements LoadersIface {

    private static final Logger log = LoggerFactory.getLogger(LoadUnLoadService.class);
    private final LoadUnLoadLLM loadUnLoadLLM;

    public LoadUnLoadService() {
        this.loadUnLoadLLM = new LoadUnLoadLLM();
    }

    @Override
    public Flux<Object> loadUnloadLLM(LoadUnLoadActions action, String modelPath) {
        Flux<Object> flux;
        try {
            flux = switch (action) {
                case LOAD -> Flux.just((Object) loadUnLoadLLM.loadModelKernel(modelPath));
                case UNLOAD -> Flux.just((Object) loadUnLoadLLM.unloadModelKernel(loadUnLoadLLM.getModel()));
            };
        } catch (Exception e) {
            log.error("Error loading/unloading model", e);
            return Flux.error(e);
        } finally {
            log.info("Model {} operation completed", action);
        }
        return flux;
    }
}