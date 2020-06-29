package Syntax.Lex;

public class Operator extends Token {
	public final static String 
	PLUS="+",		MINUS="-",		MULT="*",		DIV="/",
	LESS="<",		MORE=">",		ASSIGN="=",		LPAR="(",
	RPAR=")",		LNND=";",		SIQUO="'",		EQU="==",
	NLES=">=",		NMRE="<=",		NEQU="!=",		LBPAR="{",
	RBPAR="}";		
	
	final String value;
	public Operator(String v) {
		super(v,Tag.OP);
		// TODO Auto-generated constructor stub
		value=v;
	}
	
	public String getValue(){return value;}
	
	public String toTerminal(){return getValue();}
	
	public static Token isOperator(String s){
		switch(s){
		case EQU:
		case NLES:
		case NMRE:
		case NEQU:return new Operator(s);
		}
		
		String one=s.substring(0, 1);
		switch(one){
		case PLUS:
		case MINUS:
		case MULT:
		case DIV:
		case LESS:
		case MORE:
		case ASSIGN:
		case LPAR:
		case RPAR:
		case LNND:
		case SIQUO:
		case LBPAR:
		case RBPAR:return new Operator(one);
		default:return new Token(Tag.ERROR);
		}
	}
	
	public int width(){
		switch(value){
		case EQU:
		case NLES:
		case NMRE:
		case NEQU:return 2;
		default:return 1;
		}
	}
}
