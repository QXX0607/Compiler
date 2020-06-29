package MainFunction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//辅助类
//主要用于在屏幕打印冲突信息

public final class Log {
	public static final String SEPERATOR=":";
	
	public static void s(String head,String message){
		System.out.println(head+SEPERATOR+message);
	}
	
	public static void s(String head,String mediate,String message){
		System.out.println(head+SEPERATOR+mediate+SEPERATOR+message);
	}

	public static BufferedWriter getLogFile(String filename) throws IOException{
		File outputFile = new File(filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		return writer;
	}

	public static void releaseLogFile(BufferedWriter writer) throws IOException{
		writer.flush();
		writer.close();
	}
	
}
