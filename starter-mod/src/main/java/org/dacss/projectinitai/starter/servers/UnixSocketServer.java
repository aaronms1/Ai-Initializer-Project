package org.dacss.projectinitai.starter.servers;

import org.dacss.projectinitai.messages.controllers.AiResponseController;
import org.dacss.projectinitai.messages.controllers.UserRequestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnixDomainSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <h1>{@link UnixSocketServer}</h1>
 * Server that listens on a Unix Domain Socket.
 * This is the default local LLM server for the application.
 */
public final class UnixSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(UnixSocketServer.class);
    private static volatile boolean running = true;
    private static Path socketPath;
    private static Socket socket;
    private static OutputStream outputStream;
    private static InputStream inputStream;

    /**
     * <h3>{@link UnixSocketServer}</h3>
     *
     * @param llmName The name of the LLM to be used.
     */
    public UnixSocketServer(String llmName) {
        socketPath = Paths.get("/var/run/project-init-ai/" + llmName + ".sock");
    }

    /**
     * <h3>{@link #startServer()}</h3>
     * Starts the Unix Socket server.
     */
    public static Flux<Object> startServer() {
        return Flux.create(sink -> {
            try {
                if (Files.exists(socketPath)) {
                    Files.delete(socketPath);
                }
                try (ServerSocket serverSocket = new ServerSocket()) {
                    serverSocket.bind(UnixDomainSocketAddress.of(socketPath));
                    logger.info("Server started on {}", socketPath);
                    sink.next("Server started on " + socketPath);

                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        running = false;
                        try {
                            serverSocket.close();
                            if (socket != null) {
                                socket.close();
                            }
                        } catch (IOException startServerShutDownHookExc) {
                            logger.error("Error closing the server socket: {}", startServerShutDownHookExc.getMessage());
                            sink.error(startServerShutDownHookExc);
                        }
                    }));

                    while (running) {
                        socket = serverSocket.accept();
                        outputStream = socket.getOutputStream();
                        inputStream = socket.getInputStream();
                        logger.info("Client connected: {}", socket.getRemoteSocketAddress());
                        sink.next("Client connected: " + socket.getRemoteSocketAddress());
                    }
                }
            } catch (IOException startServerExc) {
                logger.error("Error starting the server: {}", startServerExc.getMessage());
                sink.error(startServerExc);
            }
            sink.complete();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * <h3>{@link #pingServer()}</h3>
     * Pings the Unix Socket server to check if it is up.
     */
    public static Flux<Object> pingServer() {
        return Flux.create(sink -> {
            try (Socket socket = new Socket()) {
                socket.connect(UnixDomainSocketAddress.of(socketPath));
                logger.info("Unix Socket server is up and running on {}", socketPath);
                sink.next("Unix Socket server is up and running on " + socketPath);
            } catch (IOException pingServerExc) {
                logger.warn("Failed to ping Unix Socket server on {}: {}", socketPath, pingServerExc.getMessage());
                sink.error(pingServerExc);
            }
            sink.complete();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * <h3>{@link #stopServer()}</h3>
     * Stops the Unix Socket server.
     */
    public static Flux<Object> stopServer() {
        return Flux.create(sink -> {
            try {
                if (Files.exists(socketPath)) {
                    Files.delete(socketPath);
                    logger.info("Server stopped and socket file deleted: {}", socketPath);
                    sink.next("Server stopped and socket file deleted: " + socketPath);
                } else {
                    logger.warn("Socket file does not exist: {}", socketPath);
                    sink.next("Socket file does not exist: " + socketPath);
                }
            } catch (IOException stopServerExc) {
                logger.error("Error stopping the server: {}", stopServerExc.getMessage());
                sink.error(stopServerExc);
            }
            sink.complete();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * <h3>{@link #sendMessageToLLM(Flux<Object>)}</h3>
     * Sends a message to the LLM via the Unix socket and receives the AI response.
     *
     * @param messageFlux The Flux of messages to send to the LLM.
     * @return A {@link Flux} of {@link Object} that represents the AI response.
     */
    public static Flux<Object> sendMessageToLLM(Flux<Object> messageFlux) {
        return messageFlux.flatMap(message -> Flux.create(sink -> {
            try {
                if (socket == null || socket.isClosed()) {
                    socket = new Socket();
                    socket.connect(UnixDomainSocketAddress.of(socketPath));
                    outputStream = socket.getOutputStream();
                    inputStream = socket.getInputStream();
                }

                if (message instanceof byte[]) {
                    outputStream.write((byte[]) message);
                } else {
                    outputStream.write(message.toString().getBytes());
                }
                outputStream.flush();
                logger.info("Message sent to LLM: {}", message);

                // Read AI response
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    Object aiResponse = new String(buffer, 0, bytesRead);
                    logger.info("Received AI response: {}", aiResponse);
                    sink.next(aiResponse);
                }
            } catch (IOException sendMessageToLLMExc) {
                logger.error("Failed to send message to LLM: {}", sendMessageToLLMExc.getMessage());
                sink.error(sendMessageToLLMExc);
            }
            sink.complete();
        }).subscribeOn(Schedulers.boundedElastic()));
    }

    /**
     * <h3>{@link #handleUserRequest(Flux<Object>)}</h3>
     * Handles user requests and sends them to the LLM.
     *
     * @param userRequestFlux The Flux of user requests.
     * @return A {@link Flux}-{@link Object} that represents the AI response.
     */
    public static Flux<Object> handleUserRequest(Flux<Object> userRequestFlux) {
        return UserRequestController.sendUserRequestToLLM(userRequestFlux)
                .flatMap(message -> sendMessageToLLM(Flux.just(message)))
                .flatMap(response -> AiResponseController.receiveAiResponseFromLLM(Flux.just(response)));
    }
}
