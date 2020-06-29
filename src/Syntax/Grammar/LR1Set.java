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

/**
 * 
 * 继承LRSET
 *
 */
public class LR1Set extends LRSet implements LRSetInterface {
	public LR1Set(Grammars g, FirstSet fst, FollowSet flw) {
		
		super(g, fst, flw);
		super.genMove4Item = this;
	}

	public List<LR1Item> instances = new LinkedList<>();

	@Override
	public ItemGroup closure(SLRItem seed) {
		//项目及闭包
		Set<SLRItem> items = new HashSet<>();
		items.add(seed);//状态不再最右边
		if (seed.prefix_length < seed.right.length) {
			Nonterminal non = Nonterminal.getInstanceByTag(seed.right[seed.prefix_length]);
			//获取元素，判断是不是非终结符
			if (non != null) {
				closure_itr(items, non, calcEnd4((LR1Item) seed));
				//非终结符就把其闭包加进来
			}
		}
		return new ItemGroup(items);
	}

	public void closure_itr(Set<SLRItem> group, Nonterminal seed, Set<String> ends) {
		
		//项目闭包
		Set<SLRItem> newitems = new HashSet<>();
		for (String[] right : GrammarTable.getRightSides(seed).rightSides) {
			for (String end : ends) {
				//获取每一个LR1项目，如果项目及不含这个项目，就把这个项目加入
				LR1Item newitem=getItemInstance(seed, right, 0, end);
				if(!group.contains(newitem))
					newitems.add(newitem);
			}
		}
		if (newitems.size()==0) {
			return;
		}
		group.addAll(newitems);
		for (SLRItem item : newitems) {
			Nonterminal non = Nonterminal.getInstanceByTag(item.right[0]);
			if (non != null) {
				closure_itr(group, non, calcEnd4((LR1Item) item));
				//如果右部串首不是终结符，那么递归调用本函数
			}
		}
	}

	public Set<String> calcEnd4(LR1Item seed) {
		Set<String> ends;
		if (seed.prefix_length < seed.right.length - 1) {
			Nonterminal rear = Nonterminal.getInstanceByTag(seed.right[seed.prefix_length + 1]);
			if (rear != null) {//如果项目没有到最右边，那么获取右边的元素
				//如果非空，那么他的后一个可以出现的字符式后一个元素的first集
				ends = first.get(rear);
			} else {
				ends = new HashSet<>();//为 终结符直接加入终结符
				ends.add(seed.right[seed.prefix_length + 1]);
			}
		} else {
			ends = new HashSet<>();
			ends.add(seed.real_end);//直接加入可以在后面出现的字符
		}
		return ends;
	}

	public Set<String> calcEnd4(Nonterminal seed, String[] right, int pl, String end) {
		return calcEnd4(getItemInstance(seed, right, 0, end));
	}

	public LR1Item getItemInstance(Nonterminal l, String[] r, int pl, String end) {
		for (LR1Item instance : instances) {
			if (instance.right == r && instance.left == l && instance.prefix_length == pl && instance.real_end == end) {
				return instance;
			}//更具输入的四个条件求出项目
		}
		//如果没有则创建并且加入集合并返回
		LR1Item newitem = new LR1Item(l, r, pl, end);
		instances.add(newitem);
		return newitem;
	}

	@Override
	public void genReduction4Item(int id, SLRItem item) {
		if(item.left==Nonterminal.getInstanceByTag(GrammarTable.fir))
			actionTable.add(id, GrammarTable.END);//如果左部时开始符号，就在
		//$符号下加入规约动作
		else
			actionTable.add(id, ((LR1Item) item).real_end, item);
			//否做在他们的后一个可以出现的字符后面加入规约动作
	}

	@Override
	public SLRItem genRootItem() {
		Nonterminal ig = Nonterminal.getInstanceByTag(GrammarTable.fir);
		SLRItem root = new LR1Item(ig, GrammarTable.getRightSides(ig).get(0), 0, GrammarTable.END);
		return root;
	}//求根节点

	@Override
	public SLRItem step(SLRItem current) {
		return getItemInstance(current.left, current.right, current.prefix_length + 1, ((LR1Item) current).real_end);
	}
}//状态右移，并且以此为基础加入新的项目集
