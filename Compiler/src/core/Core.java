package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Core implements Runnable {

    private static final String[] arithmeticOperators = { "+", "-", "*" };
    private static final String[] relationalOperators = { "<", ">", "=" };
    private static final String[] punctuationOperators = { ";", ",", "(", ")", "." };

    private static final int sProgram = 1001;
    private static final int sIf = 1002;
    private static final int sThen = 1003;
    private static final int sElse = 1004;
    private static final int sWhile = 1005;
    private static final int sDo = 1006;
    private static final int sBegin = 1007;
    private static final int sEnd = 1008;
    private static final int sWrite = 1009;
    private static final int sRead = 10010;
    private static final int sVar = 10011;
    private static final int sInteger = 10012;
    private static final int sBoolean = 10013;
    private static final int sTrue = 10014;
    private static final int sFalse = 10015;
    private static final int sProcedure = 10016;
    private static final int sFunction = 10017;
    private static final int sDiv = 10018;
    private static final int sAnd = 10019;
    private static final int sOr = 10020;
    private static final int sNot = 10021;
    private static final int sComma = 10022;
    private static final int sDot = 10023;
    private static final int sOpenParentheses = 10024;
    private static final int sCloseParentheses = 10025;
    private static final int sSemicolon = 10026;
    private static final int sAttribution = 10027;
    private static final int sNumber = 10028;
    private static final int sGreaterThan = 10029;
    private static final int sGreaterThanEq = 10030;
    private static final int sEquals = 10031;
    private static final int sLessThan = 10032;
    private static final int sLessThanEq = 10033;
    private static final int sNotEq = 10034;
    private static final int sPlus = 10035;
    private static final int sMinus = 10036;
    private static final int sTimes = 10037;
    private static final int sIdentifier = 10038;
    private static final int sColon = 10039;

    private static Core instance;
    private File sourceFile;
    private ArrayList<Token> tokens;
    private char currentChar;
    private BufferedReader br;

    private Core() {
    }

    public static Core getInstance() {
        if (instance == null) {
            instance = new Core();
        }

        return instance;
    }

    public void startAnalysis() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        analyzeLexically();
    }

    /**
     * Analyze {@link #sourceFile} lexically, searching for tokens
     *
     * @return
     */
    public int analyzeLexically() {
        int c;
        try {
            br = new BufferedReader(new FileReader(sourceFile));
            c = br.read();
            currentChar = (char) c;
            while (currentChar != -1) {
                while (currentChar == '{' || currentChar == ' ' ||
                        currentChar == '\r' || currentChar == '\n' && c != -1) {
                    if (currentChar == '{') {
                        while (currentChar != '}' && c != -1) {
                            c = br.read();
                            currentChar = (char) c;
                        }
                        c = br.read();
                        currentChar = (char) c;
                    }

                    while (currentChar == ' ' || currentChar == '\r' || currentChar == '\n' && c != -1) {
                        c = br.read();
                        currentChar = (char) c;
                    }
                }
                if (c != -1) {
                    Token token = getToken();
                    if (token != null) {
                        System.out.println(token.getSymbol() + " -> " + token.getLexeme());
                        // insere lista
                    } else System.out.println("eerrrrooo");
                }
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private Token getToken() {
        if (Character.isDigit(currentChar)) {
            return(handleDigit());
        } else if (Character.isLetter(currentChar)) {
            return(handleIdentifierAndReservedKeyword());
        } else if (currentChar == ':') {
            return(handleAttribution());
        } else if (Arrays.asList(arithmeticOperators).contains("" + currentChar)) {
            return(handleArithmeticOperator());
        } else if (Arrays.asList(relationalOperators).contains("" + currentChar)) {
            return(handleRelationalOperator());
        } else if (Arrays.asList(punctuationOperators).contains("" + currentChar)) {
            return(handlePunctuation());
        } else return null;
    }

    private Token handlePunctuation() {
        Token token = new Token(0, "" + currentChar);
        int c;

        if (currentChar == ';') {
            token.setSymbol(sSemicolon);
        } else if (currentChar == ',') {
            token.setSymbol(sComma);
        } else if (currentChar == '(') {
            token.setSymbol(sOpenParentheses);
        } else if (currentChar == ')') {
            token.setSymbol(sCloseParentheses);
        } else if (currentChar == '.') {
            token.setSymbol(sDot);
        }

        try {
            c = br.read();
            currentChar = (char) c;
        } catch (Exception e) {
            return null;
        }
        return token;
    }

    private Token handleRelationalOperator() {
        String operator = "" + currentChar;
        Token token = new Token(0, operator);
        int c;

        try {
            c = br.read();
            currentChar = (char) c;

            if (operator.equals("=")) {
                token.setSymbol(sEquals);
            } else {
                if (operator.equals(">")) {
                    if (currentChar == '=') {
                        operator += currentChar;
                        token.setSymbol(sGreaterThanEq);

                        c = br.read();
                        currentChar = (char) c;
                    } else {
                        token.setSymbol(sGreaterThan);
                    }
                } else if (operator.equals("<")) {
                    if (currentChar == '=') {
                        operator += currentChar;
                        token.setSymbol(sLessThanEq);

                        c = br.read();
                        currentChar = (char) c;
                    } else if (currentChar == '>' ) {
                        operator += currentChar;
                        token.setSymbol(sNotEq);

                        c = br.read();
                        currentChar = (char) c;
                    } else {
                        token.setSymbol(sLessThan);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }

        token.setLexeme(operator);
        return token;
    }

    private Token handleArithmeticOperator() {
        int c;
        String operator = "" + currentChar;
        Token token = new Token(0, operator);

        try {
            if (currentChar == '+') {
                token.setSymbol(sPlus);
            } else if (currentChar == '-') {
                token.setSymbol(sMinus);
            } else if (currentChar == '*') {
                token.setSymbol(sTimes);
            }

            c = br.read();
            currentChar = (char) c;

            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Token handleAttribution() {
        int c;
        String operator = "" + currentChar;
        Token token = new Token(0, operator);

        try {
            c = br.read();
            currentChar = (char) c;

            if (currentChar == '=') {
                operator += currentChar;
                token.setLexeme(operator);
                token.setSymbol(sAttribution);

                c = br.read();
                currentChar = (char) c;
            } else {
                token.setSymbol(sColon);
            }

            if (token.getSymbol() == 0) {
                return null;
            } else {
                return token;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Token handleIdentifierAndReservedKeyword() {
        int c;
        String id = "" + currentChar;
        Token token = null;

        try {
            c = br.read();
            currentChar = (char) c;

            while (Character.isLetter(currentChar) || Character.isDigit(currentChar) || currentChar == '_') {
                id += currentChar;

                c = br.read();
                currentChar = (char) c;
            }
            token = new Token(0, id);

            if (id.equals("programa")) {
                token.setSymbol(sProgram);
            } else if (id.equals("se")) {
                token.setSymbol(sIf);
            } else if (id.equals("entao")) {
                token.setSymbol(sThen);
            } else if (id.equals("senao")) {
                token.setSymbol(sElse);
            } else if (id.equals("enquanto")) {
                token.setSymbol(sWhile);
            } else if (id.equals("faca")) {
                token.setSymbol(sDo);
            } else if (id.equals("inicio")) {
                token.setSymbol(sBegin);
            } else if (id.equals("fim")) {
                token.setSymbol(sEnd);
            } else if (id.equals("escreva")) {
                token.setSymbol(sWrite);
            } else if (id.equals("leia")) {
                token.setSymbol(sRead);
            } else if (id.equals("var")) {
                token.setSymbol(sVar);
            } else if (id.equals("inteiro")) {
                token.setSymbol(sInteger);
            } else if (id.equals("booleano")) {
                token.setSymbol(sBoolean);
            } else if (id.equals("verdadeiro")) {
                token.setSymbol(sTrue);
            } else if (id.equals("falso")) {
                token.setSymbol(sFalse);
            } else if (id.equals("procedimento")) {
                token.setSymbol(sProcedure);
            } else if (id.equals("funcao")) {
                token.setSymbol(sFunction);
            } else if (id.equals("div")) {
                token.setSymbol(sDiv);
            } else if (id.equals("e")) {
                token.setSymbol(sAnd);
            } else if (id.equals("ou")) {
                token.setSymbol(sOr);
            } else if (id.equals("nao")) {
                token.setSymbol(sNot);
            } else {
                token.setSymbol(sIdentifier);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return token;
    }

    private Token handleDigit() {
        int c;

        String number = "" + currentChar;
        try {
            c = br.read();
            currentChar = (char) c;

            while (Character.isDigit(currentChar)) {
                number += currentChar;
                c = br.read();
                currentChar = (char) c;
            }

            return new Token(sNumber, number);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }
}
