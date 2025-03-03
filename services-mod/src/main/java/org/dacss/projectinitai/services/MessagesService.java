package org.dacss.projectinitai.services;

import org.dacss.projectinitai.annotations.Bridge;
import org.dacss.projectinitai.messages.*;
import org.dacss.projectinitai.messages.controllers.AiResponseController;
import org.dacss.projectinitai.messages.controllers.UserRequestController;
import org.dacss.projectinitai.messages.functions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * <h1>{@link MessagesService}</h1>
 * Backend service for processing messages.
 */
@Service
@Bridge("messages-service")
public class MessagesService implements MessagesIface {

    private static final Logger log = LoggerFactory.getLogger(MessagesService.class);

    public MessagesService() {}

    @Override
    public Flux<Object> processMessages(MessageAction action) {
        Flux<Object> flux;
        try {
            flux = switch (action) {
                case REQUEST -> UserRequestController.sendUserRequestToLLM(Flux.just(new Object()));
                case RESPONSE -> AiResponseController.receiveAiResponseFromLLM(Flux.just(new Object()));
                case THUMBS_UP -> ThumbsUp.processThumbsUp(Flux.just(new Object()));
                case THUMBS_DOWN -> ThumbsDown.processThumbsDown(Flux.just(new Object()));
                case RETRY -> RetryMessage.retryMessageSet(Flux.just(new Object().toString())); //info-> this is a temp hack for now.
                case TRASH -> TrashMessageSet.destroyMessageSet(Flux.just(new Object()));
                case SESSION_END -> PublishSessionEnd.publishSessionEnd();
            };
        } catch (Exception messagesServiceExc) {
            log.error("{}: Error from MessagesService performing action:", action, messagesServiceExc);
            return Flux.empty();
        } finally {
            log.info("MessagesService action completed: {}", action);
        }
        return flux;
    }
}
