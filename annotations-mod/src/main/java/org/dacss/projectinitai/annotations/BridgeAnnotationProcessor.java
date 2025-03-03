package org.dacss.projectinitai.annotations;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.SourceVersion;
import java.util.Set;
import java.util.HashSet;

/**
 * <h1>{@link BridgeAnnotationProcessor}</h1> class is an annotation processor that processes
 * elements annotated with the @Bridge annotation. It registers the names of the
 * services annotated with @Bridge.
 */
@SupportedAnnotationTypes("org.dacss.projectinitai.annotations.Bridge")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class BridgeAnnotationProcessor extends AbstractProcessor {

    private static final Set<String> registeredServices = new HashSet<>();

    /**
     * <h3>{@link #process(Set, RoundEnvironment)}</h3>
     * Processes the elements annotated with the @Bridge annotation.
     *
     * @param annotations the set of annotations to be processed
     * @param roundEnv the environment for information about the current and prior round
     * @return true if the annotations are claimed by this processor, false otherwise
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Bridge.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                Bridge bridge = typeElement.getAnnotation(Bridge.class);
                String serviceName = bridge.value();
                registeredServices.add(serviceName);
            }
        }
        return true;
    }

    /**
     * <h3>{@link #getRegisteredServices()}</h3>
     * Gets the set of all registered services.
     *
     * @return a set of all registered service names
     */
    public static Set<String> getRegisteredServices() {
        return registeredServices;
    }
}