package backend.academy.LogAnalyzer.core;

import java.time.ZonedDateTime;

public class LogEntryParams {
    private String remoteAddr;

    private String remoteUser;

    private ZonedDateTime timeLocal;

    private String request;

    private int status;

    private int bodyBytesSent;

    private String httpReferer;

    private String httpUserAgent;

    // Возвращает IP-адрес клиента, который сделал запрос.

    public String getRemoteAddr() {
        return remoteAddr;
    }

    // Устанавливает IP-адрес клиента, который сделал запрос.

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public ZonedDateTime getTimeLocal() {
        return timeLocal;
    }

    public void setTimeLocal(ZonedDateTime timeLocal) {
        this.timeLocal = timeLocal;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // Возвращает размер тела ответа в байтах.

    public int getBodyBytesSent() {
        return bodyBytesSent;
    }

    public void setBodyBytesSent(int bodyBytesSent) {
        this.bodyBytesSent = bodyBytesSent;
    }

    public String getHttpReferer() {
        return httpReferer;
    }

    public void setHttpReferer(String httpReferer) {
        this.httpReferer = httpReferer;
    }

    public String getHttpUserAgent() {
        return httpUserAgent;
    }

    public void setHttpUserAgent(String httpUserAgent) {
        this.httpUserAgent = httpUserAgent;
    }
}
