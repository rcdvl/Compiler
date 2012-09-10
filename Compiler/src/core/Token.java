package core;

public class Token {
	private int symbol;
	private String lexeme;
	
	public Token(int symbol, String lexeme) {
		this.symbol = symbol;
		this.lexeme = lexeme;
	}
	
	public int getSymbol() {
		return symbol;
	}
	
	public void setSymbol(int symbol) {
		this.symbol = symbol;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}	
}
