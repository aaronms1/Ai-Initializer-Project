package org.dacss.projectinitai.messages;

import org.dacss.projectinitai.clients.UniversalLLMClientIface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * <h1>{@link LlmClientConfig}</h1>
 * Configuration class for the LLM client so spring can autowire it.
 */
@Configuration
@ComponentScan(basePackages = "org.dacss.projectinitai.clients")
public class LlmClientConfig {
    @Bean
    public UniversalLLMClientIface llmClient() {
        return message ->/*fixme:->*/null;
    }
}
