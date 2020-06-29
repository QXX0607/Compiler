package Syntax.Lex;

public class Id extends Token {
	final String name;

	public Id(String v) {
		super(v,Tag.ID);
		name=v;
	}
	
	public String getValue(){return name;}
	
	public String toTerminal(){return tag;}
}
