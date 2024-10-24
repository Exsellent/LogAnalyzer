package backend.academy.LogAnalyzer.report;

import backend.academy.LogAnalyzer.core.LogStatistics;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import static backend.academy.LogAnalyzer.report.ReportConstants.BYTE_UNIT;
import static backend.academy.LogAnalyzer.report.ReportConstants.FLOAT_FORMAT;
import static backend.academy.LogAnalyzer.report.ReportConstants.Labels;
import static backend.academy.LogAnalyzer.report.ReportConstants.StatusCodes;

public abstract class BaseReportFormatter implements ReportFormatter {
    protected StringBuilder report;

    protected BaseReportFormatter() {
        this.report = new StringBuilder();
    }

    @Override
    public void appendGeneralInformation(LogStatistics stats) {
        // Используем формат даты ISO8601 для всех дат
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        report.append(getSectionHeader("General Information")).append(getTableHeader("Metric", "Value"))
                .append(getTableRow("File(s)", "`" + stats.getFileName() + "`"))
                .append(getTableRow("Start Date", stats.getStartDate().format(isoFormatter)))
                .append(getTableRow("End Date", stats.getEndDate().format(isoFormatter)))
                .append(getTableRow("Total Requests", String.valueOf(stats.getTotalRequests())))
                .append(getTableRow("Average Response Size",
                        String.format(FLOAT_FORMAT, stats.getAverageResponseSize()) + BYTE_UNIT))
                .append(getTableRow("95th Percentile Response Size", stats.get95PercentileSize() + BYTE_UNIT));
    }

    @Override
    public void appendRequestedResources(LogStatistics stats) {
        report.append(getSectionHeader("Requested Resources")).append(getTableHeader("Resource", Labels.COUNT));

        for (Map.Entry<String, Integer> entry : stats.getResourceCounts().entrySet()) {
            report.append(getTableRow(entry.getKey(), String.valueOf(entry.getValue())));
        }
    }

    @Override
    public void appendResponseCodes(LogStatistics stats) {
        report.append(getSectionHeader("Response Codes")).append(getTableHeader("Code", "Name", Labels.COUNT));

        appendStatusCode(stats, StatusCodes.OK, "OK");
        appendStatusCode(stats, StatusCodes.NOT_FOUND, "Not Found");
        appendStatusCode(stats, StatusCodes.SERVER_ERROR, "Internal Server Error");
        appendStatusCode(stats, StatusCodes.FORBIDDEN, "Forbidden");
        appendStatusCode(stats, StatusCodes.NOT_MODIFIED, "Not Modified");
        appendStatusCode(stats, StatusCodes.PARTIAL_CONTENT, "Partial Content");
        appendStatusCode(stats, StatusCodes.RANGE_NOT_SATISFIABLE, "Range Not Satisfiable");
    }

    private void appendStatusCode(LogStatistics stats, int code, String name) {
        report.append(generateStatusRow(code, name, stats.getStatusCounts().get(code)));
    }

    @Override
    public void appendIpAddressStatistics(LogStatistics stats) {
        report.append(getSectionHeader("Top IP Addresses")).append(getTableHeader("IP Address", Labels.REQUEST_COUNT));

        stats.getTopIpAddresses().forEach((ip, count) -> report.append(getTableRow(ip, String.valueOf(count))));
    }

    @Override
    public void appendHourlyDistribution(LogStatistics stats) {
        report.append(getSectionHeader("Hourly Request Distribution"))
                .append(getTableHeader("Hour", Labels.REQUEST_COUNT));

        stats.getHourlyDistribution().forEach(
                (hour, count) -> report.append(getTableRow(String.format("%02d:00", hour), String.valueOf(count))));
    }

    protected abstract String getSectionHeader(String title);

    protected abstract String getTableHeader(String... columns);

    protected abstract String getTableRow(String... columns);

    protected abstract String generateStatusRow(int statusCode, String statusName, Integer count);

    @Override
    public String getReport() {
        return report.toString();
    }
}
