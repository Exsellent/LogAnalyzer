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
            LOGGER.error("Путь к лог-файлу не указан. Используйте параметр -f или --file.");
            printHelpAndExit(options);
        }

        // Получение типа отчета (по умолчанию markdown)
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

    // Создает параметры командной строки для программы.**

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

    /**
     * Парсит аргументы командной строки и возвращает объект CommandLine.
     *
     * @param args
     *            Аргументы командной строки
     * @param options
     *            Параметры командной строки
     *
     * @return Объект CommandLine с распознанными параметрами
     */
    private static CommandLine parseCommandLine(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            LOGGER.error("Ошибка при разборе аргументов командной строки: {}", e.getMessage());
            printHelpAndExit(options);
        }
        return cmd;
    }

    /**
     * Выводит справочную информацию и завершает выполнение программы.
     *
     * @param options
     *            Параметры командной строки
     */
    private static void printHelpAndExit(Options options) {
        new HelpFormatter().printHelp(CLASS_NAME, options);
        System.exit(1);
    }

    /**
     * Преобразует строку из аргументов командной строки в объект ZonedDateTime.
     *
     * @param cmd
     *            Объект CommandLine с параметрами
     * @param option
     *            Параметр времени (from или to)
     *
     * @return Объект ZonedDateTime или null, если параметр не указан
     */
    private static ZonedDateTime getZonedDateTime(CommandLine cmd, String option) {
        return cmd.hasOption(option) ? ZonedDateTime.parse(cmd.getOptionValue(option), ISO_FORMATTER) : null;
    }

    /**
     * Генерирует отчет на основе типа (markdown или asciidoc) и статистики.
     *
     * @param reportType
     *            Тип отчета
     * @param stats
     *            Объект с собранной статистикой
     *
     * @return Отчет в виде строки
     */
    private static String generateReport(String reportType, LogStatistics stats) {
        return "asciidoc".equalsIgnoreCase(reportType) ? ReportGenerator.generateAsciidocReport(stats)
                : ReportGenerator.generateMarkdownReport(stats);
    }

    /**
     * Выполняет анализ лог-файла с учетом временных параметров и фильтров.
     *
     * @param filePath
     *            Путь к лог-файлу или URL
     * @param fromTime
     *            Начальное время
     * @param toTime
     *            Конечное время
     * @param filterField
     *            Поле для фильтрации
     * @param filterValue
     *            Значение для фильтрации
     *
     * @return Объект LogStatistics с собранными данными
     *
     * @throws Exception
     *             Исключение при ошибке чтения логов или анализа
     */
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
                    LOGGER.warn("Пропуск некорректной строки лога: {}", line);
                    return null;
                }
            }).filter(Objects::nonNull).filter(entry -> LogFilter.filterByTime(entry, fromTime, toTime))
                    .filter(entry -> LogFilter.filterByField(entry, filterField, filterValue))
                    .forEach(stats::updateStatistics);
        }

        return stats;
    }

    /**
     * Логирует информацию о методах HTTP-запросов и их количестве.
     *
     * @param stats
     *            Объект LogStatistics с собранной статистикой
     */
    private static void logRequestMethods(LogStatistics stats) {
        LOGGER.info("## Request Methods\n");
        stats.getMethodCounts().forEach((method, count) -> {
            LOGGER.info("| {} | {} | |", method, count);
        });
    }
}
