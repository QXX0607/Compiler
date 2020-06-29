package Syntax.Grammar;

import java.util.Set;

/**
 * 
 * 项目集，对于LR，SLR
 *
 */
public class ItemGroup {
	public int ID;
	public final Set<SLRItem> items;

	ItemGroup(Set<SLRItem> is) {
		items = is;
	}
}