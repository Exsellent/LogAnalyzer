package backend.academy.LogAnalyzer.report;

import backend.academy.LogAnalyzer.core.LogStatistics;

public final class ReportGenerator {

    private ReportGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Генерирует отчет в формате Markdown.
     *
     * @param stats
     *            Log statistics которая будет включена в отчет
     *
     * @return Formatted report as string
     */
    public static String generateMarkdownReport(LogStatistics stats) {
        return generateReport(new MarkdownReportFormatter(), stats);
    }

    /**
     * Генерирует отчет в формате AsciiDoc
     *
     * @param stats
     *            Log statistics которая будет включена в отчет
     *
     * @return Formatted report as string
     */
    public static String generateAsciidocReport(LogStatistics stats) {
        return generateReport(new AsciidocReportFormatter(), stats);
    }

    /**
     * метод для создания отчета с использованием указанного форматирования.
     *
     * @param formatter
     *            Report formatter который используется
     * @param stats
     *            Log statistics которая будет включена в отчет
     *
     * @return Formatted report as string
     */
    private static String generateReport(ReportFormatter formatter, LogStatistics stats) {
        formatter.appendGeneralInformation(stats);
        formatter.appendRequestedResources(stats);
        formatter.appendResponseCodes(stats);
        formatter.appendIpAddressStatistics(stats);
        formatter.appendHourlyDistribution(stats);

        return formatter.getReport();
    }
}
