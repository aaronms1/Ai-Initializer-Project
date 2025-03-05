package org.dacss.projectinitai.models;

import reactor.core.publisher.Flux;

public class DestroyModel {
    public static Flux<Object> destroyModel() {
        return Flux.empty();
    }
}
