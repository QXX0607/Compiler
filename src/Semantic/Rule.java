package Semantic;

import Syntax.Lex.*;
import Syntax.Grammar.SymbolTable;
import Syntax.Lex.Token;
import Syntax.Grammar.SLRItem;
import Semantic.GrammarTree.TreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//本部分主要是 Node的定义
//对于节点进行属性处理
//.code属性用于生成中间代码

public class Rule {
	//跳转用的标签Label用L表示
	private static int labelNum = 1;
	//临时变量用t表示
	private static int tNum = 1;

	public static class Node {
		public String tag;
		Map<String, String> properties;

		public Node(String _tag) {
			tag = _tag;
			properties = new HashMap<>();
		}

		public Node(SLRItem item, List<TreeNode> childs) {
			this(item.left.TAG);
			String code = "", place = "", type = "", next="";
			//依据产生式左边变量进行选择
			switch (item.left.TAG) {
			case "P":
				if (equal(item.right, new String[] { "D", "S" })) {
					code = childs.get(0).getValue("code") + childs.get(1).getValue("code")+childs.get(1).getValue("next");
				} else if (equal(item.right, new String[] { "S" })) {
					code = childs.get(0).getValue("code")+childs.get(0).getValue("next");
				}
				setProperty("code", code);
				break;
			case "S":
				if (equal(item.right, new String[] { Tag.ID, Operator.ASSIGN, "E", Operator.LNND })) {
					code = childs.get(2).getValue("code")+childs.get(0).getValue("name") + "="
							+ childs.get(2).getValue("place") + "\n";
				}

				else if (equal(item.right, new String[] { Keyword.IF, Operator.LPAR, "C", Operator.RPAR, Operator.LBPAR,
						"S", Operator.RBPAR })) {
					childs.get(2).setProperty("true", genLabel());

					code = childs.get(2).getValue("code") + childs.get(2).getValue("true")
							+ childs.get(5).getValue("code");
				}

				else if (equal(item.right, new String[] { Keyword.IF, Operator.LPAR, "C", Operator.RPAR, Operator.LBPAR,
						"S", Operator.RBPAR, Keyword.ELSE, Operator.LBPAR, "S", Operator.RBPAR })) {
					next=genLabel();
					
					code = childs.get(2).getValue("code") + childs.get(2).getValue("true")
							+ childs.get(5).getValue("code") + "goto " + next
							+ childs.get(2).getValue("false") + childs.get(9).getValue("code");
				}

				else if (equal(item.right, new String[] { Keyword.WHILE, Operator.LPAR, "C", Operator.RPAR,
						Operator.LBPAR, "S", Operator.RBPAR })) {
					String root = genLabel();
					childs.get(5).setProperty("next", root);
					code = root + childs.get(2).getValue("code") + childs.get(2).getValue("true")
							+ childs.get(5).getValue("code") + "goto " + root + childs.get(2).getValue("false");
				} else if (equal(item.right, new String[] { "S", "S" })) {
					next=childs.get(1).getValue("next");
					code = childs.get(0).getValue("code") + childs.get(0).getValue("next") + childs.get(1).getValue("code");
				}
				setProperty("code", code);
				setProperty("next", next);
				break;
			case "C":
				code = childs.get(0).getValue("code") + childs.get(2).getValue("code") + "if("
						+ childs.get(0).getValue("place");
				setProperty("true",genLabel());
				setProperty("false",genLabel());
				if (equal(item.right, new String[] { "E", Operator.MORE, "E" })) {
					code += Operator.MORE;
				} else if (equal(item.right, new String[] { "E", Operator.LESS, "E" })) {
					code += Operator.LESS;
				} else if (equal(item.right, new String[] { "E", Operator.EQU, "E" })) {
					code += Operator.EQU;
				}
				code += childs.get(2).getValue("place") + ") goto " + getValue("true") + "goto "
						+ getValue("false");
				setProperty("code",code);
				break;
			case "E":
				if (equal(item.right, new String[] { "T" })) {
					place = childs.get(0).getValue("place");
					code = childs.get(0).getValue("code");
				}else{
					place = genTmp();
					code = childs.get(0).getValue("code") + childs.get(2).getValue("code")
							+ place +"=" + childs.get(0).getValue("place");
					if (equal(item.right, new String[] { "E", Operator.PLUS, "T" })) {
						 code+= "+";
					} else if (equal(item.right, new String[] { "E", Operator.MINUS, "T" })) {
						code += "-";
					}
					code+=childs.get(2).getValue("place")+"\n";
				}
				setProperty("place", place);
				setProperty("code", code);
				break;
			case "T":
				if (equal(item.right, new String[] { "F" })) {
					place = childs.get(0).getValue("place");
					code = childs.get(0).getValue("code");
				} else {
					place = genTmp();
					code = childs.get(0).getValue("code") + childs.get(2).getValue("code")
							+ place + "=" + childs.get(0).getValue("place");
					if (equal(item.right, new String[] { "T", Operator.MULT, "F" })) {
						code += "*";
					} else if (equal(item.right, new String[] { "T", Operator.DIV, "F" })) {
						code += "/";
					}
					code += childs.get(2).getValue("place")+"\n";
				}
				setProperty("place", place);
				setProperty("code", code);
				break;
			case "F":
				if (equal(item.right, new String[] { Operator.LPAR, "E", Operator.RPAR })) {
					place = childs.get(1).getValue("place");
					code = childs.get(1).getValue("code");
				} else if (equal(item.right, new String[] { Tag.ID })) {
					place = childs.get(0).getValue("name");
					code = "";
				} else if (equal(item.right, new String[] { Tag.INT10 })) {
					place = childs.get(0).getValue("value");
					code = "";
				}
				setProperty("place", place);
				setProperty("code", code);
				break;
			case "L":
				if (equal(item.right, new String[] { Type.INT })) {
					type = "int";
				} else if (equal(item.right, new String[] { Type.FLOAT })) {
					type = "float";
				}
				setProperty("code", type);
				break;
			case "D":
				code = "";
				String name=childs.get(1).getValue("name");
				type=childs.get(0).getValue("code");
				code += type + " " + name +"\n";
				if (equal(item.right, new String[] { "L", Tag.ID, Operator.LNND, "D" })) {
					code += childs.get(3).getValue("code");
				}
				SymbolTable.setItem(name, "type", type);
				setProperty("code", code);
				break;
			default:
				break;
			}
		}

		public Node(Token token) {
			this(token.toTerminal());
			properties = new HashMap<>();
			switch (token.tag) {
			case Tag.ID:
				setProperty("name", token.getValue());
				break;
			case Tag.INT10:
				setProperty("value", token.getValue());
				break;
			case Tag.KEYWORD:
				switch (token.getValue()) {
				case Keyword.WHILE:
					setProperty("root", "");
					break;
				}
				break;
			default:
				break;
			}
		}

		public void addProperty(String property) {
			addProperty(property, "");
		}

		public void addProperty(String property, String defaultValue) {
			properties.put(property, defaultValue);
		}

		public void setProperty(String property, String value) {
			properties.put(property, value);
		}

		public String getValue(String property) {
			String mediate=properties.get(property);
			if(mediate==null){
				return null;
			}
			return mediate;
		}

		public String toString() {
			return tag;
		}
	}

	private static boolean equal(String[] left, String[] right) {
		if (left.length != right.length) {
			return false;
		}
		for (int i = 0; i < left.length; ++i) {
			if (left[i] != right[i])
				return false;
		}
		return true;
	}

	private static String genLabel() {
		return "L" + (labelNum++) + ":\n";
	}

	private static String genTmp() {
		return "t" + (tNum++);
	}
}
