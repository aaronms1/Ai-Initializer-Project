package org.dacss.projectinitai.rags;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class RAGDestroyer {

    public void destroyRAG(String ragPath) throws IOException {
        Path path = Paths.get(ragPath);
        deleteDirectory(path);
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        log.error("Failed to delete file: " + p, e);
                    }
                });
        }
    }
}
