package Syntax.Grammar;

import Syntax.Lex.Token;
import Syntax.Grammar.ActionTable.Action;
import Semantic.GrammarTree;
import Semantic.GrammarTree.TreeNode;
import MainFunction.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Stack;


public class LRParser {
	public static final String TAG = LRParser.class.getSimpleName();
	public static final String LOGFILE_NAME = "src/Syntax/Result/LR（1）语法分析.log";

	private Stack<Integer> statusStack;
	private Stack<String> symbolStack;
	private List<Token> input;
	private LRSet lrset;
	private BufferedWriter logWriter;

	public LRParser(LRSet _set) throws IOException {
		lrset = _set;
		statusStack = new Stack<>();
		symbolStack = new Stack<>();
	}

	public boolean parse(List<Token> inputTokens) throws IOException {
		input = inputTokens;
		statusStack.clear();//堆栈清空
		symbolStack.clear();
		statusStack.add(0);//状态栈初始化为0
		symbolStack.add(GrammarTable.END);//符号栈初始化加入结束符号
		logWriter = Log.getLogFile(LOGFILE_NAME);

		int cursor = 0;
		String terminal = inputTokens.get(cursor++).toTerminal();
		Boolean finished=false;
		//初始化，结束表示假
		do {
			Action action = lrset.actionTable.getAction(statusStack.peek(), terminal);
			//根据栈顶元素和输入的非终结符判断action
			printLog(action, terminal);
			switch(action.mode){
				//更具状态选择动作
				case Action.ACTION_SHIFTIN:
					//移入，id也即是状态入栈
					//终结符入符号栈
					statusStack.push(action.groupid);
					symbolStack.push(terminal);
					if (cursor < inputTokens.size()) {
						//终结符指针不再结尾
						terminal = inputTokens.get(cursor++).toTerminal();
					} else {
						terminal = GrammarTable.END;
					}
					break;
				case Action.ACTION_REDUCTION:
					for (int i = 0; i < action.gen.right.length; ++i) {
						statusStack.pop();
						//弹出产生是右部的数量的元素
						symbolStack.pop();
					}
					statusStack.push(lrset.gotoTable.go2(statusStack.peek(), action.gen.left));
					symbolStack.push(action.gen.left.TAG);
					//状态站根据goto表转换入栈
					//符号栈移入非终结符即左部
					break;
				case Action.ACTION_FINISHED:
					finished=true;//结束符号表示接受语句
					break;
				case Action.ACTION_ERROR:
					printError("wrong terminal:" + terminal + "@" + (cursor - 1));
					return false;//错误提示
			}
		} while (!finished);
		Log.releaseLogFile(logWriter);
		return true;
	}

	public boolean parse(List<Token> inputTokens,GrammarTree grammarTree) throws IOException {
		if(grammarTree==null){
			return parse(inputTokens);//建树
		}
		input = inputTokens;
		statusStack.clear();
		symbolStack.clear();
		statusStack.add(0);
		symbolStack.add(GrammarTable.END);//堆栈清空，初始化
		logWriter = Log.getLogFile(LOGFILE_NAME);
		int cursor = 0;
		String terminal = inputTokens.get(cursor++).toTerminal();
		Boolean finished=false;
		do {
			Action action = lrset.actionTable.getAction(statusStack.peek(), terminal);
			printLog(action, terminal);
			switch(action.mode){
				case Action.ACTION_SHIFTIN:
					statusStack.push(action.groupid);

					symbolStack.push(terminal);

					grammarTree.pushGenStack(new TreeNode(inputTokens.get(cursor-1)));

					if (cursor < inputTokens.size()) {
						terminal = inputTokens.get(cursor++).toTerminal();
					} else {
						terminal = GrammarTable.END;
					}
					break;
				case Action.ACTION_REDUCTION:

					for (int i = 0; i < action.gen.right.length; ++i) {
						statusStack.pop();
						symbolStack.pop();
						grammarTree.popGenStack2Workspace();
					}
					statusStack.push(lrset.gotoTable.go2(statusStack.peek(), action.gen.left));

					symbolStack.push(action.gen.left.TAG);

					grammarTree.addParent4Workspace(action.gen);

					break;
				case Action.ACTION_FINISHED:
					grammarTree.popGenStackToRoot();
					finished=true;
					break;
				case Action.ACTION_ERROR:
					printError("wrong terminal:" + terminal + "@" + (cursor - 1));
					return false;
			}
		} while (!finished);
		Log.releaseLogFile(logWriter);
		return true;
	}

	private void printError(String info) {
		String content = "status stack:" + printStatusStackToString();
		content += "\nsymbol stack:" + printSymbolStackToString();
		Log.s(TAG, "error", info + "\nenv:\n" + content);
	}

	private void printLog(Action action, String terminal) throws IOException {
		logWriter.write("Status Stack:" + printStatusStackToString());
		logWriter.newLine();
		logWriter.write("Symbol Stack:" + printSymbolStackToString());
		logWriter.newLine();
		logWriter.write("Action:" + action.toString() + " , terminal:" + terminal);
		logWriter.newLine();
		logWriter.newLine();
		logWriter.write("*******************************");
		logWriter.newLine();
		logWriter.newLine();
		logWriter.flush();
	}

	private String printStatusStackToString() {
		String content = "";
		for (Integer groupid : statusStack) {
			content += groupid + " ";
		}
		return content;
	}

	private String printSymbolStackToString() {
		String content = "";
		for (String symbol : symbolStack) {
			content += symbol + " ";
		}
		return content;
	}
}
