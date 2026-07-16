package view;

public class GeneralPrinter {
    public static void print(String message) {
        System.out.println(message);
    }

    public static void printErr(String errorMessage) {
        System.err.println("error :"+errorMessage);
    }
}
