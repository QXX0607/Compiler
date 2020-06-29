
package MainFunction;

import Syntax.Grammar.SymbolTable;
import Syntax.Lex.Lexer;
import Syntax.Grammar.GrammarTable;
import Syntax.Grammar.LR1Set;
import Syntax.Grammar.LRParser;
import Semantic.GrammarTree;

import java.io.File;
import java.util.Scanner;

	/*
		输入：从文件读取
		输出：输出到指定txt文件中
	 */


public class Main {
	private static final boolean IS_DEBUG = false;
	private static final String DEBUG_INPUT_FILE_NAME = "src/Syntax/源代码.txt";
	//private static final String DEBUG_OUTPUT_GRAMMA_TABLE_FILE_NAME = "src/Syntax/符号表.txt";
	private static final Boolean USE_LR1=true;
	private static final Boolean GEN_GRAMMAR_TREE=true;

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.out.println("===============请输入源代码文件名==============");
			System.out.println("（回车默认使用源代码.txt）");
			Scanner scanner = new Scanner(System.in);
			String filename;
			File inputFile;
			if (IS_DEBUG) {
				inputFile = new File(DEBUG_INPUT_FILE_NAME);
			} else {
				do {
					filename = scanner.nextLine();
					if (filename.equals("")) {
						filename = "src/Syntax/源代码.txt";
					}
					inputFile = new File(filename);
					if (inputFile.isDirectory() || !inputFile.exists()) {
						System.out.println("文件不存在！" + filename);
						System.out.println("请输入源代码文件名:");
					} else {
						break;
					}
				} while (true);
			}
			scanner.close();
			
			//SymbolTable symbolTable=new SymbolTable();
			/*
				对源代码进行词法分析处理
				输出：Token序列到txt文件
			 */
			Lexer l = new Lexer(inputFile);
			if(!l.parse())
				return;
			Utils.printStringToFile("src/Syntax/Token序列.txt", l.printTokensToString());
			
			GrammarTable.cookGramma();
			// 使用实验指导书中的给定文法
			GrammarTable.printGrammaTableToFile("src/Syntax/Grammar_before.txt");
			GrammarTable.genFirstSet();
			GrammarTable.genFollowSet();
			// 处理后的文法
			Utils.printStringToFile("src/Syntax/Grammar_after.txt", GrammarTable.printGrammaTableToString(),
					GrammarTable.printFirstSetToString(), GrammarTable.printFollowSetToString());
			Utils.printStringToFile("src/Syntax/Result/FIRST集.txt",GrammarTable.printFirstSetToString());
			Utils.printStringToFile("src/Syntax/Result/FOLLOW集.txt", GrammarTable.printFollowSetToString());
			
			GrammarTree grammarTree=null;
			if(GEN_GRAMMAR_TREE)
				grammarTree=new GrammarTree();
			
			boolean result=false;
			//  LR(1)项族，GoTo表，Action表输出到文件
			if(USE_LR1){
				LR1Set lr1Set = new LR1Set(GrammarTable.grammars, GrammarTable.first, GrammarTable.follow);
				lr1Set.genItemGroups();
				Utils.printStringToFile("src/Syntax/Result/LR1项族.txt", lr1Set.printItemGroupsToString());
				Utils.printStringToFile("src/Syntax/Result/GoTo表.txt",lr1Set.gotoTable.printGotoTableToString());
				Utils.printStringToFile("src/Syntax/Result/Action表.txt",lr1Set.actionTable.printActionTableToString());

				LRParser lp=new LRParser(lr1Set);
				result=lp.parse(l.getTokens(),grammarTree);
				lp=null;
			}

			if(!result)
				return;
			
			if(GEN_GRAMMAR_TREE){
				Utils.printStringToFile("src/Semantic/Result/三地址中间代码.txt", grammarTree.getFullcode());
			}
			
			Utils.printStringToFile("src/Semantic/Result/符号表.txt", SymbolTable.printSymbolTableToString());
			System.out.println("=======================分析结果=======================\n");
			System.out.println("** 请在src/Syntax/Result目录下查看语法分析处理结果文件！**");
			System.out.println("** 请在src/Semantic/Result目录下查看语义分析处理结果文件！**\n");
			System.out.println("=======================分析结束=======================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
