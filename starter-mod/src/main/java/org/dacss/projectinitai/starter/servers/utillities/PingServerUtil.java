package org.dacss.projectinitai.starter.servers.utillities;

import org.dacss.projectinitai.starter.servers.UnixSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <h1>{@link PingServerUtil}</h1>
 * Utility class for pinging the local servers to check if they are up.
 */
public final class PingServerUtil {

    private static final Logger logger = LoggerFactory.getLogger(PingServerUtil.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "30320"));

    private PingServerUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * <h1>{@link #pingServers}</h1>
     * Pings the servers to check if they are up.
     */
    public static void pingServers() {
        Runnable pingTask = () -> {
            pingHttpServer();
            UnixSocketServer.pingServer();
            pingAdditionalLLMProviders();
        };

        scheduler.scheduleAtFixedRate(pingTask, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * <h1>{@link #pingHttpServer()}</h1>
     * Pings the HTTP server.
     */
    private static void pingHttpServer() {
        try {
            URI uri = new URI("http", null, "localhost", PORT, "/ping", null, null);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                logger.info("Ping... {}", PORT);
            } else {
                logger.warn("Failed to ping HTTP server on port {}: {}", PORT, responseCode);
            }
        } catch (IOException | URISyntaxException pingHttpExc) {
            logger.error("Error pinging HTTP server on port {}: {}", PORT, pingHttpExc.getMessage());
        }
    }

    /**
     * <h1>{@link #pingAdditionalLLMProviders()}</h1>
     * Pings additional LLM providers to check if they are up.
     */
    private static void pingAdditionalLLMProviders() {
        String[] llmProviders = {
                "https://api.openai.com/v1/ping",
                "https://api.us-south.assistant.watson.cloud.ibm.com/v1/ping",
                "https://api.cognitive.microsoft.com/sts/v1.0/ping"
        };

        for (String providerUrl : llmProviders) {
            try {
                URL url = new URL(providerUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    logger.info("Ping... {}", providerUrl);
                } else {
                    logger.warn("Failed to ping LLM provider {}: {}", providerUrl, responseCode);
                }
            } catch (IOException pingLLMProviderExc) {
                logger.error("Error pinging LLM provider {}: {}", providerUrl, pingLLMProviderExc.getMessage());
            }
        }
    }
}
