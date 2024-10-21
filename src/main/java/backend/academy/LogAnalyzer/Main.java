package backend.academy.LogAnalyzer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    // Приватный конструктор для предотвращения создания объектов класса Main
    private Main() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static void main(String[] args) {
        LogLauncher.launch(args);
    }
}
