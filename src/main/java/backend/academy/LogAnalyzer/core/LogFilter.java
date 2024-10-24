package backend.academy.LogAnalyzer.core;

import java.time.ZonedDateTime;

public final class LogFilter {
    private LogFilter() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Фильтрует записи журнала по временному диапазону.

    public static boolean filterByTime(LogEntry entry, ZonedDateTime from, ZonedDateTime to) {
        ZonedDateTime time = entry.getTimeLocal();
        if (from != null && time.isBefore(from)) {
            return false;
        }
        if (to != null && time.isAfter(to)) {
            return false;
        }
        return true;
    }

    // Фильтрует записи журнала по указанному полю и значению.

    public static boolean filterByField(LogEntry entry, String field, String value) {
        boolean result = true;

        if (field == null || value == null || value.isEmpty()) {
            return true;
        }

        switch (field.toLowerCase()) {
        case "agent":
            result = entry.getHttpUserAgent() != null && entry.getHttpUserAgent().contains(value);
            break;
        case "method":
            result = entry.getRequest() != null && entry.getRequest().split(" ")[0].equalsIgnoreCase(value);
            break;
        case "ip":
            result = entry.getRemoteAddr() != null && entry.getRemoteAddr().equals(value);
            break;
        case "status":
            try {
                int statusCode = Integer.parseInt(value);
                result = entry.getStatus() == statusCode;
            } catch (NumberFormatException e) {
                result = false;
            }
            break;
        default:
            result = true;
            break;
        }

        return result;
    }

    // Проверяет, соответствует запись всем указанным критериям фильтрации.

    public static boolean matches(LogEntry entry, ZonedDateTime from, ZonedDateTime to, String filterField,
            String filterValue) {
        return filterByTime(entry, from, to) && filterByField(entry, filterField, filterValue);
    }
}
