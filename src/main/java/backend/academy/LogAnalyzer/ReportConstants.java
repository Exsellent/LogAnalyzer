package backend.academy.LogAnalyzer;

/**
 * Constants used in report formatting.
 */
public final class ReportConstants {
    private ReportConstants() {
        // Prevents instantiation
    }

    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String FLOAT_FORMAT = "%.2f";
    public static final String BYTE_UNIT = " b|";

    public static final class StatusCodes {
        private StatusCodes() {
            // Prevents instantiation
        }

        public static final int OK = 200;
        public static final int NOT_FOUND = 404;
        public static final int SERVER_ERROR = 500;
        public static final int FORBIDDEN = 403;
        public static final int NOT_MODIFIED = 304;
        public static final int PARTIAL_CONTENT = 206;
        public static final int RANGE_NOT_SATISFIABLE = 416;
    }

    public static final class Labels {
        private Labels() {
            // Prevents instantiation
        }

        public static final String REQUEST_COUNT = "Request Count";
        public static final String COUNT = "Count";
        public static final String NEW_LINE = "\n";
        public static final String DOUBLE_NEW_LINE = "\n\n";
    }
}
