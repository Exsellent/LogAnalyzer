package backend.academy.LogAnalyzer.core;

import java.time.ZonedDateTime;

public final class LogFilter {
    private LogFilter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean filterByTime(LogEntry entry, ZonedDateTime from, ZonedDateTime to) {
        ZonedDateTime time = entry.getTimeLocal();
        return (from == null || !time.isBefore(from)) && (to == null || !time.isAfter(to));
    }

    public static boolean filterByField(LogEntry entry, String field, String value) {
        if (field == null || value == null || value.isEmpty()) {
            return true;
        }
        return switch (field.toLowerCase()) {
        case "agent" -> entry.getHttpUserAgent() != null && entry.getHttpUserAgent().contains(value);
        case "method" -> entry.getRequest() != null && entry.getRequest().split(" ")[0].equalsIgnoreCase(value);
        case "ip" -> entry.getRemoteAddr() != null && entry.getRemoteAddr().equals(value);
        case "status" -> String.valueOf(entry.getStatus()).equals(value);
        default -> true;
        };
    }
}
