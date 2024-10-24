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
        assertNotNull(resourceCounts.get("/api/users")); // Убедитесь, что ресурс существует в мапе
        assertEquals(2, resourceCounts.get("/api/users")); // Должно быть 2 запроса к /api/users
        assertEquals(1, resourceCounts.get("/api/data")); // Должно быть 1 запрос к /api/data
        assertEquals(1, resourceCounts.get("/api/admin")); // Должно быть 1 запрос к /api/admin
    }

    @Test
    void shouldCalculateStatusCodes() {
        // When
        Map<Integer, Integer> statusCounts = stats.getStatusCounts();

        // Then
        assertNotNull(statusCounts.get(200)); // Убедитесь, что статус-код 200 существует в мапе
        assertEquals(2, statusCounts.get(200)); // Должно быть 2 запроса с кодом 200
        assertEquals(1, statusCounts.get(404)); // Должно быть 1 запрос с кодом 404
        assertEquals(1, statusCounts.get(500)); // Должно быть 1 запрос с кодом 500
    }

    @Test
    void shouldCalculateTopIpAddresses() {
        // When
        Map<String, Integer> ipCounts = stats.getTopIpAddresses();

        // Then
        assertNotNull(ipCounts.get("127.0.0.1")); // Убедитесь, что IP-адрес существует в мапе
        assertEquals(2, ipCounts.get("127.0.0.1")); // Должно быть 2 запроса с IP 127.0.0.1
        assertEquals(1, ipCounts.get("127.0.0.2")); // Должно быть 1 запрос с IP 127.0.0.2
        assertEquals(1, ipCounts.get("127.0.0.3")); // Должно быть 1 запрос с IP 127.0.0.3
    }

    private LogEntry createLogEntry(String ip, String request, int status, int bytes) {
        // Убедитесь, что строка запроса имеет правильный формат: метод, путь, версия HTTP
        return new LogEntry.Builder().withRemoteAddr(ip).withTimeLocal(ZonedDateTime.now())
                .withRequest("GET " + request + " HTTP/1.1") // добавлено "GET" и "HTTP/1.1"
                .withStatus(status).withBodyBytesSent(bytes).build();
    }
}
