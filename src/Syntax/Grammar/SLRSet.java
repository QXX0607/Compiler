package Syntax.Grammar;

import Syntax.Grammar.GrammarTable.FirstSet;
import Syntax.Grammar.GrammarTable.FollowSet;
import Syntax.Grammar.GrammarTable.Grammars;
import Syntax.Grammar.GrammarTable.Nonterminal;
import Syntax.Grammar.LRSet.LRSetInterface;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SLRSet extends LRSet implements LRSetInterface {

	public SLRSet(Grammars g, FirstSet fst, FollowSet flw) {
		super(g, fst, flw);
		super.genMove4Item = this;
	}

	public static List<SLRItem> instances = new LinkedList<>();

	public static SLRItem getItemInstance(Nonterminal l, String[] r, int pl) {
		for (SLRItem instance : instances) {
			if (instance.right == r && instance.left == l && instance.prefix_length == pl) {
				return instance;//左右部都相同并且状态也相同
			}
		}
		SLRItem newitem = new SLRItem(l, r, pl);
		instances.add(newitem);
		return newitem;
	}

	@Override
	public ItemGroup closure(SLRItem seed) {
		Set<SLRItem> items = new HashSet<>();
		items.add(seed);
		if (seed.prefix_length < seed.right.length) {
			Nonterminal non = Nonterminal.getInstanceByTag(seed.right[seed.prefix_length]);
			if (non != null) {//是终结符就把终结符对应的闭包加入
				items.addAll(closure_itr(non));
			}
		}
		return new ItemGroup(items);
	}

	public static Set<SLRItem> closure_itr(Nonterminal seed) {
		Set<SLRItem> items = new HashSet<>();
		for (String[] right : GrammarTable.getRightSides(seed).rightSides) {
			items.add(getItemInstance(seed, right, 0));//状态在最左边，表示等待规约
			Nonterminal non = Nonterminal.getInstanceByTag(right[0]);
			if (right[0] != seed.TAG && non != null) {
				items.addAll(closure_itr(non));//同理，等待非终结符，增加状态
			}
		}
		return items;
	}

	@Override
	public void genReduction4Item(int id, SLRItem item) {
		if (item.left == Nonterminal.getInstanceByTag(GrammarTable.fir)) {
			actionTable.add(id, GrammarTable.END);//$，acc
		} else {
			for (String flw : GrammarTable.follow.get(item.left))
				actionTable.add(id, flw, item);//select集
		}
	}

	@Override
	public SLRItem genRootItem() {
		Nonterminal ig = Nonterminal.getInstanceByTag(GrammarTable.fir);
		SLRItem root = new SLRItem(ig, GrammarTable.getRightSides(ig).get(0), 0);
		return root;
	}

	@Override
	public SLRItem step(SLRItem current) {
		return getItemInstance(current.left, current.right, current.prefix_length + 1);
	}//规约到字符，状态右移
}
