package backend.academy.LogAnalyzer;

/**
 * Interface for formatting log analysis reports.
 */
public interface ReportFormatter {
    /**
     * Appends general information section to the report.
     *
     * @param stats
     *            Log statistics to be included in the report
     */
    void appendGeneralInformation(LogStatistics stats);

    /**
     * Appends requested resources section to the report.
     *
     * @param stats
     *            Log statistics to be included in the report
     */
    void appendRequestedResources(LogStatistics stats);

    /**
     * Appends response codes section to the report.
     *
     * @param stats
     *            Log statistics to be included in the report
     */
    void appendResponseCodes(LogStatistics stats);

    /**
     * Appends IP address statistics section to the report.
     *
     * @param stats
     *            Log statistics to be included in the report
     */
    void appendIpAddressStatistics(LogStatistics stats);

    /**
     * Appends hourly distribution section to the report.
     *
     * @param stats
     *            Log statistics to be included in the report
     */
    void appendHourlyDistribution(LogStatistics stats);

    /**
     * Returns the complete formatted report.
     *
     * @return Formatted report as string
     */
    String getReport();
}
