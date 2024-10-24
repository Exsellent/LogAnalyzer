package backend.academy.LogAnalyzer.report;

public class AsciidocReportFormatter extends BaseReportFormatter {

    private static final String ASCIIDOC_TABLE_SEPARATOR = "|===";

    /**
     * Создаем AsciiDoc section header.
     *
     * @param title
     *            Section title
     *
     * @return Formatted section header
     */
    @Override
    protected String getSectionHeader(String title) {
        return "== " + title + ReportConstants.Labels.DOUBLE_NEW_LINE;
    }

    /**
     * Создаем AsciiDoc table header.
     *
     * @param columns
     *            Column names
     *
     * @return Formatted table header
     */
    @Override
    protected String getTableHeader(String... columns) {
        StringBuilder header = new StringBuilder(ASCIIDOC_TABLE_SEPARATOR).append(ReportConstants.Labels.NEW_LINE)
                .append("|");

        for (String column : columns) {
            header.append(column).append(" |");
        }

        return header.append(ReportConstants.Labels.NEW_LINE).toString();
    }

    /**
     * Создаем AsciiDoc table row.
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
            row.append(column).append(" |");
        }

        return row.append(ReportConstants.Labels.NEW_LINE).toString();
    }

    /**
     * Генерация status code row in AsciiDoc format.
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

    @Override
    public String getReport() {

        return super.getReport() + ASCIIDOC_TABLE_SEPARATOR + ReportConstants.Labels.NEW_LINE;
    }
}
