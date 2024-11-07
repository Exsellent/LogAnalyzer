package backend.academy.LogAnalyzer;

import backend.academy.LogAnalyzer.io.LogLauncher;

public final class Main {

    private Main() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static void main(String[] args) {
        LogLauncher.launch(args);
    }
}
