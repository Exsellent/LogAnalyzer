package backend.academy.LogAnalyzer;

import backend.academy.LogAnalyzer.core.LogStatistics;
import backend.academy.LogAnalyzer.report.AsciidocReportFormatter;
import backend.academy.LogAnalyzer.report.ReportConstants;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AsciidocReportFormatterTest {

    @Mock
    private LogStatistics mockStats;

    private AsciidocReportFormatter formatter;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        formatter = new AsciidocReportFormatter();

        // Создаем даты с часовым поясом
        startDate = ZonedDateTime.of(2015, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        endDate = ZonedDateTime.of(2015, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault());

        when(mockStats.getFileName()).thenReturn("test.log");
        when(mockStats.getStartDate()).thenReturn(startDate);
        when(mockStats.getEndDate()).thenReturn(endDate);
        when(mockStats.getTotalRequests()).thenReturn(100);
        when(mockStats.getAverageResponseSize()).thenReturn(1024.0);
        when(mockStats.get95PercentileSize()).thenReturn(2048);

        Map<String, Integer> resourceCounts = new HashMap<>();
        resourceCounts.put("/index.html", 50);
        resourceCounts.put("/about.html", 30);
        when(mockStats.getResourceCounts()).thenReturn(resourceCounts);

        Map<Integer, Integer> statusCounts = new HashMap<>();
        statusCounts.put(200, 80);
        statusCounts.put(404, 20);
        when(mockStats.getStatusCounts()).thenReturn(statusCounts);

        Map<String, Integer> ipCounts = new LinkedHashMap<>();
        ipCounts.put("192.168.1.1", 40);
        ipCounts.put("192.168.1.2", 30);
        when(mockStats.getTopIpAddresses()).thenReturn(ipCounts);

        Map<Integer, Integer> hourlyStats = new LinkedHashMap<>();
        hourlyStats.put(0, 10);
        hourlyStats.put(1, 15);
        when(mockStats.getHourlyDistribution()).thenReturn(hourlyStats);
    }

    @Test
    void testGeneralInformationFormat() {
        formatter.appendGeneralInformation(mockStats);
        String report = formatter.getReport();

        System.out.println("Actual report content:");
        System.out.println(report);

        assertTrue(report.contains("General Information"), "Should contain section header");
        assertTrue(report.contains("|==="), "Should contain table separator");
        assertTrue(report.contains("Metric") && report.contains("Value"), "Should contain table headers");
        assertTrue(report.contains("test.log"), "Should contain filename");
        assertTrue(report.contains("100"), "Should contain total requests");

        String formattedStartDate = startDate.format(DateTimeFormatter.ofPattern(ReportConstants.DATE_FORMAT));
        String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern(ReportConstants.DATE_FORMAT));
        assertTrue(report.contains(formattedStartDate), "Should contain start date: " + formattedStartDate);
        assertTrue(report.contains(formattedEndDate), "Should contain end date: " + formattedEndDate);

        String averageSize = String.format(ReportConstants.FLOAT_FORMAT, 1024.0);
        assertTrue(report.contains(averageSize), "Should contain average size: " + averageSize);
        assertTrue(report.contains("2048"), "Should contain 95th percentile size");
    }

    @Test
    void testResponseCodesFormat() {
        formatter.appendResponseCodes(mockStats);
        String report = formatter.getReport();

        assertTrue(report.contains("Response Codes"));
        assertTrue(report.contains("Code") && report.contains("Name") && report.contains("Count"));
        assertTrue(report.contains("200") && report.contains("OK"));
        assertTrue(report.contains("404") && report.contains("Not Found"));
    }

    @Test
    void testTableSeparators() {
        formatter.appendGeneralInformation(mockStats);
        String report = formatter.getReport();

        assertTrue(report.contains("|==="));
        assertTrue(report.split("\n").length > 5);
    }

    @Test
    void testCompleteReportStructure() {
        formatter.appendGeneralInformation(mockStats);
        formatter.appendRequestedResources(mockStats);
        formatter.appendResponseCodes(mockStats);
        formatter.appendIpAddressStatistics(mockStats);
        formatter.appendHourlyDistribution(mockStats);

        String report = formatter.getReport();

        assertTrue(report.contains("General Information"));
        assertTrue(report.contains("Requested Resources"));
        assertTrue(report.contains("Response Codes"));
        assertTrue(report.contains("Top IP Addresses"));
        assertTrue(report.contains("Hourly Request Distribution"));
        assertTrue(report.endsWith("|===\n"));
    }
}
