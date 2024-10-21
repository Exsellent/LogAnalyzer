package backend.academy.LogAnalyzer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LogLauncher {
    private static final Logger LOGGER = LogManager.getLogger(LogLauncher.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private static final String OPTION_FILE = "file";
    private static final String OPTION_REPORT = "report";
    private static final String OPTION_FROM = "from";
    private static final String OPTION_TO = "to";
    private static final String OPTION_FILTER_FIELD = "filter-field";
    private static final String OPTION_FILTER_VALUE = "filter-value";

    private static final String CLASS_NAME = "LogLauncher";

    private LogLauncher() {
    }

    public static void launch(String[] args) {
        Options options = createOptions();
        CommandLine cmd = parseCommandLine(args, options);

        String filePath = cmd.getOptionValue(OPTION_FILE);
        if (filePath == null) {
            LOGGER.error("The path to the log file was not passed. Use the option -f или --file.");
            printHelpAndExit(options);
        }

        String reportType = cmd.getOptionValue(OPTION_REPORT, "markdown");
        ZonedDateTime fromTime = getZonedDateTime(cmd, OPTION_FROM);
        ZonedDateTime toTime = getZonedDateTime(cmd, OPTION_TO);
        String filterField = cmd.getOptionValue(OPTION_FILTER_FIELD);
        String filterValue = cmd.getOptionValue(OPTION_FILTER_VALUE);

        try {
            LogStatistics stats = analyzeLogFile(filePath, fromTime, toTime, filterField, filterValue);

            // Генерация отчета
            String report = generateReport(reportType, stats);

            // Логируем сам отчет
            LOGGER.info(report);

            // Логируем методы запросов один раз
            logRequestMethods(stats);
        } catch (Exception e) {
            LOGGER.error("Error processing log file: {}", e.getMessage());
        }
    }

    private static void logRequestMethods(LogStatistics stats) {
        LOGGER.info("## Request Methods\n");
        stats.getMethodCounts().forEach((method, count) -> {
            LOGGER.info("| {} | {} | |", method, count);
        });
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption("f", OPTION_FILE, true, "Path to the log file or URL");
        options.addOption("r", OPTION_REPORT, true, "Type of report (markdown or asciidoc)");
        options.addOption(OPTION_FROM, true, "Start time in ISO8601 format (optional)");
        options.addOption(OPTION_TO, true, "End time in ISO8601 format (optional)");
        options.addOption(null, OPTION_FILTER_FIELD, true, "Field to filter by (e.g., method, agent)");
        options.addOption(null, OPTION_FILTER_VALUE, true, "Value to filter by");
        return options;
    }

    private static CommandLine parseCommandLine(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            LOGGER.error("Error parsing command line arguments: {}", e.getMessage());
            printHelpAndExit(options);
        }
        return cmd;
    }

    private static void printHelpAndExit(Options options) {
        new HelpFormatter().printHelp(CLASS_NAME, options);
        System.exit(1);
    }

    private static ZonedDateTime getZonedDateTime(CommandLine cmd, String option) {
        return cmd.hasOption(option) ? ZonedDateTime.parse(cmd.getOptionValue(option), ISO_FORMATTER) : null;
    }

    private static String generateReport(String reportType, LogStatistics stats) {
        return "asciidoc".equalsIgnoreCase(reportType) ? ReportGenerator.generateAsciidocReport(stats)
                : ReportGenerator.generateMarkdownReport(stats);
    }

    private static LogStatistics analyzeLogFile(String filePath, ZonedDateTime fromTime, ZonedDateTime toTime,
            String filterField, String filterValue) throws Exception {
        LogStatistics stats = new LogStatistics(filePath);

        try (Stream<String> lines = (filePath.startsWith("http"))
                ? new BufferedReader(new InputStreamReader(new URL(filePath).openStream())).lines()
                : Files.lines(Paths.get(filePath))) {

            lines.map(line -> {
                try {
                    return LogParser.parseLine(line);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Skipping invalid log line: {}", line);
                    return null;
                }
            }).filter(Objects::nonNull).filter(entry -> LogFilter.filterByTime(entry, fromTime, toTime))
                    .filter(entry -> LogFilter.filterByField(entry, filterField, filterValue))
                    .forEach(stats::updateStatistics);
        }

        return stats;
    }
}
