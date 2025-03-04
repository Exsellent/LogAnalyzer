package backend.academy.LogAnalyzer.core;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LogParser {
    private static final Logger LOGGER = LogManager.getLogger(LogParser.class);

    // Поддержка двух форматов даты: старого и ISO8601
    private static final DateTimeFormatter OLD_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z",
            Locale.ENGLISH);
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private static final String LOG_PATTERN = "^(\\S+) " // IP-адрес клиента
            + "- (\\S+) " // Имя пользователя (или "-")
            + "\\[(.+?)\\] " // Временная метка в формате [dd/MMM/yyyy:HH:mm:ss Z]
            + "\"(.+?)\" " // HTTP-запрос в кавычках (метод, ресурс и версия)
            + "(\\d{3}) " // Статус-код HTTP-ответа (например, 200, 404)
            + "(\\d+) " // Размер тела ответа в байтах
            + "\"(.*?)\" " // Поле HTTP Referer в кавычках
            + "\"(.*?)\"$"; // User-Agent клиента в кавычках

    private static final int REMOTE_ADDR_GROUP = 1;
    private static final int REMOTE_USER_GROUP = 2;
    private static final int TIME_LOCAL_GROUP = 3;
    private static final int REQUEST_GROUP = 4;
    private static final int STATUS_GROUP = 5;
    private static final int BODY_BYTES_SENT_GROUP = 6;
    private static final int HTTP_REFERER_GROUP = 7;
    private static final int HTTP_USER_AGENT_GROUP = 8;

    private LogParser() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Парсит строку лога и возвращает Optional от LogEntry.
     *
     * @param line
     *            строка лога
     *
     * @return Optional от LogEntry, содержащий информацию о записи, или Optional.empty() при ошибке
     */
    public static Optional<LogEntry> parseLine(String line) {
        Pattern pattern = Pattern.compile(LOG_PATTERN);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            LOGGER.warn("Строка не соответствует шаблону: {}", line);
            return Optional.empty();
        }

        try {
            String remoteAddr = matcher.group(REMOTE_ADDR_GROUP);
            String remoteUser = matcher.group(REMOTE_USER_GROUP);
            String timeLocalStr = matcher.group(TIME_LOCAL_GROUP);
            String request = matcher.group(REQUEST_GROUP);
            int status = Integer.parseInt(matcher.group(STATUS_GROUP));
            long bodyBytesSent = Long.parseLong(matcher.group(BODY_BYTES_SENT_GROUP));
            String httpReferer = matcher.group(HTTP_REFERER_GROUP);
            String httpUserAgent = matcher.group(HTTP_USER_AGENT_GROUP);

            ZonedDateTime timeLocal = parseDate(timeLocalStr);

            return Optional.of(new LogEntry(remoteAddr, remoteUser, timeLocal, request, status, bodyBytesSent,
                    httpReferer, httpUserAgent));
        } catch (Exception e) {
            LOGGER.warn("Ошибка при парсинге строки лога: {}. Ошибка: {}", line, e.getMessage());
            return Optional.empty();
        }
    }

    private static ZonedDateTime parseDate(String dateStr) {
        try {
            return ZonedDateTime.parse(dateStr, ISO_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return ZonedDateTime.parse(dateStr, OLD_DATE_FORMATTER);
        }
    }
}
