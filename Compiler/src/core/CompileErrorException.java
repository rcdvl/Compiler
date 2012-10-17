package core;

public class CompileErrorException extends Exception {

    private final int line;

    public CompileErrorException(String message, int line) {
        super(message);
        this.line = line;
        System.out.println(message + " === " + line );
    }

    public int getLine() {
        return line;
    }
}

