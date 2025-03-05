package org.dacss.projectinitai.models;

import reactor.core.publisher.Flux;

import java.io.IOException;

@FunctionalInterface
public interface ModelsIface {
    Flux<Object> processModel(ModelsActions action) throws IOException;
}
