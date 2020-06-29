package Syntax.Lex;


import MainFunction.*;
import Syntax.Grammar.SymbolTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
	public static final String TAG = Lexer.class.getSimpleName();

	char HLPR_BLNK = ' ', HLPR_END = '\n';

	String srcBuffer = "";
	int srcLength = 0;
	String current = "";
	SymbolTable symbolTable = null;

	int cursor = 0;

	List<Token> pickedTokens;

	public Lexer(File f) throws IOException {
		this(f, null);
	}

	public Lexer(File f, SymbolTable st) throws IOException {
		// Log.s(TAG,"source file length",f.length()+"");
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String tmp;
		while (true) {
			tmp = reader.readLine();
			if (tmp == null)
				break;
			srcBuffer += tmp;
			if (!tmp.substring(tmp.length() - 1).equals(";"))
				srcBuffer += HLPR_END;
		}
		// Log.s(TAG, "content",srcBuffer);
		srcLength = srcBuffer.length();
		pickedTokens = new ArrayList<Token>();
		reader.close();
		symbolTable = st;
	}

	public boolean parse() {
		// boolean errFlag=false;
		NFA nfa = new NFA();
		boolean result=true;
		while (cursor < srcLength) {
			Token picked = nfa.parseToken();
			switch (picked.getTag()) {
			case Tag.ERROR:
				result = false;
			case Tag.HLPCHR:
				continue;
			default:
				pickedTokens.add(picked);
				break;
			}
		}
		return result;
	}

	public void printTokens() {
		for (Token token : pickedTokens) {
			Log.s(TAG, token.getTag() + "/" + token.getValue());
		}
	}

	public String printTokensToString() {
		String result = "";
		for (Token token : pickedTokens) {
			// Log.s(TAG, token.getTag()+"/"+token.getValue());
			result += token.getTag() + "/" + token.getValue() + "\n";
		}
		return result;
	}

	public List<Token> getTokens() {
		return pickedTokens;
	}

	public class NFA {
		public static final String TAG = "NFA";

		public static final String STATE_DECIMAL = "state_decimal";
		public static final String STATE_WORD = "state_word";
		public static final String STATE_START = "state_start";
		public static final String STATE_END = "state_end";

		public Token parseToken() {
			String curState = STATE_START;
			String value = "";
			while (true) {
				if (cursor == srcLength) {
					Log.s(TAG, "end of file!");
					return new Token(Tag.ERROR);
				}
				char head = srcBuffer.charAt(cursor);
				switch (curState) {
				case STATE_START:
					if (Decimal.isDecimal(head)) {
						curState = STATE_DECIMAL;
						value += head;
					} else if (Letter.isLetter(head)) {
						curState = STATE_WORD;
						value += head;
					} else {
						String tmp = "";
						if (cursor < srcLength - 1)
							tmp = srcBuffer.substring(cursor, cursor + 2);
						else
							tmp = srcBuffer.substring(cursor, cursor + 1);
						Token token = containsOperator(tmp);
						if (token.getTag() != Tag.ERROR) {
							Operator op = (Operator) token;
							cursor += op.width();
							return op;
						} else if (isHelperChar(head) || head == '\t') {
							cursor++;
							return new Token(Tag.HLPCHR);
						} else {
							Log.s(TAG, "unknown char " + head);
							return new Token(Tag.ERROR);
						}
					}
					break;
				case STATE_DECIMAL:
					if (containsOperator(srcBuffer.substring(cursor, cursor + 1)).getTag() != Tag.ERROR) {
						return new Int10(Integer.parseInt(value));
					}
					if (Decimal.isDecimal(head)) {
						value += head;
					} else {
						Log.s(TAG, STATE_WORD, "unknown char '" + head + "' after '" + value + "'");
						return new Token(Tag.ERROR);
					}
					break;
				case STATE_WORD:
					if (isHelperChar(head)) {
						Token token = Keyword.isKeyword(value);
						if (token.getTag() != Tag.ERROR) {
							return token;
						}
						token = Type.isType(value);
						if (token.getTag() != Tag.ERROR) {
							return token;
						}
						Log.s(TAG, STATE_WORD, "wrong word '" + value + "'");
						return new Token(Tag.ERROR);
					}
					if (containsOperator(srcBuffer.substring(cursor, cursor + 1)).getTag() != Tag.ERROR) {
						Token token = Keyword.isKeyword(value);
						if (token.getTag() != Tag.ERROR) {
							return token;
						}
						return new Id(value);
					}
					if (Decimal.isDecimal(head) || Letter.isLetter(head)) {
						value += head;
					} else {
						Log.s(TAG, STATE_WORD, "unknown char '" + head + "' after '" + value + "'");
						return new Token(Tag.ERROR);
					}
					break;
				}
				cursor++;
			}
		}

		public Token containsOperator(String src) {
			if (src.length() > 2)
				src = src.substring(0, 1);

			return Operator.isOperator(src);
			// Token token=Operator.isOperator(src);
			// if(token.getTag()!=Tag.ERROR)
			// return token;
			//
			// token=Operator.isOperator(src.substring(0,0));
			// return token;
		}

		public boolean isHelperChar(char c) {
			if (c == HLPR_BLNK || c == HLPR_END)
				return true;
			else
				return false;
		}
	}
}
