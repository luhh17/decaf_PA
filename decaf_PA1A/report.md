# 编译原理 PA 1-A 实验报告

计63 陈晟祺 2016010981

## 实验简述

本阶段的实验要求是利用 jflex 和 byacc 完成 Decaf 语言编译器的词法分析和语法分析部分，同时生成抽象语法树。已有的实验框架已经包含了对 Decaf 语言现有语法的分析的完整支持，本阶段需要添加的语言特性有：对象复制语句、封闭类（sealed class）、串行条件卫士语句、简单的自动类型推导和一系列数组相关表达式、语句。

## 主要工作

主要工作分为两部分，一是向词法和文法文件中添加相应的 token 和产生式，二是向 AST 使用的数据结构中添加新特性对应的语法单元。其中产生式均由实验要求中给出的 EBNF 范式改写而来，而 AST 类根据需要添加（sealed class 与常量数组表达式不需要新的类）。

添加的大部分新特性均较为简单，如 scopy、sealed class、类型推导、数组初始化、拼接、slice 和下标访问操作，都只需要添加一条确定的产生式以及其对应的语法类，在规约规则中新建类并连接到语法树上。而串行条件卫视和数组常量表达式中均涉及到变长的列表，需要在撰写文法时注意列表的表述方式。实验文档中给出了一种推荐的表述，而我等价地表述如下：

```
ArrayConstant   :   '[' ConstantList ']'
                |   '[' ']';
ConstantList    :   ConstantList ',' Constant
                |	Constant;
```

如此表达的好处是，能够消除 `ConstantList` 中的空情况，减少规约时运行的代码出错的可能（因为 yyval 在整个推导过程中根据规则而变化，不仔细思考推导过程容易出现空指针/冗余对象问题）。对于串行卫士也是类似的。

实验中对一些运算符有特殊的结合性和优先级要求，如果运算符是二元的（如几个数组运算），则可以简单地在文法前端进行声明。而对于 `Expr [ Expr ] default Expr` 这样的”三元“运算符，则需要在优先级声明区对应位置声明一个”伪终结符“（实验中要求其比所有二元运算都高，则应该在靠后的位置），而后在文法的相应规则处使用 `%prec TOKEN_NAME` 的方式覆盖规则的优先级。

此外，实验中原本给出的部分文法规则比较冗长（如 `Expr`），我将其分为多个语义上相近的部分（如 `BinaryExpr`, `UnaryExpr`, `ReadExpr `等），使规则更清晰。

## 遇到问题

1. 在添加了数组常量和 list comprehension 的文法后发生 shift/reduce 冲突，遇数组常量表达式即报语法错误。

   情况如下，此时词法分析器到达了”.“的位置，有两个选择，规约到 `Expr` 或者移进继续分析 `ConstantList`。引起这个问题的原因是文法中的冲突导致 yacc 默认选择了 reduce，改写文法，并给 list comprehension 正确赋予优先级后，冲突消失，问题也解决。

   ```
   [ Constant . FOR / ','
   ```

2. 一些语法结构（如数组常量、串行卫士）在子表达式为空时需要在 AST 中打印 `<empty>`，实验文档中没有指出，但样例中给出。

   对应添加判断即可。

3. 某些特性（如 sealed class，以及 foreach）需要用到原本 SemValue 类中没有的属性。

   可以额外添加 field，或者复用现有的字段（如 literal），平衡可读性与简易性。

## 正确性测试

本程序在给出的测试样例中均产生了正确的 AST 打印输出。

为了额外测试一些细节以及 corner case，我还编写了以下的测试用例 （只保留核心部分）：

1. 嵌套的各种数组表达式与优先级：

```
foreach (var y in [a for x in [b for j in x if a[3] default (c[2:5] ++ [2,3]) %% 3]] while a == b && c > d)
```

2. 一些边界情况：

```
if {null: if{} ||| 1: {
    b = [];
    scopy(a, null);
}}
```

