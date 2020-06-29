package Syntax.Lex;

public class Decimal {

	public Decimal() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean isDecimal(char c){
		if(c<'0'||c>'9'){
			return false;
		}else{
			return true;
		}
	}
}
