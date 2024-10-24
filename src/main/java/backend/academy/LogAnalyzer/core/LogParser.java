package backend.academy.LogAnalyzer.core;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LogParser {
    private static final Logger LOGGER = LogManager.getLogger(LogParser.class);

    // поддержка двух форматов даты
    private static final DateTimeFormatter OLD_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z",
            Locale.ENGLISH);
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private static final String LOG_PATTERN = "^(\\S+) - (\\S+) \\[(.+?)\\] \"(.+?)\""
            + " (\\d{3}) (\\d+) \"(.*?)\" \"(.*?)\"$";

    private static final int REMOTE_ADDR_GROUP = 1;
    private static final int REMOTE_USER_GROUP = 2;
    private static final int TIME_LOCAL_GROUP = 3;
    private static final int REQUEST_GROUP = 4;
    private static final int STATUS_GROUP = 5;
    private static final int BODY_BYTES_SENT_GROUP = 6;
    private static final int HTTP_REFERER_GROUP = 7;
    private static final int HTTP_USER_AGENT_GROUP = 8;

    private LogParser() {

    }

    public static LogEntry parseLine(String line) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile(LOG_PATTERN);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            LOGGER.debug("Line does not match pattern: {}", line);
            throw new IllegalArgumentException("Неверный формат лога");
        }

        String remoteAddr = matcher.group(REMOTE_ADDR_GROUP);
        String remoteUser = matcher.group(REMOTE_USER_GROUP);
        String timeLocalStr = matcher.group(TIME_LOCAL_GROUP);
        String request = matcher.group(REQUEST_GROUP);
        int status = Integer.parseInt(matcher.group(STATUS_GROUP));
        int bodyBytesSent = Integer.parseInt(matcher.group(BODY_BYTES_SENT_GROUP));
        String httpReferer = matcher.group(HTTP_REFERER_GROUP);
        String httpUserAgent = matcher.group(HTTP_USER_AGENT_GROUP);

        ZonedDateTime timeLocal;
        try {
            // Попробуем сначала распарсить как ISO8601 дату

            timeLocal = ZonedDateTime.parse(timeLocalStr, ISO_DATE_FORMATTER);
        } catch (DateTimeParseException e) {

            // Если не удается, попробуем старый формат
            try {
                timeLocal = ZonedDateTime.parse(timeLocalStr, OLD_DATE_FORMATTER);
            } catch (DateTimeParseException ex) {
                LOGGER.debug("Failed to parse date: {}. Exception: {}", timeLocalStr, ex.getMessage());
                throw new IllegalArgumentException("Неверный формат даты: " + timeLocalStr, ex);
            }
        }

        LogEntryParams params = new LogEntryParams();
        params.setRemoteAddr(remoteAddr);
        params.setRemoteUser(remoteUser);
        params.setTimeLocal(timeLocal);
        params.setRequest(request);
        params.setStatus(status);
        params.setBodyBytesSent(bodyBytesSent);
        params.setHttpReferer(httpReferer);
        params.setHttpUserAgent(httpUserAgent);

        return new LogEntry(params);
    }
}
