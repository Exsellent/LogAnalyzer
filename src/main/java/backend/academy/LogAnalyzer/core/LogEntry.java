package backend.academy.LogAnalyzer.core;

import java.time.ZonedDateTime;

public class LogEntry {
    private final String remoteAddr;
    private final String remoteUser;
    private final ZonedDateTime timeLocal;
    private final String request;
    private final int status;
    private final int bodyBytesSent;
    private final String httpReferer;
    private final String httpUserAgent;

    /**
     * Object containing parameters for creating LogEntry
     */
    /* package */ LogEntry(LogEntryParams params) {
        this.remoteAddr = params.getRemoteAddr();
        this.remoteUser = params.getRemoteUser();
        this.timeLocal = params.getTimeLocal();
        this.request = params.getRequest();
        this.status = params.getStatus();
        this.bodyBytesSent = params.getBodyBytesSent();
        this.httpReferer = params.getHttpReferer();
        this.httpUserAgent = params.getHttpUserAgent();
    }

    // Gets the remote address.

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

    public int getBodyBytesSent() {
        return bodyBytesSent;
    }

    public String getHttpReferer() {
        return httpReferer;
    }

    public String getHttpUserAgent() {
        return httpUserAgent;
    }

    public static class Builder {
        private final LogEntryParams params = new LogEntryParams();

        public Builder withRemoteAddr(String addr) {
            params.setRemoteAddr(addr);
            return this;
        }

        public Builder withRemoteUser(String user) {
            params.setRemoteUser(user);
            return this;
        }

        public Builder withTimeLocal(ZonedDateTime timeLocal) {
            params.setTimeLocal(timeLocal);
            return this;
        }

        public Builder withRequest(String request) {
            params.setRequest(request);
            return this;
        }

        public Builder withStatus(int status) {
            params.setStatus(status);
            return this;
        }

        public Builder withBodyBytesSent(int bodyBytesSent) {
            params.setBodyBytesSent(bodyBytesSent);
            return this;
        }

        public Builder withHttpReferer(String referer) {
            params.setHttpReferer(referer);
            return this;
        }

        public Builder withHttpUserAgent(String userAgent) {
            params.setHttpUserAgent(userAgent);
            return this;
        }

        public LogEntry build() {
            return new LogEntry(params);
        }
    }
}
