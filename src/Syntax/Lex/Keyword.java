package Syntax.Lex;

public class Keyword extends Token {
	public static final String 
		IF="if",		THEN="then",		ELSE="else",
		WHILE="while",	DO="do";
	
	final String value;
	
	public Keyword(String v) {
		super(v,Tag.KEYWORD);
		value=v;
	}
	
	public String getValue(){return value;}
	
	public String toTerminal(){return value;}
	
	public static Token isKeyword(String s){
		switch(s){
		case IF:return new Keyword(IF);
		case THEN:return new Keyword(THEN);
		case ELSE:return new Keyword(ELSE);
		case DO:return new Keyword(DO);
		case WHILE:return new Keyword(WHILE);
		default:return new Token(Tag.ERROR);
		}
	}
}
