package backend.academy.LogAnalyzer;

import backend.academy.LogAnalyzer.io.LogLauncher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private Main() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static void main(String[] args) {
        LogLauncher.launch(args);
    }
}
