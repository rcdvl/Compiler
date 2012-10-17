package core;

import gui.PGrafica;

import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

public class Syntatic implements Runnable {

    private static Syntatic instance;
    private Lexic lexic;
    private Token token;
    private Thread thread;
    private PGrafica window;

    private Syntatic() {

    }

    public static Syntatic getInstance() {
        if (instance == null) {
            instance = new Syntatic();
        }

        return instance;
    }

    public void analyze() {
        //        do {
        // Lexico(token)
        try {
            token = runLexic();

            if (token.getSymbol() == Lexic.sProgram) {
                // Lexico(token)
                token = runLexic();

                if (token.getSymbol() == Lexic.sIdentifier) {
                    // Lexico(token)
                    token = runLexic();

                    if (token.getSymbol() == Lexic.sSemicolon) {
                        analyzeBlock();
                        if (token.getSymbol() == Lexic.sDot) {
                            // Lexico(token)
                            token = runLexic();

                            // se acabou o arquivo ou é comentario
                            if (token.getSymbol() == 7 && token.getLexeme().equals("7")) {
                                // SUCESSOOOOOOOOOO
                            } else {
                                throw new CompileErrorException("erro, esperava-se o fim do arquivo", lexic.lineNumber);
                            }
                        } else {
                            throw new CompileErrorException("erro, esperava-se o simbolo de ponto", lexic.lineNumber);
                        }
                    } else {
                        throw new CompileErrorException("erro, esperava-se o simbolo ponto e virgula", lexic.lineNumber);
                    }
                } else {
                    throw new CompileErrorException("erro, esperava-se o simbolo de um identificador", lexic.lineNumber);
                }
            } else {
                throw new CompileErrorException("erro, esperava-se o simbolo programa", lexic.lineNumber);
            }
        } catch (CompileErrorException cee) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JTextArea codeArea = window.getCodeArea();
                    DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);

                    try {
                        codeArea.getHighlighter().addHighlight(codeArea.getLineStartOffset(lexic.lineNumber-1), codeArea.getLineEndOffset(lexic.lineNumber-1), painter);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            return;
        }

        System.out.println(lexic.getToken().getLexeme());
        //        } while (lexic.getToken() != null);
    }

    private void analyzeBlock() throws CompileErrorException {
        // Lexico(token)
        token = runLexic();

        analyzeVarDeclarations();
        analyzeSubRoutines();
        analyzeCommands();
    }

    private void analyzeCommands() throws CompileErrorException {
        if (thread.isInterrupted()) {
            System.out.println("foi interrompida");
        }
        if (token.getSymbol() == Lexic.sBegin) {
            token = runLexic();
            analyzeSimpleCommand();
            while (token.getSymbol() != Lexic.sEnd) {
                if (token.getSymbol() == Lexic.sSemicolon) {
                    token = runLexic();
                    if (token.getSymbol() != Lexic.sEnd) {
                        analyzeSimpleCommand();
                    }
                } else {
                    throw new CompileErrorException("erro, esperava-se um ponto e virgula depois de um comando", lexic.lineNumber);
                }
            }
            token = runLexic();
        } else {
            throw new CompileErrorException("erro, esperava-se o simbolo de inicio", lexic.lineNumber);
        }
    }

    private void analyzeSimpleCommand() throws CompileErrorException {
        switch (token.getSymbol()) {
        case Lexic.sIdentifier:
            analyzeAttrAndProcCall();
            break;
        case Lexic.sIf:
            analyzeIf();
            break;
        case Lexic.sWhile:
            analyzeWhile();
            break;
        case Lexic.sRead:
            analyzeRead();
            break;
        case Lexic.sWrite:
            analyzeWrite();
            break;
        default:
            analyzeCommands();
        }
    }

    private void analyzeWrite() throws CompileErrorException {
        token = runLexic();
        if (token.getSymbol() == Lexic.sOpenParentheses) {
            token = runLexic();
            if (token.getSymbol() == Lexic.sIdentifier) {
                token = runLexic();
                if (token.getSymbol() == Lexic.sCloseParentheses) {
                    token = runLexic();
                } else {
                    throw new CompileErrorException("erro, era esperado um fecha parenteses depois do identificador", lexic.lineNumber);
                }
            } else {
                throw new CompileErrorException("erro, era esperado um identificador depois do abre parenteses", lexic.lineNumber);
            }
        } else {
            throw new CompileErrorException("erro, era esperado um abre parenteses depois do write", lexic.lineNumber);
        }
    }

    private void analyzeRead() throws CompileErrorException {
        token = runLexic();
        if (token.getSymbol() == Lexic.sOpenParentheses) {
            token = runLexic();
            if (token.getSymbol() == Lexic.sIdentifier) {
                token = runLexic();
                if (token.getSymbol() == Lexic.sCloseParentheses) {
                    token = runLexic();
                } else {
                    throw new CompileErrorException("erro, era esperado um fecha parenteses", lexic.lineNumber);
                }
            } else {
                throw new CompileErrorException("erro, era esperado um identificador como parametro do read", lexic.lineNumber);
            }
        } else {
            throw new CompileErrorException("erro, era esperado um abre parenteses na chamada do read", lexic.lineNumber);
        }
    }

    private void analyzeWhile() throws CompileErrorException {
        token = runLexic();
        analyzeExpression();
        if (token.getSymbol() == Lexic.sDo) {
            token = runLexic();
            analyzeSimpleCommand();
        } else {
            throw new CompileErrorException("erro, esperava do depois do while", lexic.lineNumber);
        }
    }

    private void analyzeIf() throws CompileErrorException {
        token = runLexic();
        analyzeExpression();
        if (token.getSymbol() == Lexic.sThen) {
            token = runLexic();
            analyzeSimpleCommand();
            if (token.getSymbol() == Lexic.sElse) {
                token = runLexic();
                analyzeSimpleCommand();
            }
        } else {
            throw new CompileErrorException("erro, esperava entao depois do if", lexic.lineNumber);
        }
    }

    private void analyzeExpression() throws CompileErrorException {
        analyzeSimpleExpression();
        if (token.getSymbol() == Lexic.sGreaterThan || token.getSymbol() == Lexic.sGreaterThanEq ||
                token.getSymbol() == Lexic.sEquals || token.getSymbol() == Lexic.sLessThan ||
                token.getSymbol() == Lexic.sLessThanEq || token.getSymbol() == Lexic.sNotEq) {
            token = runLexic();
            analyzeSimpleExpression();
        }
    }

    private void analyzeSimpleExpression() throws CompileErrorException {
        if (token.getSymbol() == Lexic.sPlus || token.getSymbol() == Lexic.sMinus) {
            token = runLexic();
        }
        analyzeTerm();

        while (token.getSymbol() == Lexic.sPlus || token.getSymbol() == Lexic.sMinus || token.getSymbol() == Lexic.sOr) {
            token = runLexic();
            analyzeTerm();
        }
    }

    private void analyzeTerm() throws CompileErrorException {
        analyzeFactor();
        while (token.getSymbol() == Lexic.sTimes || token.getSymbol() == Lexic.sDiv || token.getSymbol() == Lexic.sAnd) {
            token = runLexic();
            analyzeFactor();
        }
    }

    private void analyzeFactor() throws CompileErrorException {
        if (token.getSymbol() == Lexic.sIdentifier) {
            analyzeFunctionCall();
        } else if (token.getSymbol() == Lexic.sNumber) {
            token = runLexic();
        } else if (token.getSymbol() == Lexic.sNot) {
            token = runLexic();
            analyzeFactor();
        } else if (token.getSymbol() == Lexic.sOpenParentheses) {
            token = runLexic();
            analyzeExpression();
            if (token.getSymbol() == Lexic.sCloseParentheses) {
                token = runLexic();
            } else {
                throw new CompileErrorException("erro, esperava fecha parenteses depois da expressao", lexic.lineNumber);
            }
        } else if (token.getLexeme().equals("verdadeiro") || token.getLexeme().equals("falso")) {
            token = runLexic();
        } else {
            throw new CompileErrorException("erro, blablbla", lexic.lineNumber);
        }
    }

    private void analyzeFunctionCall() throws CompileErrorException {
        token = runLexic();
    }

    private void analyzeAttrAndProcCall() throws CompileErrorException {
        Token oldToken = token;
        token = runLexic();
        if (token.getSymbol() == Lexic.sAttribution) {
            analyzeAttribution();
        } else {
            analyzeProcedureCall(oldToken);
        }
    }

    private void analyzeProcedureCall(Token token) throws CompileErrorException {
        if (token.getSymbol() != Lexic.sIdentifier) {
            throw new CompileErrorException("erro, esperava identificador na chamada do procedimento", lexic.lineNumber);
        }
    }

    private void analyzeAttribution() throws CompileErrorException {
        token = runLexic();

        analyzeExpression();
    }

    private void analyzeSubRoutines() throws CompileErrorException {
        int flag = 0;
        if (token.getSymbol() == Lexic.sProcedure || token.getSymbol() == Lexic.sFunction) {

        }

        while (token.getSymbol() == Lexic.sProcedure || token.getSymbol() == Lexic.sFunction) {
            if (token.getSymbol() == Lexic.sProcedure) {
                analyzeProcedureDeclaration();
            } else {
                analyzeFunctionDeclaration();
            }
            if (token.getSymbol() == Lexic.sSemicolon) {
                token = runLexic();
            } else {
                throw new CompileErrorException("erro, esperado ponto e virgula depois de chamada de funcao/procedimento", lexic.lineNumber);
            }
        }

        if (flag == 1) {

        }
    }

    private void analyzeFunctionDeclaration() throws CompileErrorException {
        token = runLexic();
        if (token.getSymbol() == Lexic.sIdentifier) {
            token = runLexic();
            if (token.getSymbol() == Lexic.sColon) {
                token = runLexic();
                if (token.getSymbol() == Lexic.sInteger || token.getSymbol() == Lexic.sBoolean) {
                    token = runLexic();
                    if (token.getSymbol() == Lexic.sSemicolon) {
                        analyzeBlock();
                    }
                } else {
                    throw new CompileErrorException("erro, esperava tipo como inteiro ou booleano", lexic.lineNumber);
                }
            } else {
                throw new CompileErrorException("erro, esperava dois pontos", lexic.lineNumber);
            }
        } else {
            throw new CompileErrorException("erro, esperava identificador", lexic.lineNumber);
        }
    }

    private void analyzeProcedureDeclaration() throws CompileErrorException {
        token = runLexic();
        if (token.getSymbol() == Lexic.sIdentifier) {
            token = runLexic();
            if (token.getSymbol() == Lexic.sSemicolon) {
                analyzeBlock();
            } else {
                throw new CompileErrorException("erro, esperava ponto e virgula depois do identificador", lexic.lineNumber);
            }
        } else {
            throw new CompileErrorException("erro, esperava identificador", lexic.lineNumber);
        }
    }

    private void analyzeVarDeclarations() throws CompileErrorException {
        if (token.getSymbol() == Lexic.sVar) {
            token = runLexic();

            if (token.getSymbol() == Lexic.sIdentifier) {
                while (token.getSymbol() == Lexic.sIdentifier) {
                    analyzeVars();
                    if (token.getSymbol() == Lexic.sSemicolon) {
                        token = runLexic();
                    } else {
                        throw new CompileErrorException("erro, esperava-se ponto e virgula depois da decl de var", lexic.lineNumber);
                    }
                }
            } else {
                throw new CompileErrorException("erro, esperava-se a declaracao de ao menos uma variavel", lexic.lineNumber);
            }
        }
    }

    private void analyzeVars() throws CompileErrorException {
        do {
            if (token.getSymbol() == Lexic.sIdentifier) {
                token = runLexic();
                if (token.getSymbol() == Lexic.sComma || token.getSymbol() == Lexic.sColon) {
                    if (token.getSymbol() == Lexic.sComma) {
                        token = runLexic();
                        if (token.getSymbol() == Lexic.sColon) {
                            throw new CompileErrorException("erro, nao e permitido dois pontos depois da virgula?", lexic.lineNumber);
                        }
                    }
                } else {
                    throw new CompileErrorException("erro, esperava-se virgula ou dois pontos", lexic.lineNumber);
                }
            } else {
                throw new CompileErrorException("erro, esperava-se um identificador", lexic.lineNumber);
            }
        } while (token.getSymbol() != Lexic.sColon);

        token = runLexic();
        analyzeType();
    }

    private void analyzeType() throws CompileErrorException {
        if (token.getSymbol() != Lexic.sInteger && token.getSymbol() != Lexic.sBoolean) {
            throw new CompileErrorException("erro, esperava-se inteiro ou booleano como tipo", lexic.lineNumber);
        }

        token = runLexic();
    }

    private Token runLexic() throws CompileErrorException {
        try {
            lexic.getNextToken();
            synchronized (this) {
                wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Token token = lexic.getToken();
        if (token == null) {
            throw new CompileErrorException("erro, token nao reconhecido", lexic.lineNumber);
        } else {
            return token;
        }
    }

    @Override
    public void run() {
        analyze();
    }

    public void setCore(Lexic c) {
        lexic = c;
    }

    public void setThread(Thread sThread) {
        thread = sThread;
    }

    public Thread getThread() {
        return thread;
    }

    public void setWindow(PGrafica window) {
        this.window = window;
    }
}
