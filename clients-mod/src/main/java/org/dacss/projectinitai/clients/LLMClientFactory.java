package org.dacss.projectinitai.clients;

import org.dacss.projectinitai.models.ModelSettingsFactory;
import org.dacss.projectinitai.prompts.PromptFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.langchain4j.LangChainClient;
import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.translate.TranslateException;

/**
 * <h1>{@link LLMClientFactory}</h1>
 * Factory class for creating instances of {@link UniversalLLMClientIface}.
 */
//@Component
public class LLMClientFactory {

    @Value("${llm.api.key}")
    private String apiKey;

    @Value("${llm.model.type:remote}")
    private String modelType;

    @Value("${llm.local.model.path:}")
    private String localModelPath;

    private final ModelSettingsFactory modelSettingsFactory = new ModelSettingsFactory();
    private final PromptFactory promptFactory = new PromptFactory();

    /**
     * <h3>{@link #createClient(String, WebClient.Builder)}</h3>
     *
     * @param clientType The type of the client (e.g., huggingface, cohere).
     * @param webClientBuilder The builder for creating {@link WebClient} instances.
     * @return A {@link Mono} emitting the created {@link UniversalLLMClientIface} instance.
     */
    public Mono<UniversalLLMClientIface> createClient(String clientType, WebClient.Builder webClientBuilder) {
        String baseUrl;
        String uri;

        if ("local".equalsIgnoreCase(modelType)) {
            baseUrl = "http://localhost:30320/chat";//mapped to our frontend server port '/chat' feature
            uri = localModelPath;
        } else {
            uri = switch (clientType.toLowerCase()) {
                case "huggingface" -> {
                    baseUrl = "https://api-inference.huggingface.co/models";
                    yield "/gpt2";
                }
                case "cohere" -> {
                    baseUrl = "https://api.cohere.ai/v1";
                    yield "/generate";
                }
                case "googlepalm" -> {
                    baseUrl = "https://palm.googleapis.com/v1";
                    yield "/generateText";
                }
                case "nvidianemo" -> {
                    baseUrl = "https://api.nvidia.com/nemo/v1";
                    yield "/generate";
                }
                case "openai" -> {
                    baseUrl = "https://api.openai.com/v1";
                    yield "/completions";
                }
                case "ibmwatson" -> {
                    baseUrl = "https://api.us-south.assistant.watson.cloud.ibm.com/instances";
                    yield "/v1/workspaces";
                }
                case "microsoftazure" -> {
                    baseUrl = "https://api.cognitive.microsoft.com/sts/v1.0";
                    yield "/issuetoken";
                }
                default -> throw new IllegalArgumentException("Unknown client type: " + clientType);
            };
        }

        return modelSettingsFactory.createModelSettings(modelType, localModelPath)
                .map(modelSettings -> {
                    try {
                        LangChainClient langChainClient = new LangChainClient(apiKey);
                        Model model = Model.newInstance(clientType);
                        return new UniversalLLMClient(webClientBuilder.baseUrl(baseUrl).build(), modelSettings, uri, promptFactory, langChainClient, model);
                    } catch (ModelException | TranslateException e) {
                        throw new RuntimeException("Error creating LLM client", e);
                    }
                });
    }
}
