package backend.academy.LogAnalyzer;

import java.time.ZonedDateTime;

public final class LogFilter {
    private LogFilter() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Фильтрует записи журнала по временному диапазону.
     *
     * @param entry
     *            Лог-запись
     * @param from
     *            Начало временного диапазона (включительно), null если не задаётся
     * @param to
     *            Конец временного диапазона (включительно), null если не задаётся
     *
     * @return true, если запись попадает в диапазон, иначе false
     */
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

    /**
     * Фильтрует записи журнала по указанному полю и значению.
     *
     * @param entry
     *            Лог-запись
     * @param field
     *            Поле для фильтрации (например, "agent", "method", "ip")
     * @param value
     *            Значение для фильтрации
     *
     * @return true, если запись соответствует критериям, иначе false
     */
    public static boolean filterByField(LogEntry entry, String field, String value) {
        boolean result = true;

        if (field == null || value == null || value.isEmpty()) {
            return true; // Если поле или значение не указаны, не применять фильтрацию
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
                result = false; // Если не удалось распознать код статуса, фильтрация не применяется
            }
            break;
        default:
            result = true; // Если поле неизвестно, не фильтровать
            break;
        }

        return result;
    }

    /**
     * Проверяет, соответствует ли запись всем указанным критериям фильтрации.
     *
     * @param entry
     *            Лог-запись
     * @param from
     *            Начало временного диапазона
     * @param to
     *            Конец временного диапазона
     * @param filterField
     *            Поле для фильтрации
     * @param filterValue
     *            Значение для фильтрации
     *
     * @return true, если запись соответствует всем условиям, иначе false
     */
    public static boolean matches(LogEntry entry, ZonedDateTime from, ZonedDateTime to, String filterField,
            String filterValue) {
        return filterByTime(entry, from, to) && filterByField(entry, filterField, filterValue);
    }
}
