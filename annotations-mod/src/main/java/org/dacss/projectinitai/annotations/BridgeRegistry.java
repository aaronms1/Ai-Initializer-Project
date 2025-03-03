package org.dacss.projectinitai.annotations;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1>{@link BridgeRegistry}</h1> class is a Spring component that maintains a registry of services
 * annotated with the @Bridge annotation. It provides methods to retrieve services by
 * their bridge name and to get a set of all registered services.
 */
@Component
public class BridgeRegistry {

    private final Map<String, Object> bridgeMap;

    /**
     * <h3>{@link #BridgeRegistry(ApplicationContext)}</h3>
     * Constructs a BridgeRegistry instance and initializes the bridgeMap with beans
     * annotated with the @Bridge annotation.
     *
     * @param context the Spring application context used to retrieve beans with the @Bridge annotation
     */
    public BridgeRegistry(ApplicationContext context) {
        this.bridgeMap = context.getBeansWithAnnotation(Bridge.class)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getValue().getClass().getAnnotation(Bridge.class).value(),
                        Map.Entry::getValue
                ));
    }

    /**
     * <h3>{@link #getService(String)}</h3>
     * Retrieves a service by its bridge name.
     *
     * @param bridgeName the name of the bridge
     * @return the service object associated with the given bridge name, or null if no such service exists
     */
    public Object getService(String bridgeName) {
        return bridgeMap.get(bridgeName);
    }

    /**
     * <h3>{@link #getRegisteredServices()}</h3>
     * Gets a set of all registered services.
     *
     * @return a set of all registered service names
     */
    public Set<String> getRegisteredServices() {
        return BridgeAnnotationProcessor.getRegisteredServices();
    }
}