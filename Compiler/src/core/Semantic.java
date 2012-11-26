package core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Stack;
import java.util.Vector;

public class Semantic {
    private static Semantic instance = null;
    private final Vector<SymbolsTableEntry> symbolsTable = new Vector<SymbolsTableEntry>();
    private final Vector<String> equation = new Vector<String>();
    public final static String OPERATORS[] = {"+", "-", "*", "div", ">", ">=", "<", "<=", "=", "<>", "e", "ou", "uinv", "nao"};
    public final static int OP_PLUS = 0;
    public final static int OP_MINUS = 1;
    public final static int OP_TIMES = 2;
    public final static int OP_DIV = 3;
    public final static int OP_GREATER = 4;
    public final static int OP_GE = 5;
    public final static int OP_LESS = 6;
    public final static int OP_LE = 7;
    public final static int OP_EQ = 8;
    public final static int OP_NOT_EQ = 9;
    public final static int OP_AND = 10;
    public final static int OP_OR = 11;
    public final static int OP_INV = 12;
    public final static int OP_NEG = 13;
    
    public boolean isBooleanExpression = false;

    private Semantic() {

    }

    public static Semantic getInstance() {
        if (instance == null) {
        	newInstance();
        }

        return instance;
    }
    
    public static void newInstance() {
    	instance = new Semantic();
    }

    /**
     * find duplicate entry in symbols table
     * @param entry
     * @return true if found duplicate
     */
    public boolean findDuplicateInTable(String entry) {
        ListIterator<SymbolsTableEntry> li = symbolsTable.listIterator();
        SymbolsTableEntry ste = symbolsTable.get(symbolsTable.size() - 1);
        if (ste.lexeme.equals(entry)) {
            return true;
        } else if (ste.level != SymbolsTableEntry.UNDEFINED) {
            return false;
        }

        while (li.hasPrevious()) {
            ste = li.previous();
            if (ste.lexeme.equals(entry)) {
                return true;
            }

            if (ste.level == SymbolsTableEntry.SCOPE_MARK) {
                break;
            }
        }
        return false;
    }

    public void addSymbolToTable(SymbolsTableEntry ste) {
        symbolsTable.add(ste);
    }

    public void insertTypeOnVars(String lexeme) {
        int varType = lexeme.equals("inteiro") ? SymbolsTableEntry.VAR_TYPE_INTEGER : SymbolsTableEntry.VAR_TYPE_BOOLEAN;
        SymbolsTableEntry entry = symbolsTable.get(symbolsTable.size() - 1);
        if (entry.type == SymbolsTableEntry.VAR_TYPE &&
                entry.varType == SymbolsTableEntry.UNDEFINED) {
            entry.varType = varType;
        } else {
            return;
        }

        ListIterator<SymbolsTableEntry> it = symbolsTable.listIterator(symbolsTable.size() - 1);
        while (it.hasPrevious()) {
            entry = it.previous();
            if (entry.type != SymbolsTableEntry.VAR_TYPE) return;
            if (entry.varType != SymbolsTableEntry.UNDEFINED) return;
            entry.varType = varType;
        }
    }

    /**
     * searches for a declaration of lexeme in the symbols table
     * @param lexeme
     * @return index if found or -1
     */
    public int searchForDeclaration(String lexeme) {
        ListIterator<SymbolsTableEntry> li = symbolsTable.listIterator(symbolsTable.size() - 1);
        SymbolsTableEntry entry = symbolsTable.lastElement();
        if (entry.lexeme.equals(lexeme)) {
            return symbolsTable.indexOf(entry);
        }

        while (li.hasPrevious()) {
            entry = li.previous();
            if (entry.lexeme.equals(lexeme)) {
                return symbolsTable.indexOf(entry);
            }
        }

        return -1;
    }

    public void popUntilScope() {
        SymbolsTableEntry entry = symbolsTable.lastElement();
        ListIterator<SymbolsTableEntry> li = symbolsTable.listIterator(symbolsTable.size());

        while (li.hasPrevious()) {
            entry = li.previous();
            if (entry.level == SymbolsTableEntry.SCOPE_MARK) {
                entry.level = 0;
                break;
            }
            li.remove();
        }
    }

    public String getCurrentScope() {
        SymbolsTableEntry entry = symbolsTable.get(symbolsTable.size() - 1);
        if (entry.level == SymbolsTableEntry.SCOPE_MARK) {
            return entry.lexeme;
        }

        ListIterator<SymbolsTableEntry> it = symbolsTable.listIterator(symbolsTable.size() - 1);
        while (it.hasPrevious()) {
            entry = it.previous();
            if (entry.level == SymbolsTableEntry.SCOPE_MARK) return entry.lexeme;
        }
        return "";
    }
    
    public SymbolsTableEntry get(int index) {
        return symbolsTable.get(index);
    }

    public void addToEquation(String lexeme) {
        equation.add(lexeme);
    }

    public String getEquation() {
        StringBuilder sb = new StringBuilder();
        for(String token : equation) {
            sb.append(token);
        }
        return sb.toString();
    }

    public void clearEquation() {
        equation.removeAllElements();
    }

    /**
     * @return
     */
    public Vector<String> getPostfixEquation() {
        Stack<String> stack = new Stack<String>();
        Vector<String> postfix = new Vector<String>();
        boolean isNumber = false;

        for (String element : equation) {
            try {
                Integer.valueOf(element);
                isNumber = true;
            } catch (NumberFormatException nfe) {
                isNumber = false;
            }

            if (!Arrays.asList(OPERATORS).contains(element) && isNumber) {
                postfix.add(element);
            } else if (element.equals("(")) {
                stack.push(element);
            } else if (element.equals(")")) {
                while (!stack.empty() && !stack.peek().equals("(")) {
                    postfix.add(stack.pop());
                }
                if (!stack.empty())
                    stack.pop(); // popping out the left brace '('
            }
            else {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    if (comparePrecedence(stack.peek(), element)) {
                        postfix.add(stack.pop());
                    } else {
                        break;
                    }
                }
                stack.push(element);
            }
        }

        while (!stack.empty()) {
            if (stack.peek().equals("(")) {
                stack.pop();
                continue;
            }
            postfix.add(stack.pop());
        }
        return postfix;
    }

    /**
     * precedence:
     * <li>nao, +unario, -unario</li>
     * <li>* div</li>
     * <li>+ -</li>
     * <li>> >= < <= = <></li>
     * <li>e</li>
     * <li>ou</li>
     * @param element
     * @return true if element has higher precedence than stack top
     */
    private boolean comparePrecedence(String top, String element) {
        HashMap<String, Integer> precedenceTable = new HashMap<String, Integer>();
        precedenceTable.put("ou", 0);
        precedenceTable.put("e", 1);
        precedenceTable.put(">", 2);
        precedenceTable.put(">=", 2);
        precedenceTable.put("<", 2);
        precedenceTable.put("<=", 2);
        precedenceTable.put("=", 2);
        precedenceTable.put("<>", 2);
        precedenceTable.put("+", 3);
        precedenceTable.put("-", 3);
        precedenceTable.put("*", 4);
        precedenceTable.put("div", 4);

        if (!precedenceTable.containsKey(element)) {
            return false; // element is nao, +unario, -unario = element has higher precedence
        } else if (!precedenceTable.containsKey(top)){
            return true; // top is nao, +unario, -unario = top has higher precedence
        } else if (precedenceTable.get(top).intValue() >= precedenceTable.get(element).intValue()) {
            return true;
        } else {
            return false;
        }
    }

    public void analyzeExpression() {
        Vector<String> exp = getPostfixEquation();
        CodeGenerator generator = CodeGenerator.getInstance();
        isBooleanExpression = false;
        String booleanOperators[] = {">", ">=", "<", "<=", "=", "<>", "e", "ou", "nao", "verdadeiro", "falso"};

        for (String symbol : exp) {
            if (symbol.equals(Semantic.OPERATORS[Semantic.OP_PLUS])) {
                generator.generate("", "ADD", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_MINUS])) {
                generator.generate("", "SUB", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_DIV])) {
                generator.generate("", "DIVI", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_TIMES])) {
                generator.generate("", "MULT", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_AND])) {
                generator.generate("", "AND", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_OR])) {
                generator.generate("", "OR", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_EQ])) {
                generator.generate("", "CEQ", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_NOT_EQ])) {
                generator.generate("", "CDIF", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_GREATER])) {
                generator.generate("", "CMA", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_LESS])) {
                generator.generate("", "CME", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_GE])) {
                generator.generate("", "CMAQ", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_LE])) {
                generator.generate("", "CMEQ", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_INV])) {
                generator.generate("", "INV", "", "");
            } else if (symbol.equals(Semantic.OPERATORS[Semantic.OP_NEG])) {
                generator.generate("", "NEG", "", "");
            } else if (searchForDeclaration(symbol) != -1) {
                // its a var or func
            	SymbolsTableEntry ste = get(searchForDeclaration(symbol));
            	if (ste.type == SymbolsTableEntry.INTEGER_FUNCTION_TYPE || ste.type == SymbolsTableEntry.BOOLEAN_FUNCTION_TYPE) {
            		generator.generate("", "LDV", ste.returnAddress, "");
            	} else {
            		generator.generate("", "LDV", ste.address, "");
            	}
            } else {
                // its a number
                generator.generate("", "LDC", symbol, "");
            }
        }

        String lastElement = exp.lastElement();
        int index;
        if (Arrays.asList(booleanOperators).contains(lastElement)) {
        	isBooleanExpression = true;
        } else if ((index = searchForDeclaration(lastElement)) != -1) {
        	SymbolsTableEntry ste = symbolsTable.get(index);
        	if (ste.varType == SymbolsTableEntry.VAR_TYPE_BOOLEAN || ste.type == SymbolsTableEntry.BOOLEAN_FUNCTION_TYPE) {
        		isBooleanExpression = true;
        	}
        }
    }
}
