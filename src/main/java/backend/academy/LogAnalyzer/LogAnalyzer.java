package backend.academy.LogAnalyzer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LogAnalyzer {

    private static final Logger LOGGER = LogManager.getLogger(LogAnalyzer.class);
    private static final String OPTION_FILE = "file";
    private static final String OPTION_FROM = "from";
    private static final String OPTION_TO = "to";
    private static final String OPTION_OUTPUT = "output";
    private static final String OPTION_FILTER_FIELD = "filter-field";
    private static final String OPTION_FILTER_VALUE = "filter-value";
    private static final String FORMAT_MARKDOWN = "markdown";

    private LogAnalyzer() {
        // Приватный конструктор для предотвращения создания экземпляров
    }

    public static void analyze(String[] args) {
        Options options = new Options();
        options.addOption("f", OPTION_FILE, true, "Path to log file or URL");
        options.addOption(OPTION_FROM, true, "Start time in ISO8601 format");
        options.addOption(OPTION_TO, true, "End time in ISO8601 format");
        options.addOption("r", OPTION_OUTPUT, true, "Output report format (markdown or asciidoc)");

        // Новые параметры фильтрации
        options.addOption(null, OPTION_FILTER_FIELD, true, "Field to filter (e.g., agent, method)");
        options.addOption(null, OPTION_FILTER_VALUE, true, "Value to filter by");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            // Parsing command-line arguments
            cmd = parser.parse(options, args);

            // Здесь проверяем, правильно ли считываются параметры фильтрации
            String filterField = cmd.getOptionValue(OPTION_FILTER_FIELD);
            String filterValue = cmd.getOptionValue(OPTION_FILTER_VALUE);

            // Логирование значений для проверки
            LOGGER.info("Filter Field: {}", filterField);
            LOGGER.info("Filter Value: {}", filterValue);

            String logFilePath = cmd.getOptionValue(OPTION_FILE);
            String fromTimeStr = cmd.getOptionValue(OPTION_FROM);
            String toTimeStr = cmd.getOptionValue(OPTION_TO);
            String outputFormat = cmd.getOptionValue(OPTION_OUTPUT, FORMAT_MARKDOWN);

            // Date parsing
            DateTimeFormatter formatterDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            ZonedDateTime fromTime = fromTimeStr != null ? ZonedDateTime.parse(fromTimeStr, formatterDate) : null;
            ZonedDateTime toTime = toTimeStr != null ? ZonedDateTime.parse(toTimeStr, formatterDate) : null;

            // Log file reading, supporting both file path and URL
            List<LogEntry> entries = LogReader.readLogs(logFilePath).map(line -> {
                try {
                    return LogParser.parseLine(line);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Invalid log entry: {}", line);
                    return null;
                }
            }).filter(Objects::nonNull).filter(entry -> LogFilter.filterByTime(entry, fromTime, toTime))
                    .filter(entry -> LogFilter.filterByField(entry, filterField, filterValue))
                    .collect(Collectors.toList());

            // Generating and logging the report as usual

        } catch (ParseException e) {
            LOGGER.error("Error parsing arguments: {}", e.getMessage());
            formatter.printHelp("LogAnalyzer", options);
        } catch (IOException e) {
            LOGGER.error("Error reading logs: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred: {}", e.getMessage());
        }
    }
}
