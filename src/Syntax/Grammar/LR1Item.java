package Syntax.Grammar;

import Syntax.Grammar.GrammarTable.Nonterminal;

/**
 * 
 * 继承了SLR集合
 *
 */
public class LR1Item extends SLRItem{
	public final String real_end;
	
	public LR1Item(Nonterminal l, String[] r, int pl, String end) {
		super(l, r, pl);
		real_end=end;
		// TODO Auto-generated constructor stub
	}
	
	public String toString() {
		String tmp = new String(left.TAG);
		tmp += "->";
		for (int i = 0; i < right.length; ++i) {
			if (i == prefix_length)
				tmp += ".";
			tmp += right[i];
		}
		if (prefix_length == right.length) {
			tmp += ".";
		}
		if(real_end!=null&&real_end!=""){
			tmp+=","+real_end;
		}
		return tmp;
	}
}
