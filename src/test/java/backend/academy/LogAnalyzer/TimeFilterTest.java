package backend.academy.LogAnalyzer;

import backend.academy.LogAnalyzer.core.LogEntry;
import backend.academy.LogAnalyzer.core.LogFilter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Log Filter Tests")
class LogFilterTest {
    private LogEntry testEntry;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @BeforeEach
    void setUp() {

        testEntry = createLogEntry("2015-05-21T10:00:00Z");
    }

    private LogEntry createLogEntry(String timeStr) {
        ZonedDateTime time = ZonedDateTime.parse(timeStr, FORMATTER);
        return new LogEntry.Builder().withTimeLocal(time).withRemoteAddr("127.0.0.1")
                .withRequest("GET /api/users HTTP/1.1").withStatus(200).withHttpUserAgent("Mozilla/5.0").build();
    }

    @Test
    @DisplayName("Should filter by time range")
    void shouldFilterByTimeRange() {

        ZonedDateTime from = ZonedDateTime.parse("2015-05-21T09:00:00Z", FORMATTER);
        ZonedDateTime to = ZonedDateTime.parse("2015-05-21T11:00:00Z", FORMATTER);

        assertTrue(LogFilter.filterByTime(testEntry, from, to));
    }

    @Test
    @DisplayName("Should handle boundary values")
    void shouldIncludeBoundaryValues() {

        ZonedDateTime exactTime = ZonedDateTime.parse("2015-05-21T10:00:00Z", FORMATTER);

        assertTrue(LogFilter.filterByTime(testEntry, exactTime, exactTime));
    }

    @Test
    @DisplayName("Should return false for non-overlapping range")
    void shouldReturnEmptyListForNonOverlappingRange() {

        ZonedDateTime from = ZonedDateTime.parse("2015-05-17T11:00:00Z", FORMATTER);
        ZonedDateTime to = ZonedDateTime.parse("2015-05-17T12:00:00Z", FORMATTER);

        assertFalse(LogFilter.filterByTime(testEntry, from, to));
    }

    @Test
    @DisplayName("Should filter by field")
    void shouldFilterByField() {

        assertTrue(LogFilter.filterByField(testEntry, "ip", "127.0.0.1"));
        assertTrue(LogFilter.filterByField(testEntry, "method", "GET"));
        assertTrue(LogFilter.filterByField(testEntry, "status", "200"));
        assertTrue(LogFilter.filterByField(testEntry, "agent", "Mozilla"));
    }
}
