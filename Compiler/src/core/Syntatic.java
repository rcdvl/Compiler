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
    private final Semantic semantic;
    private final CodeGenerator codeGenerator;
    private int label;
    private int currentAddress = 0;
    private int varDeclCount;
    private String unaryToken;

    private Syntatic() {
    	Semantic.newInstance();
        semantic = Semantic.getInstance();
        CodeGenerator.newInstance();
        codeGenerator = CodeGenerator.getInstance();
        codeGenerator.clearFile();
        label = 1;
    }

    public static Syntatic getInstance() {
        if (instance == null) {
        	newInstance();
        }

        return instance;
    }
    
    public static void newInstance() {
    	instance = new Syntatic();
    }

    public void analyze() {
        //        do {
        // Lexico(token)
        try {
            label = 1;
            token = runLexic();

            if (token.getSymbol() == Lexic.sProgram) {
                // Lexico(token)
                token = runLexic();

                if (token.getSymbol() == Lexic.sIdentifier) {
                    // insere_tabela(token.lexema, "nomedoprograma", "", "")
                    SymbolsTableEntry ste = new SymbolsTableEntry();
                    ste.lexeme = token.getLexeme();
                    ste.type = SymbolsTableEntry.PROGRAM_NAME_TYPE;
                    ste.level = SymbolsTableEntry.SCOPE_MARK;
                    semantic.addSymbolToTable(ste);
                    codeGenerator.generate("    ", "START", "    ", "    ");
                    // Lexico(token)
                    token = runLexic();

                    if (token.getSymbol() == Lexic.sSemicolon) {
                        analyzeBlock();
                        if (token.getSymbol() == Lexic.sDot) {
                            // Lexico(token)
                            token = runLexic();
                            codeGenerator.generate("    ", "DALLOC", 0, currentAddress);
                            codeGenerator.generate("    ", "HLT", "    ", "    ");

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
        } catch (final CompileErrorException cee) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JTextArea codeArea = window.getCodeArea();
                    DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);

                    try {
                        codeArea.getHighlighter().addHighlight(codeArea.getLineStartOffset(lexic.lastLine-2), codeArea.getLineEndOffset(lexic.lastLine-2), painter);
                        JTextArea errorArea = window.getErrorArea();
                        errorArea.setText(cee.getMessage());
                        codeArea.requestFocus();
                        codeArea.setCaretPosition(codeArea.getDocument().getDefaultRootElement().getElement(lexic.lastLine-2).getStartOffset());
                    	window.jLabelCompilar.setText("Ocorreram erros na compilacao.");
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

        varDeclCount = 0;
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
        int symbolIndex;
        token = runLexic();
        if (token.getSymbol() == Lexic.sOpenParentheses) {
            token = runLexic();
            if (token.getSymbol() == Lexic.sIdentifier) {
                // se pesquisa_ declvarfunc_tabela(token.lexema)
                // então
                if ((symbolIndex = semantic.searchForDeclaration(token.getLexeme())) != -1) {
                	SymbolsTableEntry ste = semantic.get(symbolIndex);
                	if (ste.varType != SymbolsTableEntry.VAR_TYPE_INTEGER && ste.type != SymbolsTableEntry.INTEGER_FUNCTION_TYPE) {
                		throw new CompileErrorException("erro semantico, so e possivel escrever variaveis e funcoes inteiras", lexic.lineNumber);
                	}
                	if (ste.type == SymbolsTableEntry.INTEGER_FUNCTION_TYPE) {
                		analyzeFunctionCall();
                		System.out.println("RETURN ADDRESS DA " + ste.label + ": " + ste.returnAddress);
                        codeGenerator.generate("", "LDV", ste.returnAddress, "");
	                    codeGenerator.generate("", "PRN", "", "");
                	} else {
	                    codeGenerator.generate("", "LDV", semantic.get(symbolIndex).address, "");
	                    codeGenerator.generate("", "PRN", "", "");
	                    token = runLexic();
                	}
                    if (token.getSymbol() == Lexic.sCloseParentheses) {
                        token = runLexic();
                    } else {
                        throw new CompileErrorException("erro, era esperado um fecha parenteses depois do identificador", lexic.lineNumber);
                    }
                } else {
                    throw new CompileErrorException("erro semantico, identificador não declarado", lexic.lineNumber);
                    // senao erro
                }
            } else {
                throw new CompileErrorException("erro, era esperado um identificador depois do abre parenteses", lexic.lineNumber);
            }
        } else {
            throw new CompileErrorException("erro, era esperado um abre parenteses depois do write", lexic.lineNumber);
        }
    }

    private void analyzeRead() throws CompileErrorException {
        int symbolIndex;
        token = runLexic();
        if (token.getSymbol() == Lexic.sOpenParentheses) {
            token = runLexic();
            if (token.getSymbol() == Lexic.sIdentifier) {
                // se pesquisa_declvar_tabela(token.lexema)
                // então início (pesquisa em toda a tabela)
                if ((symbolIndex = semantic.searchForDeclaration(token.getLexeme())) != -1) {
                	SymbolsTableEntry ste = semantic.get(symbolIndex);
                	if (ste.varType != SymbolsTableEntry.VAR_TYPE_INTEGER) {
                		throw new CompileErrorException("erro semantico, so e possivel ler variaveis inteiras", lexic.lineNumber);
                	}
                    codeGenerator.generate("", "RD", "", "");
                    codeGenerator.generate("", "STR", semantic.get(symbolIndex).address, "");
                    token = runLexic();
                    if (token.getSymbol() == Lexic.sCloseParentheses) {
                        token = runLexic();
                    } else {
                        throw new CompileErrorException("erro, era esperado um fecha parenteses", lexic.lineNumber);
                    }
                } else {
                    // senao erro
                    throw new CompileErrorException("erro semantico, identificador nao declarado", lexic.lineNumber);
                }
            } else {
                throw new CompileErrorException("erro, era esperado um identificador como parametro do read", lexic.lineNumber);
            }
        } else {
            throw new CompileErrorException("erro, era esperado um abre parenteses na chamada do read", lexic.lineNumber);
        }
    }

    private void analyzeWhile() throws CompileErrorException {
        int firstLabelAux, secondLabelAux;
        firstLabelAux = label;
        System.out.println("null que indica o comeco do while: " + label);
        codeGenerator.generate(label, "NULL", "    ", "    ");
        label++;
        token = runLexic();
        analyzeExpression();
        semantic.analyzeExpression();
        semantic.clearEquation();
        if (!semantic.isBooleanExpression) {
        	throw new CompileErrorException("erro, esperava uma expressao booleana no while", lexic.lineNumber);
        }
        
        if (token.getSymbol() == Lexic.sDo) {
            secondLabelAux = label;
            System.out.println("jmpf pra sair do while, vai para " + secondLabelAux);
            codeGenerator.generate("    ", "JMPF", label, "    ");
            label++;
            token = runLexic();
            analyzeSimpleCommand();
            System.out.println("jmp pra voltar pro while, vai para " + firstLabelAux);
            codeGenerator.generate("    ", "JMP ", firstLabelAux, "    ");
            System.out.println("null pra sair do while, vai para " + secondLabelAux);
            codeGenerator.generate(secondLabelAux, "NULL", "    ", "    ");
        } else {
            throw new CompileErrorException("erro, esperava faca depois do enquanto", lexic.lineNumber);
        }
    }

    private void analyzeIf() throws CompileErrorException {
        int labelAux = label;
        token = runLexic();
        analyzeExpression();

        semantic.analyzeExpression();
        semantic.clearEquation();
        if (!semantic.isBooleanExpression) {
        	throw new CompileErrorException("erro, esperava uma expressao booleana no if", lexic.lineNumber);
        }
        
        codeGenerator.generate("", "JMPF", label, "");
        label++;
        if (token.getSymbol() == Lexic.sThen) {
            token = runLexic();
            analyzeSimpleCommand();
            codeGenerator.generate("", "JMP", label, "");
            codeGenerator.generate(labelAux, "NULL", "", "");
            if (token.getSymbol() == Lexic.sElse) {
                token = runLexic();
                analyzeSimpleCommand();
            }
            codeGenerator.generate(label, "NULL", "", "");
            label++;
        } else {
            throw new CompileErrorException("erro, esperava entao depois do if", lexic.lineNumber);
        }
    }

    private void analyzeExpression() throws CompileErrorException {
        analyzeSimpleExpression();
        if (token.getSymbol() == Lexic.sGreaterThan || token.getSymbol() == Lexic.sGreaterThanEq ||
                token.getSymbol() == Lexic.sEquals || token.getSymbol() == Lexic.sLessThan ||
                token.getSymbol() == Lexic.sLessThanEq || token.getSymbol() == Lexic.sNotEq) {
            semantic.addToEquation(token.getLexeme());
            token = runLexic();
            analyzeSimpleExpression();
        }
    }

    private void analyzeSimpleExpression() throws CompileErrorException {
        unaryToken = "";
        if (token.getSymbol() == Lexic.sPlus || token.getSymbol() == Lexic.sMinus) {
            unaryToken = "uinv";
            //semantic.addToEquation(token.getLexeme());
            token = runLexic();
        }
        analyzeTerm();

        unaryToken = "";
        while (token.getSymbol() == Lexic.sPlus || token.getSymbol() == Lexic.sMinus || token.getSymbol() == Lexic.sOr) {
            semantic.addToEquation(token.getLexeme());
            token = runLexic();
            analyzeTerm();
        }
    }

    private void analyzeTerm() throws CompileErrorException {
        analyzeFactor();
        while (token.getSymbol() == Lexic.sTimes || token.getSymbol() == Lexic.sDiv || token.getSymbol() == Lexic.sAnd) {
            semantic.addToEquation(token.getLexeme());
            token = runLexic();
            analyzeFactor();
        }
    }

    private void analyzeFactor() throws CompileErrorException {
        if (token.getSymbol() == Lexic.sIdentifier) {
            // Se pesquisa_tabela(token.lexema,nível,ind)
            // Então Se (TabSimb[ind].tipo = “função inteiro”) ou
            // (TabSimb[ind].tipo = “função booleano”)
            // Então
            int index;
            if ((index = semantic.searchForDeclaration(token.getLexeme())) != -1) {
                SymbolsTableEntry entry = semantic.get(index);
                if (entry.type == SymbolsTableEntry.INTEGER_FUNCTION_TYPE || entry.type == SymbolsTableEntry.BOOLEAN_FUNCTION_TYPE) {
                    semantic.addToEquation(token.getLexeme());
                    analyzeFunctionCall();
                } else {
                    semantic.addToEquation(token.getLexeme());
                    token = runLexic();
                }
            } else {
                throw new CompileErrorException("erro semantico, identificador nao declarado", lexic.lineNumber);
            }
            // Senão Léxico(token)
            // Senão ERRO
        } else if (token.getSymbol() == Lexic.sNumber) {
            if (unaryToken.isEmpty()) {
                semantic.addToEquation(token.getLexeme());
            } else {
                semantic.addToEquation(token.getLexeme());
                semantic.addToEquation(unaryToken);
            }
            token = runLexic();
        } else if (token.getSymbol() == Lexic.sNot) {
            semantic.addToEquation(token.getLexeme());
            token = runLexic();
            analyzeFactor();
        } else if (token.getSymbol() == Lexic.sOpenParentheses) {
            semantic.addToEquation(token.getLexeme());
            token = runLexic();
            analyzeExpression();
            if (token.getSymbol() == Lexic.sCloseParentheses) {
                semantic.addToEquation(token.getLexeme());

//                semantic.analyzeExpression();
                //semantic.clearEquation();
                token = runLexic();
            } else {
                throw new CompileErrorException("erro, esperava fecha parenteses depois da expressao", lexic.lineNumber);
            }
        } else if (token.getLexeme().equals("verdadeiro") || token.getLexeme().equals("falso")) {
            semantic.addToEquation(token.getLexeme().equals("verdadeiro") ? "1" : "0");
            token = runLexic();
        } else {
            throw new CompileErrorException("erro, blablbla", lexic.lineNumber);
        }
    }

    private void analyzeFunctionCall() throws CompileErrorException {
    	if (semantic.searchForDeclaration(token.getLexeme()) == -1) {
            throw new CompileErrorException("erro semantico... funcao nao foi declarada", lexic.lineNumber);
        }

    	SymbolsTableEntry ste = semantic.get(semantic.searchForDeclaration(token.getLexeme()));
        codeGenerator.generate("    ", "CALL ", ste.label, "    ");
        token = runLexic();
    }

    private void analyzeAttrAndProcCall() throws CompileErrorException {
        if (semantic.searchForDeclaration(token.getLexeme()) == -1) {
            throw new CompileErrorException("erro semantico... token nao foi declarado", lexic.lineNumber);
        }
        Token oldToken = token;
        token = runLexic();
        if (token.getSymbol() == Lexic.sAttribution) {
            analyzeAttribution();
            
	        SymbolsTableEntry ste = semantic.get(semantic.searchForDeclaration(oldToken.getLexeme()));
	        if (ste.level == SymbolsTableEntry.SCOPE_MARK && semantic.getCurrentScope().equals(oldToken.getLexeme())) {
        		System.out.println("RETURN ADDRESS DA " + ste.label + ": " + ste.returnAddress);
	        	codeGenerator.generate("", "STR", ste.returnAddress, "");
	        } else {
	        	codeGenerator.generate("", "STR", semantic.get(semantic.searchForDeclaration(oldToken.getLexeme())).address, "");
	        }
            
        } else {
            analyzeProcedureCall(oldToken);
        }
    }

    private void analyzeProcedureCall(Token token) throws CompileErrorException {
    	if (semantic.searchForDeclaration(token.getLexeme()) == -1) {
            throw new CompileErrorException("erro semantico... procedimento nao foi declarado", lexic.lineNumber);
        }
        codeGenerator.generate("    ", "CALL ", semantic.get(semantic.searchForDeclaration(token.getLexeme())).label, "    ");
        if (token.getSymbol() != Lexic.sIdentifier) {
            throw new CompileErrorException("erro, esperava identificador na chamada do procedimento", lexic.lineNumber);
        }
    }

    private void analyzeAttribution() throws CompileErrorException {
        token = runLexic();

        analyzeExpression();
        semantic.analyzeExpression();
        semantic.clearEquation();
    }

    private void analyzeSubRoutines() throws CompileErrorException {
        int labelAux;
        int varDeclBackup = varDeclCount;

        labelAux = label;
        System.out.println("jmp de subrotina, vai para " + label);
        codeGenerator.postponeGenerate("    ", "JMP ", label, "    ");
        label++;
        while (token.getSymbol() == Lexic.sProcedure || token.getSymbol() == Lexic.sFunction) {
            if (token.getSymbol() == Lexic.sProcedure) {
                codeGenerator.doPostponeGenerate(null, null, -1, 1);
                analyzeProcedureDeclaration();
                varDeclCount = varDeclBackup;
            } else {
                codeGenerator.doPostponeGenerate("", "ALLOC ", currentAddress, 1);
                currentAddress++;
                varDeclBackup++;
                analyzeFunctionDeclaration(currentAddress - 1);
                varDeclCount = varDeclBackup;
            }
            if (token.getSymbol() == Lexic.sSemicolon) {
                token = runLexic();
            } else {
                throw new CompileErrorException("erro, esperado ponto e virgula depois de chamada de funcao/procedimento", lexic.lineNumber);
            }
        }

        System.out.println("null de subrotina " + labelAux);
        codeGenerator.generate(labelAux, "NULL", "    ", "    ");
    }

    private void analyzeFunctionDeclaration(int returnAddress) throws CompileErrorException {
        token = runLexic();
        if (token.getSymbol() == Lexic.sIdentifier) {
            // pesquisa_declfunc_tabela(token.lexema)
            // se não encontrou
            // então início
            //     Insere_tabela(token.lexema,””,nível,rótulo)
            if (semantic.searchForDeclaration(token.getLexeme()) == -1) {
                SymbolsTableEntry ste = new SymbolsTableEntry();
                ste.lexeme = token.getLexeme();
                ste.type = SymbolsTableEntry.UNDEFINED;
                ste.level = SymbolsTableEntry.SCOPE_MARK;
                ste.label = label;
                ste.address = currentAddress;
                ste.returnAddress = returnAddress;
                semantic.addSymbolToTable(ste);
                // TODO - colocar o endereco da funcao na ste e gerar o null com a label e atualizar a label

                codeGenerator.generate(label, "NULL", "    ", "    ");
                label++;
                
                token = runLexic();
                if (token.getSymbol() == Lexic.sColon) {
                    token = runLexic();
                    if (token.getSymbol() == Lexic.sInteger || token.getSymbol() == Lexic.sBoolean) {
                        // se (token.símbolo = Sinteger)
                        // então TABSIMB[pc].tipo:= 'função inteiro'
                        // senão TABSIMB[pc].tipo:= 'função boolean'
                        if (token.getSymbol() == Lexic.sInteger) {
                            ste.type = SymbolsTableEntry.INTEGER_FUNCTION_TYPE;
                        } else {
                            ste.type = SymbolsTableEntry.BOOLEAN_FUNCTION_TYPE;
                        }

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
                throw new CompileErrorException("erro semantico, identificador ja declarado", lexic.lineNumber);
                // senão ERRO
            }
        } else {
            throw new CompileErrorException("erro, esperava identificador", lexic.lineNumber);
        }

        semantic.popUntilScope();
        codeGenerator.generate("    ", "DALLOC", currentAddress - varDeclCount, varDeclCount);
        codeGenerator.generate("    ", "RETURN", "    ", "    ");
        currentAddress = currentAddress - varDeclCount;
    }

    private void analyzeProcedureDeclaration() throws CompileErrorException {
        token = runLexic();
        if (token.getSymbol() == Lexic.sIdentifier) {
            // pesquisa_declproc_tabela(token.lexema)
            // se não encontrou
            // então início
            // Insere_tabela(token.lexema,”procedimento”,nível, rótulo)
            if (semantic.searchForDeclaration(token.getLexeme()) == -1) {
                SymbolsTableEntry ste = new SymbolsTableEntry();
                ste.lexeme = token.getLexeme();
                ste.type = SymbolsTableEntry.PROCEDURE_TYPE;
                ste.level = SymbolsTableEntry.SCOPE_MARK;
                ste.label = label;
                ste.address = currentAddress;
                semantic.addSymbolToTable(ste);

                System.out.println("label do procedimento que o call vai buscar " + label);
                codeGenerator.generate(label, "NULL", "    ", "    ");
                label++;

                token = runLexic();
                if (token.getSymbol() == Lexic.sSemicolon) {
                    analyzeBlock();
                } else {
                    throw new CompileErrorException("erro, esperava ponto e virgula depois do identificador", lexic.lineNumber);
                }
            } else {
                // fim
                // senão ERRO
                throw new CompileErrorException("erro semantico, identificador não declarado", lexic.lineNumber);
            }
        } else {
            throw new CompileErrorException("erro, esperava identificador", lexic.lineNumber);
        }
        semantic.popUntilScope();
        codeGenerator.generate("    ", "DALLOC", currentAddress - varDeclCount, varDeclCount);
        codeGenerator.generate("    ", "RETURN", "    ", "    ");
        currentAddress = currentAddress - varDeclCount;
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
        int varCount = 0;
        do {
            if (token.getSymbol() == Lexic.sIdentifier) {
                // Pesquisa_duplicvar_ tabela(token.lexema)
                // se não encontrou duplicidade
                // então início
                // insere_tabela(token.lexema, "variável")
                if (!semantic.findDuplicateInTable(token.getLexeme())) {
                    SymbolsTableEntry ste = new SymbolsTableEntry();
                    ste.lexeme = token.getLexeme();
                    ste.type = SymbolsTableEntry.VAR_TYPE;
                    ste.varType = SymbolsTableEntry.UNDEFINED;
                    ste.address = currentAddress + varCount;
                    semantic.addSymbolToTable(ste);

                    varCount++;
                    varDeclCount++;

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
                    throw new CompileErrorException("erro semantico, identificador duplicado", lexic.lineNumber);
                }
                // fim
                // senao erro
            } else {
                throw new CompileErrorException("erro, esperava-se um identificador", lexic.lineNumber);
            }
        } while (token.getSymbol() != Lexic.sColon);
        codeGenerator.generate("", "ALLOC ", currentAddress, varCount);
        currentAddress += varCount;

        token = runLexic();
        analyzeType();
    }

    private void analyzeType() throws CompileErrorException {
        if (token.getSymbol() != Lexic.sInteger && token.getSymbol() != Lexic.sBoolean) {
            throw new CompileErrorException("erro, esperava-se inteiro ou booleano como tipo", lexic.lineNumber);
        } else {
            semantic.insertTypeOnVars(token.getLexeme());
            // senao coloca_tipo_tabela(token.lexema)
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
