package core;

import gui.PGrafica;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Lexic implements Runnable {

    private static final String[] arithmeticOperators = { "+", "-", "*" };
    private static final String[] relationalOperators = { "<", ">", "=" };
    private static final String[] punctuationOperators = { ";", ",", "(", ")", "." };

    public static final int sProgram = 1001;
    public static final int sIf = 1002;
    public static final int sThen = 1003;
    public static final int sElse = 1004;
    public static final int sWhile = 1005;
    public static final int sDo = 1006;
    public static final int sBegin = 1007;
    public static final int sEnd = 1008;
    public static final int sWrite = 1009;
    public static final int sRead = 10010;
    public static final int sVar = 10011;
    public static final int sInteger = 10012;
    public static final int sBoolean = 10013;
    public static final int sTrue = 10014;
    public static final int sFalse = 10015;
    public static final int sProcedure = 10016;
    public static final int sFunction = 10017;
    public static final int sDiv = 10018;
    public static final int sAnd = 10019;
    public static final int sOr = 10020;
    public static final int sNot = 10021;
    public static final int sComma = 10022;
    public static final int sDot = 10023;
    public static final int sOpenParentheses = 10024;
    public static final int sCloseParentheses = 10025;
    public static final int sSemicolon = 10026;
    public static final int sAttribution = 10027;
    public static final int sNumber = 10028;
    public static final int sGreaterThan = 10029;
    public static final int sGreaterThanEq = 10030;
    public static final int sEquals = 10031;
    public static final int sLessThan = 10032;
    public static final int sLessThanEq = 10033;
    public static final int sNotEq = 10034;
    public static final int sPlus = 10035;
    public static final int sMinus = 10036;
    public static final int sTimes = 10037;
    public static final int sIdentifier = 10038;
    public static final int sColon = 10039;

    private static Lexic instance;
    private File sourceFile;
    private char currentChar;
    private BufferedReader br;
    private BufferedWriter bw;
    private int c;
    private Token token;
    private Syntatic syntatic;
    private boolean firstRun = true;
    private Thread thread;
    public int lineNumber = 1;
    public int lastLine = 1;
    private PGrafica window;

    private Lexic() {

    }

    public static Lexic getInstance() {
        if (instance == null) {
            instance = new Lexic();
        }

        return instance;
    }

    public Thread getThread() {
        return thread;
    }

    public void getNextToken() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            if (firstRun) {
                File log = new File("log.txt");
                bw = new BufferedWriter(new FileWriter(log));
                c = br.read();
                currentChar = (char) c;
                firstRun = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        analyzeLexically();
        synchronized (syntatic) {
            syntatic.notify();
        }
    }

    /**
     * Analyze {@link #sourceFile} lexically, searching for tokens
     *
     * @return
     */
    public int analyzeLexically() {
        try {
            //            while (currentChar != -1) {
            while (currentChar == '{' || currentChar == ' ' ||
                    currentChar == '\r' || currentChar == '\n' || currentChar == '\t' && c != -1) {
                if (currentChar == '{') {
                    while (currentChar != '}' && c != -1) {
                        if (currentChar == '\n') {
                            lastLine = lineNumber;
                            lineNumber++;
                        }
                        c = br.read();
                        currentChar = (char) c;
                    }
                    c = br.read();
                    currentChar = (char) c;
                }

                while (currentChar == ' ' || currentChar == '\r' || currentChar == '\n' || currentChar == '\t' && c != -1) {
                    if (currentChar == '\n') {
                        lastLine = lineNumber;
                        lineNumber++;
                    }
                    c = br.read();
                    currentChar = (char) c;
                }
            }
            if (c != -1) {
                token = parseToken();
                if (token != null) {
                    // System.out.println(token.getSymbol() + " -> " + token.getLexeme());
                    bw.write("Simbolo: " + token.getSymbol() + " , lexema: " + token.getLexeme() + "\n");
                    // insere lista
                } else {
                    bw.write("Erro\n");
                    bw.close();
                    //                    new CompileErrorException("erro, token nao reconhecido", 0);
                    //System.out.println("eerrrrooo");
                    c = br.read();
                    currentChar = (char) c;
                    return -1;
                }
            } else {
                token = new Token(7, "7");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!CompileErrorException.any) {
                            JTextArea codeArea = window.getCodeArea();
                            codeArea.getHighlighter().removeAllHighlights();

                            JTextArea errorArea = window.getErrorArea();
                            errorArea.setText("Compilado com sucesso.\n");
                        }
                    }
                });
                bw.write("-------------- fim de uma execucao -------------\n\n");
                bw.flush();
                bw.close();
                return -1;
            }
            //            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private Token parseToken() {
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
        try {
            br = new BufferedReader(new FileReader(sourceFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        lineNumber = 1;
        lastLine = 1;
        firstRun = true;
        CompileErrorException.any = false;
    }

    public Token getToken() {
        return token;
    }

    public void setSyntatic(Syntatic s) {
        syntatic = s;
    }

    public void setWindow(PGrafica pGrafica) {
        window = pGrafica;
    }
}
