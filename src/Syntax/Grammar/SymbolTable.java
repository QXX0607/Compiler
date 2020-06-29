package Syntax.Grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
//查填符号表

public class SymbolTable {
	public static class Properties extends HashMap<String,String>{
		
	}
	
	static Map<String,Properties> symbolTable=new HashMap<String,Properties>();
	
	public static void setItem(String id,String property,String value){
		if(!symbolTable.containsKey(id)){
			symbolTable.put(id, new Properties());
		}
		symbolTable.get(id).put(property, value);
	}
	
	public static Set<String> getIds(){
		return symbolTable.keySet();
	}
	
	public static Properties getProperties(String id){
		return symbolTable.get(id);
	}
	
	public static String getProperty(String id,String property){
		return getProperties(id).get(property);
	}
	
	public static String printSymbolTableToString(){
		String result="";
		for(String id:getIds()){
			result+="id:"+id+",";
			for(String property:getProperties(id).keySet()){
				result+=property+":"+getProperty(id,property)+",";
			}
			result+="\n";
		}
		return result;
	}
}
