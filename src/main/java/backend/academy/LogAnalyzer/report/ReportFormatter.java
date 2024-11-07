package backend.academy.LogAnalyzer.report;

import backend.academy.LogAnalyzer.core.LogStatistics;

public interface ReportFormatter {

    void appendGeneralInformation(LogStatistics stats);

    void appendRequestedResources(LogStatistics stats);

    void appendResponseCodes(LogStatistics stats);

    // Добавляет к отчету раздел статистики по IP-адресам.
    void appendIpAddressStatistics(LogStatistics stats);

    // Добавляет к отчету раздел почасового распределения.
    void appendHourlyDistribution(LogStatistics stats);

    // Возвращает полный отформатированный отчет
    String getReport();
}
