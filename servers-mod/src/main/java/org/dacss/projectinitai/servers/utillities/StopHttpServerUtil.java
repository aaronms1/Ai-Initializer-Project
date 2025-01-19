package org.dacss.projectinitai.servers.utillities;
/**/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * <h1>{@link StopHttpServerUtil}</h1>
 * Utility class for stopping the HTTP server.
 */
public final class StopHttpServerUtil {

    private static final Logger logger = LoggerFactory.getLogger(StopHttpServerUtil.class);
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "30320"));

    /**
     * <h1>{@link StopHttpServerUtil}</h1>
     * 0-arg constructor.
     */
    private StopHttpServerUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * <h1>{@link #stopServer()}</h1>
     * Stops the HTTP server.
     */
    public static void stopServer() {
        try {
            URI uri = new URI("http", null, "localhost", PORT, "/shutdown", null, null);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                logger.info("HTTP server stopped successfully on port {}", PORT);
            } else {
                logger.warn("Failed to stop HTTP server on port {}: {}", PORT, responseCode);
            }
        } catch (IOException | URISyntaxException e) {
            logger.error("Error stopping HTTP server on port {}: {}", PORT, e.getMessage());
        }
    }
}
