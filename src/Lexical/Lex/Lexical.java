package Lexical.Lex;

import java.io.*;
import java.util.ArrayList;

/*
        读取：从code.txt文件中读取
        终端输出：行号：N  单词   Token ( 种别码，自身值)
        保存文件：按行保存到token.txt文件中
    */

public class Lexical {
    public static ArrayList<String> strings =new ArrayList<String>();
    private String keyWord[] = {"if","then","else","while","do","int","void","main","return"};
    private char ch;
    //判断是否是关键字
    boolean isKey(String str)
    {
        for(int i = 0;i < keyWord.length;i++)
        {
            if(keyWord[i].equals(str))
                return true;
        }
        return false;
    }
    //判断是否是字母
    boolean isLetter(char letter)
    {
        if((letter >= 'a' && letter <= 'z')||(letter >= 'A' && letter <= 'Z'))
            return true;
        else
            return false;
    }
    //判断是否是数字
    boolean isDigit(char digit)
    {
        if(digit >= '0' && digit <= '9')
            return true;
        else
            return false;
    }
    //判断是否是分隔符
    boolean isFen(char Fen){
        if(Fen==';')
            return true;
        else
            return false;
    }
    //词法分析
    void analyze(char[] chars)
    {
        int line = 1;
        String arr = "";
        String str0;
        for(int i = 0;i< chars.length;i++) {

            ch = chars[i];
            arr = "";
            if(ch == ' '||ch == '\t'||ch == '\r'){}
            else if(ch == '\n'){
                line++;
            }
            else if(isFen(ch)){
                str0="line :"+line+"\t\t"+ch+"\t\t"+"( "+"SEMI "+" ,  "+ch+" )";
                System.out.println(str0);
                strings.add(str0);
            }
            else if(isLetter(ch)){
                while(isLetter(ch)||isDigit(ch)){
                    arr += ch;
                    ch = chars[++i];
                }
                //回退一个字符
                i--;
                if(isKey(arr)){
                    // 关键字---Keyword
                    str0 = "line :"+line+"\t\t"+arr+"\t\t"+"( "+"Keyword "+" ,  "+arr+" )";
                    System.out.println(str0);
                    strings.add(str0);
                }
                else{
                    // 标识符---Indentifier
                    str0 = "line :"+line+"\t\t"+arr+"\t\t"+"( "+"Indentifier "+" ,  "+arr+" )";
                    System.out.println(str0);
                    strings.add(str0);
                }
            }
            else if(isDigit(ch)||(ch == '.'))
            {
                // 整型常数
                while(isDigit(ch))
                {
                    arr = arr + ch;
                    ch = chars[++i];
                }
                str0 = "line :"+line+"\t\t"+arr+"\t\t"+"( "+"IntegerConstant "+" ,  "+arr+" )";
                System.out.println(str0);
                strings.add(str0);
            }
            else switch(ch){
                    // 运算符---OP
                    case '+':
                        str0 = "line :"+line+"\t\t"+ch+"\t\t"+"( "+"ADD "+" ,  "+ch+" )";
                        System.out.println(str0);
                        strings.add(str0);
                        break;
                    case '-':
                        str0 = "line :"+line+"\t\t"+ch+"\t\t"+"( "+"SUB "+" ,  "+ch+" )";
                        System.out.println(str0);
                        strings.add(str0);
                        break;
                    case '*':
                        str0 = "line :"+line+"\t\t"+ch+"\t\t"+"( "+"MUL "+" ,  "+ch+" )";
                        System.out.println(str0);
                        strings.add(str0);
                        break;
                    case '/':
                        str0 = "line :"+line+"\t\t"+ch+"\t\t"+"( "+"DIV "+" ,  "+ch+" )";
                        System.out.println(str0);
                        strings.add(str0);
                        break;
                    case '(':
                        str0 = "line :"+line+"\t\t"+ch+"\t\t"+"( "+"LP "+" ,  "+ch+" )";
                        System.out.println(str0);
                        strings.add(str0);
                        break;
                    case ')':
                        str0 = "line :"+line+"\t\t"+ch+"\t\t"+"( "+"RP "+" ,  "+ch+" )";
                        System.out.println(str0);
                        strings.add(str0);
                        break;
//                    case ';':System.out.println(ch+"\t"+"( "+"SEMI "+" ,  "+ch+" )");break;
                    case '\'':
                        str0 = "line :"+line+"\t\t"+ch+"\t\t"+"( "+"QU "+" ,  "+ch+" )";
                        System.out.println(str0);
                        strings.add(str0);
                        break;
                    case '=':{
                        ch = chars[++i];
                        if(ch == '='){
                            str0 = "line :"+line+"\t\t"+"==\t\t"+"( "+"EE "+" ,  "+"== )";
                            System.out.println(str0);
                            strings.add(str0);
                        }
                        else {
                            str0 = "line :"+line+"\t\t"+"=\t\t"+"( "+"EQ "+" ,  "+"= )";
                            System.out.println(str0);
                            strings.add(str0);
                            i--;
                        }
                    }break;
                    case '>':{
                        ch = chars[++i];
                        if(ch == '='){
                            str0 = "line :"+line+"\t\t"+">=\t\t"+"( "+"GE "+" ,  "+">= )";
                            System.out.println(str0);
                            strings.add(str0);
                        }
                        else {
                            str0 ="line :"+line+"\t\t"+">\t\t"+"( GT "+" ,  "+"> )";
                            System.out.println(str0);
                            strings.add(str0);
                            i--;
                        }
                    }break;
                    case '<':{
                        ch = chars[++i];
                        if(ch == '='){
                            str0 = "line :"+line+"\t\t"+"<=\t\t"+"( "+"LE "+" ,  "+"<= )";
                            System.out.println(str0);
                            strings.add(str0);
                        }
                        else {
                            str0 = "line :"+line+"\t\t"+"<\t\t"+"( "+"LT "+" ,  "+"< )";
                            System.out.println(str0);
                            strings.add(str0);
                            i--;
                        }
                    }break;
                    case '!':{
                        ch = chars[++i];
                        if(ch == '=') {
                            str0 = "line :" + line + "\t\t" + "!=\t\t" + "( " + "NE " + " ,  " + "!= )";
                            System.out.println(str0);
                            strings.add(str0);
                        }
                    }break;
                    // 无识别---NUL
                    case '\u0000':{
                        str0 = "line :"+line+"\t\t"+ch+"\t\t"+"( "+" NUL "+" ,   )";
                        System.out.println(str0);
                        strings.add(str0);
                    }break;
                    default:
                        str0 = "line :"+line+"\t\t"+ch+"\t\t"+" 错误输入，不存在匹配的单词！！！";
                        strings.add(str0);
                        System.out.println("line :"+line+"\t\t"+ch+"\t\t"+" 错误输入，不存在匹配的单词！！！");
                        break;
                }
        }
    }
    void write(ArrayList<String> strings){
        String path="src/Lexical/Lex/token.txt";
        File file = new File(path);
        //如果没有文件就创建
        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String l:strings){
            try {
                writer.write(l + "\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.close();
            System.out.println("结果已经保存至txt文件!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws Exception {
        File file = new File("src/Lexical/Lex/code.txt");//定义一个file对象，用来初始化FileReader
        FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
        int length = (int) file.length();
        //这里定义字符数组的时候需要多定义一个,因为词法分析器会遇到超前读取一个字符的时候，如果是最后一个
        //字符被读取，如果在读取下一个字符就会出现越界的异常
        char buf[] = new char[length+1];
        reader.read(buf);
        reader.close();
        Lexical l = new Lexical();
        l.analyze(buf);
        l.write(strings);
    }
}
