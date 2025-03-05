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
import com.langchain4j.LangChain4j;
import org.springframework.ai.SpringAI;

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
        LangChain4j langChain4j = new LangChain4j();
        SpringAI springAI = new SpringAI();

        Flux<Object> flux;
        try {
            flux = switch (action) {
                case REQUEST -> {
                    Flux<Object> requestFlux = UserRequestController.sendUserRequestToLLM(Flux.just(new Object()));
                    langChain4j.chain(requestFlux);
                    springAI.integrate(requestFlux);
                    yield requestFlux;
                }
                case RESPONSE -> {
                    Flux<Object> responseFlux = AiResponseController.receiveAiResponseFromLLM(Flux.just(new Object()));
                    langChain4j.chain(responseFlux);
                    springAI.integrate(responseFlux);
                    yield responseFlux;
                }
                case THUMBS_UP -> {
                    Flux<Object> thumbsUpFlux = ThumbsUp.processThumbsUp(Flux.just(new Object()));
                    langChain4j.chain(thumbsUpFlux);
                    springAI.integrate(thumbsUpFlux);
                    yield thumbsUpFlux;
                }
                case THUMBS_DOWN -> {
                    Flux<Object> thumbsDownFlux = ThumbsDown.processThumbsDown(Flux.just(new Object()));
                    langChain4j.chain(thumbsDownFlux);
                    springAI.integrate(thumbsDownFlux);
                    yield thumbsDownFlux;
                }
                case RETRY -> {
                    Flux<Object> retryFlux = RetryMessage.retryMessageSet(Flux.just(new Object().toString()));
                    langChain4j.chain(retryFlux);
                    springAI.integrate(retryFlux);
                    yield retryFlux;
                }
                case TRASH -> {
                    Flux<Object> trashFlux = TrashMessageSet.destroyMessageSet(Flux.just(new Object()));
                    langChain4j.chain(trashFlux);
                    springAI.integrate(trashFlux);
                    yield trashFlux;
                }
                case SESSION_END -> {
                    Flux<Object> sessionEndFlux = PublishSessionEnd.publishSessionEnd();
                    langChain4j.chain(sessionEndFlux);
                    springAI.integrate(sessionEndFlux);
                    yield sessionEndFlux;
                }
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
