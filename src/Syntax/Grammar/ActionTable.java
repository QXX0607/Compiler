package Syntax.Grammar;

import MainFunction.Log;

import java.util.HashMap;
import java.util.Map;

public class ActionTable {
	public static final String TAG = ActionTable.class.getSimpleName();

	public static class Action {
		public static final String ACTION_REDUCTION = "规约";
		public static final String ACTION_SHIFTIN = "移入";
		public static final String ACTION_FINISHED="ACC";
		public static final String ACTION_ERROR="error";
		//对应action表四个状态
		public final String mode;
		public final int groupid;
		public final SLRItem gen;
		
		Action(String m){
			this(m,-1);
		}

		Action(String m, int id) {
			mode = m;
			groupid = id;
			gen = null;
		}

		Action(String m, SLRItem g) {
			mode = m;
			groupid = -1;
			gen = g;
		}

		public String toString() {
			String tmp = "";
			switch(mode){
			case ACTION_REDUCTION:
				tmp += "r";//规约状态用r表示，括号内表示产生是
				tmp += "(" + gen.toString() + ")";
				break;
			case ACTION_SHIFTIN:
				tmp += "s";//移入状态。标号表示状态站的转换结果 
				tmp += groupid;
				break;
			default:
				tmp += mode;
				break;
			}
			return tmp;
		}
	}

	Map<Integer, Map<String, Action>> actionTable;

	public ActionTable() {
		actionTable = new HashMap<>();
	}

	public void add(int id, String terminal, int nextid) {
		Action newAction = new Action(Action.ACTION_SHIFTIN, nextid);
		add(id, terminal, newAction);//状态和终结符和下一个状态加入集合表示移入
	}

	public void add(int id, String terminal, SLRItem gen) {
		Action newAction = new Action(Action.ACTION_REDUCTION, gen);
		//加入状态终结符和规约产生是
		add(id, terminal, newAction);
	}
	
	public void add(int id, String terminal){
		add(id,terminal,new Action(Action.ACTION_FINISHED));
	//对应接受状态
	}

	public void add(int id, String terminal, Action newAction) {
		if (!actionTable.containsKey(id)) {
			actionTable.put(id, new HashMap<String, Action>());
		}
		//对应冲突状态，如果标表中对应项目为空，加入错误状态
		if (actionTable.get(id).containsKey(terminal)) {
			//printConflict(id, terminal, actionTable.get(id).get(terminal), newAction);
		}//否则打印冲突原因
		actionTable.get(id).put(terminal, newAction);
	}

	public Action getAction(int id, String terminal) {//通过栈顶元素和
		//终结符返回动作
		Action action=actionTable.get(id).get(terminal);
		if(action==null)
			action=new Action(Action.ACTION_ERROR);
		return action;
	}

	public void printActionTable() {
		String buff = "";
		for (Integer id : actionTable.keySet()) {
			buff = "";
			for (String terminal : actionTable.get(id).keySet()) {
				buff += terminal + " " + actionTable.get(id).get(terminal).toString() + " | ";
			}
			Log.s(TAG, "Group " + id, buff);
		}
	}

	public String printActionTableToString() {
		String buff = "";
		String result = TAG + "\n\n";
		for (Integer id : actionTable.keySet()) {
			buff = "";
			for (String terminal : actionTable.get(id).keySet()) {
				buff += "'" + terminal + "' " + actionTable.get(id).get(terminal).toString() + " | ";
			}
			result += "Group " + id + ":" + buff + "\n";
		}
		return result;
	}

	public void printConflict(int id, String terminal, Action original, Action newAction) {
		Log.s(TAG, "Conflict@Group " + id + " with terminal " + terminal,
				"orignial " + original.toString() + " new " + newAction.toString());
	}
}
