package backend.academy.LogAnalyzer;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import backend.academy.LogAnalyzer.io.LogReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogReaderTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldReadLocalFile() throws IOException {
        // Given
        Path logFile = tempDir.resolve("test.log");
        // Обновляем даты на ISO8601 формат
        String logContent = "127.0.0.1 - - [2015-05-21T10:55:36Z] \"GET /api/users HTTP/1.1\" 200 1234 \"-\" \"Mozilla/5.0\"\n"
                + "127.0.0.2 - - [2015-05-17T10:55:37Z] \"POST /api/data HTTP/1.1\" 201 532 \"-\" \"Mozilla/5.0\"";
        Files.writeString(logFile, logContent);

        // When
        List<String> lines = LogReader.readLogs(logFile.toString()).toList();

        // Then
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("127.0.0.1"));
    }

    @Test
    void shouldThrowExceptionWhenLocalFileNotFound() {
        // Given
        String nonExistentFile = "nonexistent.log";

        // When & Then
        assertThrows(IOException.class, () -> LogReader.readLogs(nonExistentFile));
    }

    @Test
    void shouldReadFromUrl() throws IOException {
        // Given
        // Создаем временный файл и используем его как локальный URL
        Path logFile = tempDir.resolve("test.log");
        // Обновляем даты на ISO8601 формат
        String logContent = "127.0.0.1 - - [2015-05-17T10:55:36Z] \"GET /api/users HTTP/1.1\" 200 1234 \"-\" \"Mozilla/5.0\"";
        Files.writeString(logFile, logContent);
        URL url = logFile.toUri().toURL();

        // When
        List<String> lines = LogReader.readLogs(url.toString()).toList();

        // Then
        assertFalse(lines.isEmpty());
        assertTrue(lines.get(0).contains("127.0.0.1"));
    }

    @Test
    void shouldHandleEmptyFile() throws IOException {
        // Given
        Path emptyFile = tempDir.resolve("empty.log");
        Files.createFile(emptyFile);

        // When
        List<String> lines = LogReader.readLogs(emptyFile.toString()).toList();

        // Then
        assertTrue(lines.isEmpty());
    }
}
