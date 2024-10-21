package backend.academy.LogAnalyzer;

/**
 * Main class for generating reports in different formats.
 */
public final class ReportGenerator {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ReportGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Generates a report in Markdown format.
     *
     * @param stats
     *            Log statistics to be included in the report
     *
     * @return Formatted report as string
     */
    public static String generateMarkdownReport(LogStatistics stats) {
        return generateReport(new MarkdownReportFormatter(), stats);
    }

    /**
     * Generates a report in AsciiDoc format.
     *
     * @param stats
     *            Log statistics to be included in the report
     *
     * @return Formatted report as string
     */
    public static String generateAsciidocReport(LogStatistics stats) {
        return generateReport(new AsciidocReportFormatter(), stats);
    }

    /**
     * Generic method to generate report using specified formatter.
     *
     * @param formatter
     *            Report formatter to use
     * @param stats
     *            Log statistics to be included in the report
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
