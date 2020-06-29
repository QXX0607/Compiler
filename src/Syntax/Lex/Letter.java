package Syntax.Lex;

public class Letter {

	public Letter() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean isLetter(char c){
		if(c>='a'&&c<='z'){
			return true;
		}else if(c>='A'&&c<='Z'){
			return true;
		}else{
			return false;
		}
	}

}
