package Syntax.Grammar;

import MainFunction.Log;
import Syntax.Grammar.GrammarTable.Nonterminal;

import java.util.HashMap;
import java.util.Map;

public class GotoTable {
	public static final String TAG = GotoTable.class.getSimpleName();

	public Map<Integer, Map<Nonterminal, Integer>> gotoTable;

	public GotoTable() {
		gotoTable = new HashMap<>();
	}

	public void add(int current, Nonterminal non, int next) {
		if (!gotoTable.containsKey(current)) {
			gotoTable.put(current, new HashMap<Nonterminal, Integer>());
		}
		gotoTable.get(current).put(non, next);
	}//加入状态和规约到的元素转换状态符号

	public int go2(int current, Nonterminal non) {
		return gotoTable.get(current).get(non);
		//更具状态和被规约到的非终结符返回状态
	}

	public void printGotoTable() {
		String buff = "";
		for (Integer id : gotoTable.keySet()) {
			buff = "";
			for (Nonterminal non : gotoTable.get(id).keySet()) {
				buff += non.TAG + " " + gotoTable.get(id).get(non) + " | ";
			}
			Log.s(TAG, "Group " + id, buff);
		}
	}

	public String printGotoTableToString() {
		String buff = "";
		String result = TAG + "\n\n";
		for (Integer id : gotoTable.keySet()) {
			buff = "";
			for (Nonterminal non : gotoTable.get(id).keySet()) {
				buff += non.TAG + " " + gotoTable.get(id).get(non) + " | ";
			}
			result += "Group " + id + ":" + buff + "\n";
		}
		return result;
	}
}
