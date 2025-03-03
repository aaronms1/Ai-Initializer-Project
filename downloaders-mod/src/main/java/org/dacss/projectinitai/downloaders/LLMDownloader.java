package org.dacss.projectinitai.downloaders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * <h1>{@link LLMDownloader}</h1>
 * This class is responsible for downloading files related to a specific LLM (Language Learning Model).
 */
public class LLMDownloader {

    private static final Logger log = LoggerFactory.getLogger(LLMDownloader.class);
    private static final String USER_HOME = System.getProperty("user.home");
    private static String baseUrl;

    /**
     * <h3>{@link #LLMDownloader(String)}</h3>
     *
     * @param baseUrl The base URL from which files will be downloaded.
     */
    public LLMDownloader(String baseUrl) {
        LLMDownloader.baseUrl = baseUrl;
    }

    /**
     * <h3>{@link #getFilesToDownload()}</h3>
     * Retrieves a list of files to be downloaded.
     *
     * @return A list of FileDownloadInfo objects containing file names and URLs.
     */
    private static List<FileDownloadInfo> getFilesToDownload() {
        return List.of(
                new FileDownloadInfo(".gitattributes", baseUrl + ".gitattributes?download=true"),
                new FileDownloadInfo("LICENSE", baseUrl + "LICENSE?download=true"),
                new FileDownloadInfo("README.md", baseUrl + "README.md?download=true"),
                new FileDownloadInfo("generation_config.json", baseUrl + "generation_config.json?download=true"),
                new FileDownloadInfo("merges.txt", baseUrl + "merges.txt?download=true"),
                new FileDownloadInfo("model.safetensors", baseUrl + "model.safetensors?download=true"),
                new FileDownloadInfo("tokenizer.json", baseUrl + "tokenizer.json?download=true"),
                new FileDownloadInfo("tokenizer_config.json", baseUrl + "tokenizer_config.json?download=true"),
                new FileDownloadInfo("vocab.json", baseUrl + "vocab.json?download=true")
        );
    }

    /**
     * <h3>{@link #downloadLLM(String)}</h3>
     * Downloads the specified LLM files in parallel.
     *
     * @param llmName The name of the LLM for which files are being downloaded.
     * @return A Flux stream of download status messages.
     */
    public static Flux<Object> downloadLLM(String llmName) {
        int parallelism = Runtime.getRuntime().availableProcessors();
        return Flux.fromIterable(getFilesToDownload())
                .parallel(parallelism)
                .runOn(Schedulers.boundedElastic())
                .flatMap(fileInfo -> Flux.create(sink -> {
                    HttpClient client = HttpClient.newBuilder()
                            .followRedirects(HttpClient.Redirect.ALWAYS)
                            .build();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(fileInfo.getFileUrl()))
                            .GET()
                            .build();

                    CompletableFuture<HttpResponse<InputStream>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream());
                    responseFuture.thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            try (InputStream inputStream = response.body()) {
                                Path targetPath = getTargetPath(llmName, fileInfo.getFileName());
                                createDirectory(targetPath.getParent().toString()).publishOn(Schedulers.boundedElastic()).doOnComplete(() -> {
                                            try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(targetPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
                                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                                int bytesRead;
                                                while ((bytesRead = inputStream.read(buffer.array())) != -1) {
                                                    buffer.limit(bytesRead);
                                                    fileChannel.write(buffer, fileChannel.size()).get();
                                                    buffer.clear();
                                                }
                                                log.info("Downloaded: {}", fileInfo.getFileName());
                                                sink.next("Downloaded: " + fileInfo.getFileName());
                                                sink.complete();
                                            } catch (IOException | InterruptedException | java.util.concurrent.ExecutionException llmDownloaderExc) {
                                                log.error("Error writing file: {}", fileInfo.getFileName(), llmDownloaderExc);
                                                sink.next("Failed to download: " + fileInfo.getFileName() + " (Exception: " + llmDownloaderExc.getMessage() + ")");
                                                sink.complete();
                                            }
                                        }).subscribe();
                            } catch (IOException llmDownloaderInputStreamExc) {
                                log.error("Error processing input stream for file: {}", fileInfo.getFileName(), llmDownloaderInputStreamExc);
                                sink.next("Failed to download: " + fileInfo.getFileName() + " (Exception: " + llmDownloaderInputStreamExc.getMessage() + ")");
                                sink.complete();
                            }
                        } else {
                            log.error("Failed to download: {} (HTTP {})", fileInfo.getFileName(), response.statusCode());
                            sink.next("Failed to download: " + fileInfo.getFileName() + " (HTTP " + response.statusCode() + ")");
                            sink.complete();
                        }
                    }).exceptionally(exc -> {
                        log.error("Error during HTTP request for file: {}", fileInfo.getFileName(), exc);
                        sink.next("Failed to download: " + fileInfo.getFileName() + " (Exception: " + exc.getMessage() + ")");
                        sink.complete();
                        return null;
                    });
                })).sequential();
    }

    /**
     * <h3>{@link #createDirectory(String)}</h3>
     * Creates a directory if it does not already exist.
     *
     * @param path The path of the directory to be created.
     * @return A Flux stream indicating the creation status.
     */
    private static Flux<Object> createDirectory(String path) {
        return Flux.create(sink -> {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    log.error("Failed to create directory: {}", path);
                } else {
                    log.info("Directory created successfully: {}", path);
                }
            } else {
                log.info("Directory already exists: {}", path);
            }
            sink.next(new Object());
            sink.complete();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * <h3>{@link #getTargetPath(String, String)}</h3>
     * Constructs the target path for the downloaded file based on its type.
     *
     * @param llmName The name of the LLM.
     * @param fileName The name of the file.
     * @return The target path where the file will be saved.
     */
    private static Path getTargetPath(String llmName, String fileName) {
        String subDir;
        if (fileName.endsWith(".json") || fileName.endsWith(".txt")) {
            subDir = "configs";
        } else if (fileName.equals(".gitattributes") || fileName.equals("LICENSE") || fileName.equals("README.md")) {
            subDir = "info";
        } else if (fileName.startsWith("model.")) {
            subDir = "model";
        } else {
            subDir = "checksums";
        }
        return Paths.get(USER_HOME).resolve(Paths.get(".project-ai-initializer/models", llmName, subDir, fileName)).normalize();
    }
}