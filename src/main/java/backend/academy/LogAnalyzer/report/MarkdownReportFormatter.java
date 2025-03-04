package backend.academy.LogAnalyzer.report;

public class MarkdownReportFormatter extends BaseReportFormatter {

    /**
     * Создаем markdown section header.
     *
     * @param title
     *            Section title
     *
     * @return Formatted section header
     */
    @Override
    protected String getSectionHeader(String title) {
        return "## " + title + ReportConstants.Labels.DOUBLE_NEW_LINE;
    }

    /**
     * Создаем markdown table header.
     *
     * @param columns
     *            Column names
     *
     * @return Formatted table header
     */
    @Override
    protected String getTableHeader(String... columns) {
        StringBuilder header = new StringBuilder("|");
        StringBuilder separator = new StringBuilder("|");

        for (String column : columns) {
            header.append(" ").append(column).append(" |");
            separator.append("-------|");
        }

        return header.append(ReportConstants.Labels.NEW_LINE).append(separator).append(ReportConstants.Labels.NEW_LINE)
                .toString();
    }

    /**
     * Создаем markdown table row.
     *
     * @param columns
     *            Row values
     *
     * @return Formatted table row
     */
    @Override
    protected String getTableRow(String... columns) {
        StringBuilder row = new StringBuilder("|");

        for (String column : columns) {
            row.append(" ").append(column).append(" |");
        }

        return row.append(ReportConstants.Labels.NEW_LINE).toString();
    }

    /**
     * Генерируем строку кода состояния в формате markdown.
     *
     * @param statusCode
     *            HTTP status code
     * @param statusName
     *            Status code description
     * @param count
     *            Number of occurrences
     *
     * @return Formatted status code row
     */
    @Override
    protected String generateStatusRow(int statusCode, String statusName, Integer count) {
        return getTableRow(String.valueOf(statusCode), statusName, count != null ? count.toString() : "0");
    }
}
