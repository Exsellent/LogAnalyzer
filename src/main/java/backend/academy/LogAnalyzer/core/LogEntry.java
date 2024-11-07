package backend.academy.LogAnalyzer.core;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LogEntry {
    private String remoteAddr;
    private String remoteUser;
    private ZonedDateTime timeLocal;
    private String request;
    private int status;
    private long bodyBytesSent;
    private String httpReferer;
    private String httpUserAgent;

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public ZonedDateTime getTimeLocal() {
        return timeLocal;
    }

    public String getRequest() {
        return request;
    }

    public int getStatus() {
        return status;
    }

    public long getBodyBytesSent() {
        return bodyBytesSent;
    }

    public String getHttpReferer() {
        return httpReferer;
    }

    public String getHttpUserAgent() {
        return httpUserAgent;
    }

    // Сеттеры
    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public void setTimeLocal(ZonedDateTime timeLocal) {
        this.timeLocal = timeLocal;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setBodyBytesSent(long bodyBytesSent) {
        this.bodyBytesSent = bodyBytesSent;
    }

    public void setHttpReferer(String httpReferer) {
        this.httpReferer = httpReferer;
    }

    public void setHttpUserAgent(String httpUserAgent) {
        this.httpUserAgent = httpUserAgent;
    }
}
