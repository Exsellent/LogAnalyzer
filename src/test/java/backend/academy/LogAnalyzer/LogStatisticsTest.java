package backend.academy.LogAnalyzer;

import backend.academy.LogAnalyzer.core.LogEntry;
import backend.academy.LogAnalyzer.core.LogStatistics;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogStatisticsTest {
    private LogStatistics stats;
    private List<LogEntry> entries;

    @BeforeEach
    void setUp() {
        entries = List.of(createLogEntry("127.0.0.1", "/api/users", 200, 1000),
                createLogEntry("127.0.0.1", "/api/users", 200, 2000),
                createLogEntry("127.0.0.2", "/api/data", 404, 500),
                createLogEntry("127.0.0.3", "/api/admin", 500, 1500));

        stats = new LogStatistics("test.log", entries);
    }

    @Test
    void shouldCalculateBasicMetrics() {
        // When & Then
        assertEquals(4, stats.getTotalRequests());
        assertEquals(1250.0, stats.getAverageResponseSize(), 0.01);
        assertTrue(stats.get95PercentileSize() > 0);
    }

    @Test
    void shouldCalculateResourceCounts() {
        // When
        Map<String, Integer> resourceCounts = stats.getResourceCounts();

        // Then
        assertNotNull(resourceCounts.get("/api/users"));
        assertEquals(2, resourceCounts.get("/api/users"));
        assertEquals(1, resourceCounts.get("/api/data"));
        assertEquals(1, resourceCounts.get("/api/admin"));
    }

    @Test
    void shouldCalculateStatusCodes() {
        // When
        Map<Integer, Integer> statusCounts = stats.getStatusCounts();

        // Then
        assertNotNull(statusCounts.get(200));
        assertEquals(2, statusCounts.get(200));
        assertEquals(1, statusCounts.get(404));
        assertEquals(1, statusCounts.get(500));
    }

    @Test
    void shouldCalculateTopIpAddresses() {
        // When
        Map<String, Integer> ipCounts = stats.getTopIpAddresses();

        // Then
        assertNotNull(ipCounts.get("127.0.0.1"));
        assertEquals(2, ipCounts.get("127.0.0.1"));
        assertEquals(1, ipCounts.get("127.0.0.2"));
        assertEquals(1, ipCounts.get("127.0.0.3"));
    }

    private LogEntry createLogEntry(String ip, String request, int status, int bytes) {
        ZonedDateTime time = ZonedDateTime.now();
        return new LogEntry(ip, // remoteAddr
                "-", // remoteUser
                time, // timeLocal
                "GET " + request + " HTTP/1.1", // request
                status, // status
                bytes, // bodyBytesSent
                "http://example.com", // httpReferer
                "Mozilla/5.0" // httpUserAgent
        );
    }
}
