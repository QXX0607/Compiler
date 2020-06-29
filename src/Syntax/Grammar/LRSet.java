package Syntax.Grammar;

import MainFunction.Log;
import Syntax.Grammar.GrammarTable.FirstSet;
import Syntax.Grammar.GrammarTable.FollowSet;
import Syntax.Grammar.GrammarTable.Grammars;
import Syntax.Grammar.GrammarTable.Nonterminal;

import java.util.*;

public class LRSet {
	public String TAG=LRSet.class.getSimpleName();
	
	public static interface LRSetInterface{
		SLRItem genRootItem();
		void genReduction4Item(int id, SLRItem item);

		ItemGroup closure(SLRItem seed);
		SLRItem step(SLRItem current);
	}
	
	Grammars grammar;
	FirstSet first;
	FollowSet follow;
	LRSetInterface genMove4Item;

	public List<ItemGroup> itemGroups;
	public Set<Integer> finishedGroups;
	public ActionTable actionTable;
	public GotoTable gotoTable;
	
	LRSet(Grammars g, FirstSet fst, FollowSet flw){
		grammar = g;
		first = fst;
		follow = flw;
		itemGroups = new ArrayList<>();
		finishedGroups = new HashSet<>();
		actionTable = new ActionTable();
		gotoTable = new GotoTable();
	}
	
	public void genItemGroups() {
		addGroup(genMove4Item.closure(genMove4Item.genRootItem()));
		genItemGroups(0);
	}
	
	public void genItemGroups(int id) {
		if (finishedGroups.contains(id))
			return;
		ItemGroup tmpGroup = null;
		Map<String, Set<SLRItem>> edgeNgroup = new HashMap<>();
		List<Integer> queue = new LinkedList<>();
		for (SLRItem item : itemGroups.get(id).items) {
			if (item.prefix_length == item.right.length) {

				genMove4Item.genReduction4Item(id, item);//规约
			} else {
				String edge = item.right[item.prefix_length];
				//状态法，等待规约的字符就是边
				tmpGroup = genMove4Item.closure(genMove4Item.step(item));
				
				if (edgeNgroup.get(edge) != null) {
					edgeNgroup.get(edge).addAll(tmpGroup.items);//加入闭包
				} else {
					edgeNgroup.put(edge, tmpGroup.items);
				}
			}
		}
		for (String edge : edgeNgroup.keySet()) {
			Nonterminal non = Nonterminal.getInstanceByTag(edge);
			tmpGroup = new ItemGroup(edgeNgroup.get(edge));
			int nextid = addGroup(tmpGroup);
			if (non == null) {

				actionTable.add(id, edge, nextid);//终结符添加action表
			} else {

				gotoTable.add(id, non, nextid);//非终结符添加goto表
			}
			queue.add(nextid);
		}
		finishedGroups.add(id);
		for (Integer nextid : queue) {//递归构造表和转换图
			genItemGroups(nextid);
		}
		queue = null;
		edgeNgroup = null;
	}
	
	public int addGroup(ItemGroup newGroup) {
		for (ItemGroup group : itemGroups) {
			if(group.items.containsAll(newGroup.items))
				return group.ID;
		}
		itemGroups.add(newGroup);
		newGroup.ID = itemGroups.size() - 1;//当前列表数目-1，构造项集组主要函数
		return newGroup.ID;
	}
	
	public void printItemGroups() {
		for (ItemGroup group : itemGroups) {
			for (SLRItem item : group.items) {
				Log.s(TAG, "Group " + group.ID, item.toString());
			}
		}
	}
	
	public String printItemGroupsToString(){
		String buff=TAG+"\n\n";
		String tmp="";
		for (ItemGroup group : itemGroups) {
			tmp="Group " + group.ID + ":";
			for (SLRItem item : group.items) {
				tmp+=item.toString() + " | ";
			}
			buff+=tmp+"\n";
		}
		return buff;
	}
}
