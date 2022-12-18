# README

### 1.关于本项目

本项目实现了一个2-way的AETG算法，输入一个测试样例的CTI建模后，本项目将输出该测试样例的Covering array。关于该算法的详尽描述可见本项目的报告部分。



### 2.部署与说明

#### 1.关于部署

本项目是一个java project，使用idea开发。由于本项目尚未封装jar包，推荐使用idea运行本项目。且本项目中包含中文的注释与输入输出，推荐以GBK的编码形式打开本项目，否则可能存在注释乱码的问题。



#### 2.关于输入

首先，本项目已提供四个testcase作为输入，其中testcase1为全英文，其余testcase均存在中文。已存放在文件夹中，在运行本项目前，请修改Main.class中关于输入文件的路径定义，请修改为输入文件的绝对路径。

```java
public final static String inputfilepath = "D:\\grade3.1\\软件测试与保护\\AETG\\testcase2.txt";
```



其次，由于本项目提供的testcase中包含中文，请确保您的txt文件编码为GBK（使用记事本打开则为ANSI），否则可能出现输入输出乱码问题。以及，请确保你的格式为：

```
factor1:value1-1,value1-2,...value1-n

例如：
品牌:hp,ThinkPad,HUAWEI,Lenovo,DELL,APPLE
能效等级:一级能耗,二级能耗,五级能耗
显卡:MX230,RTX3070Ti,RTX3080Ti
```

factor和value之间用英文冒号连接，value之间用英文逗号连接，每行最后一个value末尾没有多余逗号，中间无多余空格，factor和value中英文皆可。



#### 3.关于输出

本项目已提供一个默认的文件用于输出，已存放在文件夹中，在运行本项目前，请修改Main.class中关于输出文件的路径定义，请修改为输出文件的绝对路径。

```java
public final static String outputfilepath = "D:\\grade3.1\\软件测试与保护\\AETG\\output.txt";
```



请尽量保证输出文件的编码格式为GBK，否则可能出现输出乱码问题
