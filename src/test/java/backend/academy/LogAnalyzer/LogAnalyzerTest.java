package backend.academy.LogAnalyzer;

import backend.academy.LogAnalyzer.core.LogAnalyzer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class LogAnalyzerTest {

    @TempDir
    Path tempDir;

    private Path testLogFile;

    @BeforeEach
    void setUp() throws IOException {

        testLogFile = tempDir.resolve("nginx_logs.txt");

        // Обновленный лог с использованием ISO8601 даты
        String logContent = "93.180.71.3 - - [2015-05-17T08:05:32Z] " + "\"GET /downloads/product_1 HTTP/1.1\" 304 0 "
                + "\"-\" \"Debian APT-HTTP/1.3\"\n";

        Files.writeString(testLogFile, logContent);
    }

    @Test
    void shouldAnalyzeLogFileWithoutErrors() {

        String[] args = { "-f", testLogFile.toString() };

        assertDoesNotThrow(() -> LogAnalyzer.analyze(args));
    }

    @Test
    void shouldFindGetRequestInLog() {
        String[] args = { "-f", testLogFile.toString(), "--filter-field", "method", "--filter-value", "GET" };

        assertDoesNotThrow(() -> LogAnalyzer.analyze(args));
    }
}
