package org.dacss.projectinitai.loaders;


import reactor.core.publisher.Flux;

/**
 * <h1>{@link LoadersIface}</h1>
 */

@FunctionalInterface
public interface LoadersIface {

    Flux<Object> loadUnloadLLM(LoadUnLoadActions action, String modelPath);
}
