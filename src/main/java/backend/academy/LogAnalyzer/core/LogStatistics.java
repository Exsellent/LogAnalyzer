package backend.academy.LogAnalyzer.core;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogStatistics {
    private static final double PERCENTILE_95 = 0.95;
    private int totalRequests;
    private Map<String, Integer> resourceCounts = new HashMap<>();
    private Map<Integer, Integer> statusCounts = new HashMap<>();
    private Map<String, Integer> methodCounts = new HashMap<>();
    private List<Long> responseSizes = new ArrayList<>();
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private String fileName;

    // Дополнительные статистические поля
    private Map<String, Integer> ipAddressCounts = new HashMap<>();
    private Map<Integer, Integer> hourlyDistribution = new HashMap<>();

    public LogStatistics() {
    }

    public LogStatistics(String fileName) {
        this.fileName = fileName;
    }

    public LogStatistics(String fileName, List<LogEntry> entries) {
        this.fileName = fileName;
        entries.forEach(this::updateStatistics);
    }

    public void updateStatistics(LogEntry entry) {
        totalRequests++;

        String[] requestParts = entry.getRequest().split(" ");
        if (requestParts.length > 1) {
            String resource = requestParts[1];
            if (resource != null && !resource.isEmpty()) {
                resourceCounts.put(resource, resourceCounts.getOrDefault(resource, 0) + 1);
            }
        }
        if (requestParts.length > 0) {
            String method = requestParts[0];
            if (method != null && !method.isEmpty()) {
                methodCounts.put(method, methodCounts.getOrDefault(method, 0) + 1);
            }
        }

        int status = entry.getStatus();
        statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);

        responseSizes.add(entry.getBodyBytesSent());

        String ipAddress = entry.getRemoteAddr();
        ipAddressCounts.put(ipAddress, ipAddressCounts.getOrDefault(ipAddress, 0) + 1);

        int hour = entry.getTimeLocal().getHour();
        hourlyDistribution.put(hour, hourlyDistribution.getOrDefault(hour, 0) + 1);

        if (startDate == null || entry.getTimeLocal().isBefore(startDate)) {
            startDate = entry.getTimeLocal();
        }
        if (endDate == null || entry.getTimeLocal().isAfter(endDate)) {
            endDate = entry.getTimeLocal();
        }
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    @SuppressWarnings("all")
    public Map<String, Integer> getResourceCounts() {
        return resourceCounts.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @SuppressWarnings("all")
    public Map<Integer, Integer> getStatusCounts() {
        return statusCounts.entrySet().stream().sorted(Map.Entry.<Integer, Integer> comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @SuppressWarnings("all")
    public Map<String, Integer> getMethodCounts() {
        return methodCounts.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @SuppressWarnings("all")
    public double getAverageResponseSize() {
        return responseSizes.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    public int get95PercentileSize() {
        if (responseSizes.isEmpty()) {
            return 0;
        }
        List<Long> sortedSizes = new ArrayList<>(responseSizes);
        Collections.sort(sortedSizes);
        int index = (int) Math.ceil(PERCENTILE_95 * sortedSizes.size()) - 1;
        return sortedSizes.get(index).intValue();
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @SuppressWarnings("all")
    public Map<String, Integer> getTopIpAddresses() {
        return ipAddressCounts.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @SuppressWarnings("all")
    public Map<Integer, Integer> getHourlyDistribution() {
        return hourlyDistribution.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
