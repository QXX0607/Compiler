package Syntax.Grammar;

import Syntax.Lex.Keyword;
import Syntax.Lex.Operator;
import Syntax.Lex.Tag;
import Syntax.Lex.Type;
import MainFunction.Log;
import MainFunction.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GrammarTable {
	public static final String TAG = GrammarTable.class.getSimpleName();
	public static final String EMPTY = "empty";
	public static final String END = "$";

	public static class Nonterminal {
		public static Map<String, Nonterminal> instances = new HashMap<>();
		//创建一个哈希表的实例，用非终结符的标签tag查找非终结符
		public final String TAG;

		Nonterminal(String tag) {
			TAG = tag;
			instances.put(tag, this);
		}

		public boolean hasEmpty() {
			if (getRightSides(this).hasEmpty())//产生式右部为空返回
				return true;
			return false;
		}

		public static Nonterminal getInstanceByTag(String tag) {
			if (!instances.containsKey(tag)) {//完成上述根据tag查找非终结符
				return null;
			}
			return instances.get(tag);
		}
	}

	public static class GrammaFomula {
		public Nonterminal left;
		public RightSide right;

		public GrammaFomula(Nonterminal l, RightSide r) {
			left = l;
			right = r;
		}
	}

	public static class RightSide {
		public List<String[]> rightSides = new ArrayList<>();
		//多参数的情况，只有一个方法
		public RightSide(String[]... params) {
			for (int i = 0; i < params.length; ++i) {
				rightSides.add(params[i]);//对于每一个候选式，添加到一个列表元素中
			}
		}

		public RightSide(String[] params) {
			rightSides.add(params);
		}

		public boolean hasEmpty() {
			for (String[] gen : rightSides) {
				if (gen[0] == EMPTY)
					return true;//有空产生式的时候返回真，不是全部为空产生式，而是存在
			}
			return false;
		}

		public String[] get(int pos) {
			return rightSides.get(pos);//获得某个产生式
		}
	}

	public static class FirstSet extends HashMap<Nonterminal,Set<String>>{}
	
	public static class FollowSet extends HashMap<Nonterminal, Set<String>>{}
	
	public static class Grammars extends HashSet<GrammaFomula>{}//产生式集合
	
	public static final String fir = "fir";

	public static Grammars grammars;
//	public static Map<Nonterminal, Set<String>> first;
//	public static Map<Nonterminal, Set<String>> follow;

	public static FirstSet first;
	public static FollowSet follow;
	
	public static void cookGramma() {
		grammars = new Grammars();
		first = new FirstSet();
		follow = new FollowSet();
		
		genFullGramma();
//		genTestGramma();
		// 得到FIRST和FOLLOW集
		genFirstSet();
		genFollowSet();
	}
	
	public static void genFullGramma(){
		//初始化语法产生式
		//加入所有产生式
		grammars.add(new GrammaFomula(new Nonterminal(fir), new RightSide(new String[] { "P" })));

		grammars.add(
				new GrammaFomula(new Nonterminal("P"), new RightSide(new String[] { "D", "S" }, new String[] { "S" })));
		grammars.add(new GrammaFomula(new Nonterminal("D"), new RightSide(
				new String[] { "L", Tag.ID, Operator.LNND, "D" }, new String[] { "L", Tag.ID, Operator.LNND })));

		grammars.add(new GrammaFomula(new Nonterminal("L"),
				new RightSide(new String[] { Type.INT }, new String[] { Type.FLOAT })));
		grammars.add(new GrammaFomula(new Nonterminal("S"), new RightSide(new String[] { Tag.ID, Operator.ASSIGN, "E", Operator.LNND},
				new String[] { Keyword.IF, Operator.LPAR, "C", Operator.RPAR, Operator.LBPAR, "S", Operator.RBPAR },
				new String[] { Keyword.IF, Operator.LPAR, "C", Operator.RPAR, Operator.LBPAR, "S", Operator.RBPAR,
						Keyword.ELSE, Operator.LBPAR, "S", Operator.RBPAR },
				new String[] { Keyword.WHILE, Operator.LPAR, "C", Operator.RPAR, Operator.LBPAR, "S", Operator.RBPAR },
				new String[] { "S", "S" })));
		grammars.add(new GrammaFomula(new Nonterminal("C"), new RightSide(new String[] { "E", Operator.MORE, "E" },
				new String[] { "E", Operator.LESS, "E" }, new String[] { "E", Operator.EQU, "E" })));
		grammars.add(new GrammaFomula(new Nonterminal("E"), new RightSide(new String[] { "E", Operator.PLUS, "T" },
				new String[] { "E", Operator.MINUS, "T" }, new String[] { "T" })));
		grammars.add(new GrammaFomula(new Nonterminal("T"), new RightSide(new String[] { "F" },
				new String[] { "T", Operator.MULT, "F" }, new String[] { "T", Operator.DIV, "F" })));
		grammars.add(
				new GrammaFomula(new Nonterminal("F"), new RightSide(new String[] { Operator.LPAR, "E", Operator.RPAR },
						new String[] { Tag.ID }, new String[] { Tag.INT10 })));
	}
	
	public static void genTestGramma() {

		grammars.add(new GrammaFomula(new Nonterminal(fir), new RightSide(new String[] { "S" })));
		grammars.add(new GrammaFomula(new Nonterminal("S"),
				new RightSide(new String[] { Tag.ID, Operator.ASSIGN, Tag.INT10, Operator.LNND},
						new String[] { Keyword.IF, Operator.LPAR, Tag.INT10, Operator.RPAR, Operator.LBPAR, "S",
								Operator.RBPAR },
				new String[] { Keyword.IF, Operator.LPAR, Tag.INT10, Operator.RPAR, Operator.LBPAR, "S", Operator.RBPAR,
						Keyword.ELSE, Operator.LBPAR, "S", Operator.RBPAR },
				new String[] { Keyword.WHILE, Operator.LPAR, Tag.INT10, Operator.RPAR, Operator.LBPAR, "S",
						Operator.RBPAR }, new String[] { "S", "S" })));
//		grammars.add(new GrammaFomula(new Nonterminal(IGNITION), new RightSide(new String[] { "S" })));
//		grammars.add(new GrammaFomula(new Nonterminal("S"),new RightSide(new String[] { "C", "C" })));
//		grammars.add(new GrammaFomula(new Nonterminal("C"),new RightSide(new String[] { Operator.MULT, "C" },new String[] { Operator.PLUS })));
	}
	
	public static void genFirstSet() {
		for (GrammaFomula ele : grammars) {
			if (!first.containsKey(ele.left)) {//如果左部
				// Log.s(TAG, "gen firstset for " + ele.left.TAG);
				first.put(ele.left, getFirstSet(ele.left));
			}
		}
	}
	
	public static Set<String> getFirstSet(Nonterminal n) {
		// Log.s(TAG, "getFirstSet()", n.TAG);
		if (first.get(n) != null) {
			return first.get(n);//不为空，查询返回值
		}

		Set<String> firstSet = new HashSet<>();
		RightSide rightSide = getRightSides(n);//
		for (String[] gen : rightSide.rightSides) {
			Nonterminal firstEle = Nonterminal.getInstanceByTag(gen[0]);//获取串首符号，根据符号判断
			if (gen[0] == EMPTY) {
				firstSet.add(EMPTY);//串首为空，即空产生是，加入空语句
			} else if (firstEle != null) {
				boolean isempty = firstEle.hasEmpty();//是否有空产生是
				int i = 0;
				do {
					Nonterminal eleI = Nonterminal.getInstanceByTag(gen[i++]);
					//根据串首
					if (eleI != n) {//与当前非终结符不同，递归添加
						Utils.addWithoutEmpty(firstSet, getFirstSet(eleI));
					}
					isempty = eleI.hasEmpty();
				} while (i < gen.length && isempty);//如果当前非终结符有空产生式，并且i不大于等于产生式长度
				//表示可能由后面的产生式产生first集
			} else {
				firstSet.add(gen[0]);//终结符直接添加
			}
		}

		first.put(n, firstSet);//在集合中添加非终结符的first集
		return first.get(n);
	}

	public static void genFollowSet() {
		List<String> ig_follow = new ArrayList<>();
		ig_follow.add(END);
		Map<Nonterminal, Set<Nonterminal>> depends = new HashMap<>();
		FollowSet tmp_follow = new FollowSet();
		for (GrammaFomula ele : grammars) {//对于每个产生式
			//将左部加入follow集合和依赖集
			tmp_follow.put(ele.left, new HashSet<String>());
			depends.put(ele.left, new HashSet<Nonterminal>());
		}
		tmp_follow.get(Nonterminal.getInstanceByTag(fir)).add(END);
		//向开始符号非终结符中加入$
		for (GrammaFomula ele : grammars) {
			calcFollowSetDepends(depends, tmp_follow, ele);
			//计算依赖集，中间过渡的follow集，
		}
		try {//写文件
			Utils.printStringToFile("src/Syntax/Result/语法Follow集依赖.txt", printDependSetToString(depends));
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(!depends.isEmpty()){//通过依赖集计算follow集，计算出来后移除这个元素
			
			Nonterminal root=getRootFromDepends(depends);
			calcFollowSet(depends,tmp_follow,root);
			depends.remove(root);
		}
		depends = null;
		for (Nonterminal non : tmp_follow.keySet()) {
			follow.put(non, tmp_follow.get(non));//通过follow集的过渡集合按非终结符添加到
			//follow集合中
		}
		tmp_follow = null;
	}

	public static void calcFollowSetDepends(Map<Nonterminal, Set<Nonterminal>> depends, FollowSet tmp_follow, GrammaFomula fomula) {
		// Log.s(TAG, "getFollowSet()", n.TAG);
		// Map<String, Boolean> followSet = new HashMap<>();
		RightSide rights = fomula.right;
		for (String[] STR : rights.rightSides) {//每个非终结符的产生式
			Nonterminal ele = null;
			for (int i = 0; i < STR.length; ++i) {
				boolean CBL = false;
				ele = Nonterminal.getInstanceByTag(STR[i]);
				if (ele == null)
					continue;//空产生式略过找下一个
				if (i == STR.length - 1)
					CBL = true;//最后一个产生式都找了以后，标记为真
				for (int j = i + 1; j < STR.length; ++j) {
					Nonterminal next = Nonterminal.getInstanceByTag(STR[j]);//获取下一个元素，看是不是
					//终结符
					if (next == null) {
						tmp_follow.get(ele).add(STR[i + 1]);//没有非终结符就把下一个元素加入follow集
						CBL = false;//标记为假
						break;
					} else {
						Utils.addWithoutEmpty(tmp_follow.get(ele), first.get(next));//拔下一个元素的first集加入follow集
						
						if (!next.hasEmpty()) {//如果不含空产生式。与后面的非终结符就无关联
							CBL = false;
							break;
						}
					}
				}
				if (CBL && fomula.left!=ele) {//空产生式且左部不与其他产生式符号相同
					depends.get(fomula.left).add(ele);
				}
			}
		}
	}

	public static void calcFollowSet(Map<Nonterminal, Set<Nonterminal>> depends, FollowSet tmp_follow, Nonterminal root) {
		if(root==null){
			return;
		}
		for (Nonterminal child : depends.get(root)) {
			tmp_follow.get(child).addAll(tmp_follow.get(root));
			//对于依赖集合中的元素全部添加到本元素的集合中
			//修改所有的相关集合
			//递归调用计算，直到某一时刻集合不在变化
			calcFollowSet(depends, tmp_follow, child);
		}
	}

	public static Nonterminal getRootFromDepends(Map<Nonterminal,Set<Nonterminal>> depends){
		Nonterminal root=null;
		for(Nonterminal parent:depends.keySet()){
			root=parent;
			break;
		}
		//每一次求其父节点，并且求其父节点的父节点，
		//如果不为空，递归调用求父节点，最后返回根节点
		Nonterminal tmp=getParent(depends,root);
		while(tmp!=null){
			root=tmp;
			tmp=getParent(depends,root);
		}
		return root;
	}
	
	public static Nonterminal getParent(Map<Nonterminal,Set<Nonterminal>> depends,Nonterminal child){
		for(Nonterminal parent:depends.keySet()){
			if(depends.get(parent).contains(child)){
				return parent;
			}//根据Java JRE自带的函数实现查找父节点的函数，根据依赖集
		}
		return null;
	}
	
	public static boolean lead2Empty(Nonterminal... nons) {
		for (int i = 0; i < nons.length; ++i) {
			if (!nons[i].hasEmpty())
				return false;
		}//对于每一个参数，如果每一个非终结符都有空产生是，返回真
		return true;
	}

	public static RightSide getRightSides(Nonterminal n) {
		for (GrammaFomula ele : grammars) {
			if (ele.left.TAG.equals(n.TAG)) {
				return ele.right;
				//根据标签返回v产生是有部
			}
		}
		return null;
	}

	public static void printGrammaTable() {
		String tmp = "";
		for (GrammaFomula ele : grammars) {
			for (String[] gen : ele.right.rightSides) {
				tmp = "";
				for (int i = 0; i < gen.length; ++i)
					tmp += gen[i];
				Log.s(TAG, "Grammar", ele.left.TAG + "->" + tmp);
			}//对于每一个产生是，分别输出，即不采用简化形式的产生是
		}
	}

	public static String printGrammaTableToString() {
		String tmp = "";
		String result = "";
		for (GrammaFomula ele : grammars) {
			for (String[] gen : ele.right.rightSides) {
				tmp = "";
				for (int i = 0; i < gen.length; ++i)
					tmp += gen[i];
				result += "Grammar:" + ele.left.TAG + "->" + tmp + "\n";
				// Log.s(TAG, "Grammar", ele.left.TAG + "->" + tmp);
			}
		}
		return result;
	}

	public static void printGrammaTableToFile(String filename) throws IOException {
		File outputFile = new File(filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		String tmp = "";
		writer.write("gramma table:");
		writer.newLine();
		for (GrammaFomula ele : grammars) {
			for (String[] gen : ele.right.rightSides) {
				tmp = "";
				for (int i = 0; i < gen.length; ++i)
					tmp += gen[i];
				writer.write(ele.left.TAG + "->" + tmp);
				writer.newLine();
			}
		}
		writer.flush();
		writer.close();
	}

	public static void printFirstSet() {
		String buff = "";
		for (Nonterminal ele : first.keySet()) {
			buff = "";
			for (String data : first.get(ele)) {
				buff = buff + data + " | ";
			}//对于每一个符号的first集
			//对于每一个first中的元素，用|分割
			Log.s(TAG, "first set for " + ele.TAG + ":" + buff);
		}
	}

	public static String printFirstSetToString() {
		String buff = "";
		String result = "";
		for (Nonterminal ele : first.keySet()) {
			buff = "";
			for (String data : first.get(ele)) {
				buff = buff + data + " | ";
			}
			result += "first set for " + ele.TAG + ":" + buff + "\n";
			// Log.s(TAG, "first set for " + ele.TAG + ":" + buff);
		}
		return result;
	}

	public static void printFollowSet() {
		String buff = "";
		for (Nonterminal ele : follow.keySet()) {
			buff = "";
			for (String data : follow.get(ele)) {
				buff = buff + data + " | ";
			}//类似first集的打印
			Log.s(TAG, "follow set for " + ele.TAG + ":" + buff);
		}
	}

	public static String printFollowSetToString() {
		String buff = "";
		String result = "";
		for (Nonterminal ele : follow.keySet()) {
			buff = "";
			for (String data : follow.get(ele)) {
				buff = buff + data + " | ";
			}
			// Log.s(TAG, "follow set for " + ele.TAG + ":" + buff);
			result += "follow set for " + ele.TAG + ":" + buff + "\n";
		}
		return result;
	}

	public static String printDependSetToString(Map<Nonterminal,Set<Nonterminal>> data) {
		String tmp="";
		String buff="";
		for(Nonterminal parent:data.keySet()){
			tmp=parent.TAG+"->";
			for(Nonterminal child:data.get(parent)){
				tmp+=child.TAG+" | ";
			}
			buff+=tmp+"\n";
		}//同理类似first集的输出
		return buff;
	}
}