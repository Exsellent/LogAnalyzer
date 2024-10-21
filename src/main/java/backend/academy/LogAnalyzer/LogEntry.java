package backend.academy.LogAnalyzer;

import java.time.ZonedDateTime;

/**
 * Представляет собой единую запись log со всеми его полями.
 */
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
     * Package-private constructor accepting LogEntryParams.
     *
     * @param params
     *            Object containing parameters for creating LogEntry
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

    /**
     * Gets the remote address.
     *
     * @return Remote address
     */
    public String getRemoteAddr() {
        return remoteAddr;
    }

    /**
     * Gets the remote user.
     *
     * @return Remote user
     */
    public String getRemoteUser() {
        return remoteUser;
    }

    /**
     * Gets the local time.
     *
     * @return Local time
     */
    public ZonedDateTime getTimeLocal() {
        return timeLocal;
    }

    /**
     * Gets the request.
     *
     * @return Request
     */
    public String getRequest() {
        return request;
    }

    /**
     * Gets the status code.
     *
     * @return Status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * Gets the body bytes sent.
     *
     * @return Body bytes sent
     */
    public int getBodyBytesSent() {
        return bodyBytesSent;
    }

    /**
     * Gets the HTTP referer.
     *
     * @return HTTP referer
     */
    public String getHttpReferer() {
        return httpReferer;
    }

    /**
     * Gets the HTTP user agent.
     *
     * @return HTTP user agent
     */
    public String getHttpUserAgent() {
        return httpUserAgent;
    }

    /**
     * Builder class for creating LogEntry instances.
     */
    public static class Builder {
        private final LogEntryParams params = new LogEntryParams();

        /**
         * Sets the remote address.
         *
         * @param addr
         *            Remote address
         *
         * @return Builder instance
         */
        public Builder withRemoteAddr(String addr) {
            params.setRemoteAddr(addr);
            return this;
        }

        /**
         * Sets the remote user.
         *
         * @param user
         *            Remote user
         *
         * @return Builder instance
         */
        public Builder withRemoteUser(String user) {
            params.setRemoteUser(user);
            return this;
        }

        /**
         * Sets the local time.
         *
         * @param timeLocal
         *            Local time
         *
         * @return Builder instance
         */
        public Builder withTimeLocal(ZonedDateTime timeLocal) {
            params.setTimeLocal(timeLocal);
            return this;
        }

        /**
         * Sets the request.
         *
         * @param request
         *            Request
         *
         * @return Builder instance
         */
        public Builder withRequest(String request) {
            params.setRequest(request);
            return this;
        }

        /**
         * Sets the status code.
         *
         * @param status
         *            Status code
         *
         * @return Builder instance
         */
        public Builder withStatus(int status) {
            params.setStatus(status);
            return this;
        }

        /**
         * Sets the body bytes sent.
         *
         * @param bodyBytesSent
         *            Body bytes sent
         *
         * @return Builder instance
         */
        public Builder withBodyBytesSent(int bodyBytesSent) {
            params.setBodyBytesSent(bodyBytesSent);
            return this;
        }

        /**
         * Sets the HTTP referer.
         *
         * @param referer
         *            HTTP referer
         *
         * @return Builder instance
         */
        public Builder withHttpReferer(String referer) {
            params.setHttpReferer(referer);
            return this;
        }

        /**
         * Sets the HTTP user agent.
         *
         * @param userAgent
         *            HTTP user agent
         *
         * @return Builder instance
         */
        public Builder withHttpUserAgent(String userAgent) {
            params.setHttpUserAgent(userAgent);
            return this;
        }

        /**
         * Builds a new LogEntry instance.
         *
         * @return New LogEntry instance
         */
        public LogEntry build() {
            return new LogEntry(params);
        }
    }
}
