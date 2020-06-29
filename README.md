### 运行环境

- 
  ##### 操作系统：Windows10

- ##### 语言环境：Java

-----------

### Lexical为词法分析器

> 1. code.txt为待分析源码
>
>    ![code.txt](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200629175955486.png)
>
> 2. token.txt为输出结果
>
>    ![token.txt](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200629180007003.png)

### MainFunction为语法&&语义的主函数入口，因两者关联较大，直接使用了一个主函数调用
> ##### 源代码.txt 可以进行修改,回车默认分析源代码.txt中存储的代码段，也可以输入指定文件名

### Syntax为语法分析器
> ##### 语法分析结果保存在  CompilePrinciples\src\Syntax\Result
>
> 1. Action表.txt
>
>    ![Action表](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200629180058437.png)
>
> 2. FIRST集.txt
>
>    ![FIRST集](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200629180038225.png)
>
> 3. FOLLOW集.txt
>
>    ![FOLLOW集](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200629180044684.png)
>
> 4. GoTo表.txt
>
>    ![GoTo表](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200629180052100.png)
>
> 5. LR(1)语法分析.log
>
>    ![语法分析](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200629180109476.png)

### Semantic为语义处理
> ##### 语法分析结果文件保存在 CompilePrinciples\src\Semantic\Result
>
> 1. 三地址中间代码.txt
>
>    ![三地址中间代码](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200629180135851.png)
>
> 2. 符号表.txt
>
>    ![符号表](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200629180142511.png)
