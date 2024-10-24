package backend.academy.LogAnalyzer.report;

import backend.academy.LogAnalyzer.core.LogStatistics;

public interface ReportFormatter {

    // Добавляет к отчету раздел общей информации.
    void appendGeneralInformation(LogStatistics stats);

    // Добавляет к отчету раздел "запрошенные ресурсы"
    void appendRequestedResources(LogStatistics stats);

    // Добавляет в отчет раздел "Коды ответов.
    void appendResponseCodes(LogStatistics stats);

    // Добавляет к отчету раздел статистики по IP-адресам.
    void appendIpAddressStatistics(LogStatistics stats);

    // Добавляет к отчету раздел почасового распределения.
    void appendHourlyDistribution(LogStatistics stats);

    // Возвращает полный отформатированный отчет
    String getReport();
}
