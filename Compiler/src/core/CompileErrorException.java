package core;

public class CompileErrorException extends Exception {

    private final int line;
    public static boolean any = false;

    public CompileErrorException(String message, int line) {
        super(message);
        this.line = line;
        any = true;
    }

    public int getLine() {
        return line;
    }
}

