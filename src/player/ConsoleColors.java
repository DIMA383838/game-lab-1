package player;

public class ConsoleColors {
    public static final String RED = "\033[0;31m";
    public static final String RESET = "\033[0m";

    public static String red(String text) {
        return RED + text + RESET;
    }
}
