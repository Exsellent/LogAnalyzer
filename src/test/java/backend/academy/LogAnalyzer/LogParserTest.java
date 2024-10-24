package backend.academy.LogAnalyzer;

import backend.academy.LogAnalyzer.core.LogEntry;
import backend.academy.LogAnalyzer.core.LogParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Log Parser Tests")
class LogParserTest {

    @Test
    @DisplayName("Should parse valid log entry")
    void testValidLogEntry() {
        // Given
        // Обновленный лог с использованием ISO8601 даты
        String logLine = "127.0.0.1 - - [2015-05-17T10:55:36Z] \"GET /api/users HTTP/1.1\" 200 1234 \"-\" \"Mozilla/5.0\"";

        // When
        LogEntry entry = LogParser.parseLine(logLine);

        assertNotNull(entry);
        assertEquals("127.0.0.1", entry.getRemoteAddr());
    }

    @Test
    @DisplayName("Should throw NullPointerException for null input")
    void testNullPointerException() {
        // Given & When & Then
        assertThrows(NullPointerException.class, () -> LogParser.parseLine(null),
                "Null input should throw NullPointerException");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for empty string")
    void testEmptyString() {
        // Given
        String emptyLine = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> LogParser.parseLine(emptyLine),
                "Empty string should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid format")
    void testInvalidFormat() {
        // Given
        String invalidLine = "invalid format";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> LogParser.parseLine(invalidLine),
                "Invalid format should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid date format")
    void testInvalidDateFormat() {
        // Given
        String lineWithInvalidDate = "127.0.0.1 - - [invalid_date] \"GET /api/users HTTP/1.1\" 200 1234 \"-\" \"Mozilla/5.0\"";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> LogParser.parseLine(lineWithInvalidDate),
                "Invalid date format should throw IllegalArgumentException");
    }
}
