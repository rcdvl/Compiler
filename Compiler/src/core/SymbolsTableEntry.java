package core;

public class SymbolsTableEntry {
    public static final int UNDEFINED = -1;

    public static final int PROGRAM_NAME_TYPE = 0;
    public static final int VAR_TYPE = 1;
    public static final int PROCEDURE_TYPE = 2;
    public static final int INTEGER_FUNCTION_TYPE = 3;
    public static final int BOOLEAN_FUNCTION_TYPE = 4;

    public static final int SCOPE_MARK = -2;

    public static final int VAR_TYPE_BOOLEAN = 100;
    public static final int VAR_TYPE_INTEGER = 101;

    public int level;
    public int type;
    public int varType;
    public String lexeme;
    public int label;
    public int address;
    public int returnAddress;

    public int brutalReturnLabel;
}
