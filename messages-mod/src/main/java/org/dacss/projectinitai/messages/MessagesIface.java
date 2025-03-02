package org.dacss.projectinitai.messages;

import reactor.core.publisher.Flux;

/**
 * <h1>{@link MessagesIface}</h1>
 * <p>
 *     Functional interface for handling messages.
 * </p>
 */
@FunctionalInterface
public interface MessagesIface {

    /**
     * <h3>{@link #processMessages(MessageAction)}</h3>
     * @param action the action to be performed on the message
     * @return a {@link Flux} of objects
     */
    Flux<Object> processMessages(MessageAction action);
}
