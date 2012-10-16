package core;

public class Syntatic implements Runnable {

    private static Syntatic instance;
    private Core core;
    private Token token;

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
        token = runLexic();

        if (token.getSymbol() == Core.sProgram) {
            // Lexico(token)
            token = runLexic();

            if (token.getSymbol() == Core.sIdentifier) {
                // Lexico(token)
                token = runLexic();

                if (token.getSymbol() == Core.sSemicolon) {
                    analyzeBlock();
                    if (token.getSymbol() == Core.sDot) {
                        // Lexico(token)
                        token = runLexic();

                        // se acabou o arquivo ou é comentario
                        if (token.getSymbol() == 7 && token.getLexeme().equals("7")) {
                            // SUCESSOOOOOOOOOO
                        } else {
                            // erro, esperava-se o fim do arquivo.
                        }
                    } else {
                        // erro, esperava-se o simbolo de ponto
                    }
                } else {
                    // erro, esperava-se o simbolo ponto e virgula
                }
            } else {
                // erro, esperava-se o simbolo de um identificador
            }
        } else {
            // erro, esperava-se o simbolo programa
        }

        System.out.println(core.getToken().getLexeme());
        //        } while (core.getToken() != null);
    }

    private void analyzeBlock() {
        // Lexico(token)
        token = runLexic();

        analyzeVarDeclarations();
        analyzeSubRoutines();
        analyzeCommands();
    }

    private void analyzeCommands() {
        if (token.getSymbol() == Core.sBegin) {
            token = runLexic();
            analyzeSimpleCommand();
            while (token.getSymbol() != Core.sEnd) {
                if (token.getSymbol() == Core.sSemicolon) {
                    token = runLexic();
                    if (token.getSymbol() != Core.sEnd) {
                        analyzeSimpleCommand();
                    }
                } else {
                    // erro, esperava-se um ponto e virgula depois de um comando
                }
            }
            token = runLexic();
        } else {
            // erro, esperava-se o simbolo de inicio
        }
    }

    private void analyzeSimpleCommand() {
        switch (token.getSymbol()) {
        case Core.sIdentifier:
            analyzeAttrAndProcCall();
            break;
        case Core.sIf:
            analyzeIf();
            break;
        case Core.sWhile:
            analyzeWhile();
            break;
        case Core.sRead:
            analyzeRead();
            break;
        case Core.sWrite:
            analyzeWrite();
            break;
        default:
            analyzeCommands();
        }
    }

    private void analyzeWrite() {
        token = runLexic();
        if (token.getSymbol() == Core.sOpenParentheses) {
            token = runLexic();
            if (token.getSymbol() == Core.sIdentifier) {
                token = runLexic();
                if (token.getSymbol() == Core.sCloseParentheses) {
                    token = runLexic();
                } else {
                    // erro, era esperado um fecha parenteses depois do identificador
                }
            } else {
                // erro, era esperado um identificador depois do abre parenteses
            }
        } else {
            // erro, era esperado um abre parenteses depois do write
        }
    }

    private void analyzeRead() {
        token = runLexic();
        if (token.getSymbol() == Core.sOpenParentheses) {
            token = runLexic();
            if (token.getSymbol() == Core.sIdentifier) {
                token = runLexic();
                if (token.getSymbol() == Core.sCloseParentheses) {
                    token = runLexic();
                } else {
                    // erro, era esperado um fecha parenteses
                }
            } else {
                // erro, era esperado um identificador como parametro do read
            }
        } else {
            // erro, era esperado um abre parenteses na chamada do read
        }
    }

    private void analyzeWhile() {
        token = runLexic();
        analyzeExpression();
        if (token.getSymbol() == Core.sDo) {
            token = runLexic();
            analyzeSimpleCommand();
        } else {
            // erro, esperava do depois do while
        }
    }

    private void analyzeIf() {
        token = runLexic();
        analyzeExpression();
        if (token.getSymbol() == Core.sThen) {
            token = runLexic();
            analyzeSimpleCommand();
            if (token.getSymbol() == Core.sElse) {
                token = runLexic();
                analyzeSimpleCommand();
            }
        } else {
            // erro, esperava entao depois do if
        }
    }

    private void analyzeExpression() {
        analyzeSimpleExpression();
        if (token.getSymbol() == Core.sGreaterThan || token.getSymbol() == Core.sGreaterThanEq ||
                token.getSymbol() == Core.sEquals || token.getSymbol() == Core.sLessThan ||
                token.getSymbol() == Core.sLessThanEq || token.getSymbol() == Core.sNotEq) {
            token = runLexic();
            analyzeSimpleExpression();
        }
    }

    private void analyzeSimpleExpression() {
        if (token.getSymbol() == Core.sPlus || token.getSymbol() == Core.sMinus) {
            token = runLexic();
        }
        analyzeTerm();

        while (token.getSymbol() == Core.sPlus || token.getSymbol() == Core.sMinus || token.getSymbol() == Core.sOr) {
            token = runLexic();
            analyzeTerm();
        }
    }

    private void analyzeTerm() {
        analyzeFactor();
        while (token.getSymbol() == Core.sTimes || token.getSymbol() == Core.sDiv || token.getSymbol() == Core.sIf) {
            token = runLexic();
            analyzeFactor();
        }
    }

    private void analyzeFactor() {
        if (token.getSymbol() == Core.sIdentifier) {
            analyzeFunctionCall();
        } else if (token.getSymbol() == Core.sNumber) {
            token = runLexic();
        } else if (token.getSymbol() == Core.sNot) {
            token = runLexic();
            analyzeFactor();
        } else if (token.getSymbol() == Core.sOpenParentheses) {
            token = runLexic();
            analyzeExpression();
            if (token.getSymbol() == Core.sCloseParentheses) {
                token = runLexic();
            } else {
                // erro, esperava fecha parenteses depois da expressao
            }
        } else if (token.getLexeme().equals("verdadeiro") || token.getLexeme().equals("falso")) {
            token = runLexic();
        } else {
            // erro, blablbla
        }
    }

    private void analyzeFunctionCall() {
        token = runLexic();
    }

    private void analyzeAttrAndProcCall() {
        token = runLexic();
        if (token.getSymbol() == Core.sAttribution) {
            analyzeAttribution();
        } else {
            analyzeProcedureCall();
        }
    }

    private void analyzeProcedureCall() {
        if (token.getSymbol() != Core.sIdentifier) {
            // erro, esperava identificador na chamada do procedimento
        }
    }

    private void analyzeAttribution() {
        token = runLexic();

        if (token.getSymbol() != Core.sIdentifier) {
            // erro, esperava identificador na chamada do procedimento
        } else {
            token = runLexic();
            if (token.getSymbol() == Core.sAttribution) {
                analyzeExpression();
            } else {
                // erro, esperava-se um '=' na atribuicao
            }
        }
    }

    private void analyzeSubRoutines() {
        int flag = 0;
        if (token.getSymbol() == Core.sProcedure || token.getSymbol() == Core.sFunction) {

        }

        while (token.getSymbol() == Core.sProcedure || token.getSymbol() == Core.sFunction) {
            if (token.getSymbol() == Core.sProcedure) {
                analyzeProcedureDeclaration();
            } else {
                analyzeFunctionDeclaration();
            }
            if (token.getSymbol() == Core.sSemicolon) {
                token = runLexic();
            } else {
                // erro, esperado ponto e virgula depois de chamada de funcao/procedimento
            }
        }

        if (flag == 1) {

        }
    }

    private void analyzeFunctionDeclaration() {
        token = runLexic();
        if (token.getSymbol() == Core.sIdentifier) {
            token = runLexic();
            if (token.getSymbol() == Core.sColon) {
                token = runLexic();
                if (token.getSymbol() == Core.sInteger || token.getSymbol() == Core.sBoolean) {
                    token = runLexic();
                    if (token.getSymbol() == Core.sSemicolon) {
                        analyzeBlock();
                    }
                } else {
                    // erro, esperava tipo como inteiro ou booleano
                }
            } else {
                // erro, esperava dois pontos
            }
        } else {
            // erro, esperava identificador
        }
    }

    private void analyzeProcedureDeclaration() {
        token = runLexic();
        if (token.getSymbol() == Core.sIdentifier) {
            token = runLexic();
            if (token.getSymbol() == Core.sSemicolon) {
                analyzeBlock();
            } else {
                // erro, esperava ponto e virgula depois do identificador
            }
        } else {
            // erro, esperava identificador
        }
    }

    private void analyzeVarDeclarations() {
        if (token.getSymbol() == Core.sVar) {
            token = runLexic();

            if (token.getSymbol() == Core.sIdentifier) {
                while (token.getSymbol() == Core.sIdentifier) {
                    analyzeVars();
                    if (token.getSymbol() == Core.sSemicolon) {
                        token = runLexic();
                    } else {
                        // erro, esperava-se ponto e virgula depois da decl de var
                    }
                }
            } else {
                // erro, esperava-se a declaracao de ao menos uma variavel
            }
        }
    }

    private void analyzeVars() {
        do {
            if (token.getSymbol() == Core.sIdentifier) {
                token = runLexic();
                if (token.getSymbol() == Core.sComma || token.getSymbol() == Core.sColon) {
                    if (token.getSymbol() == Core.sComma) {
                        token = runLexic();
                        if (token.getSymbol() == Core.sColon) {
                            // erro, nao e permitido dois pontos depois da virgula?
                        }
                    }
                } else {
                    // erro, esperava-se virgula ou dois pontos
                }
            } else {
                // erro, esperava-se um identificador
            }
        } while (token.getSymbol() != Core.sColon);

        token = runLexic();
        analyzeType();
    }

    private void analyzeType() {
        if (token.getSymbol() != Core.sInteger && token.getSymbol() != Core.sBoolean) {
            // erro, esperava-se inteiro ou booleano como tipo
        }

        token = runLexic();
    }

    private Token runLexic() {
        try {
            core.getNextToken();
            synchronized (this) {
                wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return core.getToken();
    }

    @Override
    public void run() {
        analyze();
    }

    public void setCore(Core c) {
        core = c;
    }
}
