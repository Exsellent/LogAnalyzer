package backend.academy.LogAnalyzer.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LogReader {

    private static final int MAX_RETRIES = 3;

    private LogReader() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Stream<String> readLogs(String path) throws IOException {
        if (isValidURL(path)) {
            return readFromUrlWithRetries(path, MAX_RETRIES).stream();
        } else {

            Path filePath = getPath(path);
            if (!Files.exists(filePath)) {
                throw new IOException("File does not exist: " + path);
            }
            return Files.lines(filePath);
        }
    }

    private static boolean isValidURL(String path) {
        try {
            URL url = new URL(path);
            return "http".equalsIgnoreCase(url.getProtocol()) || "https".equalsIgnoreCase(url.getProtocol())
                    || "file".equalsIgnoreCase(url.getProtocol());
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private static List<String> readFromUrlWithRetries(String path, int maxRetries) throws IOException {
        int attempt = 0;
        IOException lastException = null;

        while (attempt < maxRetries) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(path).openStream()))) {

                return reader.lines().collect(Collectors.toList());
            } catch (IOException e) {
                lastException = e;
                attempt++;
                System.err.println("Failed to read from URL: " + path + ". Attempt " + attempt + " of " + maxRetries);
                if (attempt >= maxRetries) {
                    throw new IOException("Exceeded maximum retry attempts to read from URL: " + path, lastException);
                }
            }
        }
        throw new IOException("Failed to read from URL after " + maxRetries + " attempts");
    }

    // Метод для корректного получения пути, поддерживающий как URL, так и локальные файлы
    private static Path getPath(String path) throws IOException {
        try {
            URL url = new URL(path);
            return Paths.get(url.toURI());
        } catch (MalformedURLException | IllegalArgumentException e) {

            return Paths.get(path);
        } catch (Exception e) {
            throw new IOException("Unable to process path: " + path, e);
        }
    }
}
