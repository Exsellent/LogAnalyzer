package backend.academy.LogAnalyzer.io;

import backend.academy.LogAnalyzer.core.LogFilter;
import backend.academy.LogAnalyzer.core.LogParser;
import backend.academy.LogAnalyzer.core.LogStatistics;
import backend.academy.LogAnalyzer.report.ReportGenerator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
            LOGGER.error("Путь к лог-файлу не указан. Используйте параметр -f или --file.");
            printHelpAndExit(options);
        }

        String reportType = cmd.getOptionValue(OPTION_REPORT, "markdown");

        ZonedDateTime fromTime = getZonedDateTime(cmd, OPTION_FROM);
        ZonedDateTime toTime = getZonedDateTime(cmd, OPTION_TO);

        String filterField = cmd.getOptionValue(OPTION_FILTER_FIELD);
        String filterValue = cmd.getOptionValue(OPTION_FILTER_VALUE);

        try {
            LogStatistics stats = analyzeLogFile(filePath, fromTime, toTime, filterField, filterValue);

            String report = generateReport(reportType, stats);

            LOGGER.info(report);

            logRequestMethods(stats);
        } catch (Exception e) {
            LOGGER.error("Ошибка при обработке лог-файла: {}", e.getMessage());
        }
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption("f", OPTION_FILE, true, "Путь к лог-файлу или URL");
        options.addOption("r", OPTION_REPORT, true, "Тип отчета (markdown или asciidoc)");
        options.addOption(OPTION_FROM, true, "Начальное время в формате ISO8601 (необязательно)");
        options.addOption(OPTION_TO, true, "Конечное время в формате ISO8601 (необязательно)");
        options.addOption(null, OPTION_FILTER_FIELD, true, "Поле для фильтрации (например, method, agent)");
        options.addOption(null, OPTION_FILTER_VALUE, true, "Значение для фильтрации");
        return options;
    }

    private static CommandLine parseCommandLine(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            LOGGER.error("Ошибка при разборе аргументов командной строки: {}", e.getMessage());
            printHelpAndExit(options);
            return null; // недостижимо, но для компиляции
        }
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

            lines.map(LogParser::parseLine).flatMap(Optional::stream) // Получаем только успешные парсинги
                    .filter(entry -> LogFilter.filterByTime(entry, fromTime, toTime))
                    .filter(entry -> LogFilter.filterByField(entry, filterField, filterValue))
                    .forEach(stats::updateStatistics);
        }

        return stats;
    }

    private static void logRequestMethods(LogStatistics stats) {
        LOGGER.info("## Request Methods\n");
        stats.getMethodCounts().forEach((method, count) -> {
            LOGGER.info("| {} | {} |", method, count);
        });
    }
}
