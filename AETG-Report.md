# AETG-Report

### 1.问题分析

#### 1.名词解释：

* 被测对象：本项目的目标，被简化为一个模型，模型包含对象的各个属性以及各个属性的属性值集合

* 二元属性值对：是被测对象的两个属性的值，保证这两个值不属于同一个属性
* 二元属性值对集合：本项目的一个容器，存储被测对象所有二元属性值对
* testcase：即测试用例，一条测试用例包括测试对象的每个属性以及每个属性的一个值
* 覆盖：如果一条测试中两个属性的值形成的二元属性值对与二元属性值对集合中的某个元素一致，称为覆盖



#### 2.算法分析：

AETG算法是用于寻找一个被测对象的covering-array，本项目实现的是一个2-way AETG，即目标为生成的covering array包含被测对象所有属性值的二元集合。整个算法的思路如下：我们会生成一个尚未覆盖的二元属性值对的集合，不断生成testcase以覆盖这个集合中的元素，直到这个集合成为一个空集。为生成一个备选的testcase，我们需要50个候选的testcase。在生成这50个testcase前，首先要确定一个属性以及相应的值，这个值是被测对象在目前的尚未覆盖的二元属性值对中出现频次最高的。在寻找到这个值后，我们会固定这个属性为第一个属性，然后打乱剩余的属性50次，每次依次确定每个属性的值，确定方式为，遍历每个属性的每个值，寻找一个可以和已经确定的值覆盖最多的尚未覆盖的二元属性值对的属性值。寻找到所有属性的值后完成一个备选testcase的创建。创建完50个备选testcase后，需要在这50个备选testcase中挑选一个最优的，筛选条件为在这50个候选testcase中，寻找可以覆盖最多尚未覆盖的二元属性值对的case，并把他作为一个testcase确定下来，在尚未覆盖的二元属性值对集合中删除该testcase可以覆盖的属性对，重复整个过程直到未覆盖的二元属性值对集合成为一个空集。



### 2.核心代码

#### 1.类的定义

* Factor

  该类用于记录被测对象的一个属性，使用一个String记录属性的名字，使用一个ArrayList记录属性的值的列表

  ```java
  public class Factor {
      public ArrayList<String> values;  //属性的值
      public String name;  //属性
  }
  ```

  

* Test_case

  该类用于记录本次测试的一个测试用例，使用一个hashmap记录该测试用例的属性-值键值对

  ```java
  public class Test_case {
      public HashMap<String, String> record; //记录本条testcase的属性-值对
  }
  ```

  

* pair_array

  该类用两个String记录被测对象的一个二元属性值对，每一个String都是被测对象的一个属性的值，两个值不会为同一种属性

  ```java
  public class pair_array {
      private String config1;
      private String config2;
  }
  ```

  



#### 2.关键代码

* 初始化本次测试所有需要覆盖的二元属性值对

  ```java
  for (int i = 0; i < factors.size(); i++) {
              for (int j = i + 1; j < factors.size(); j++) {
                  for (String config1 : factors.get(i).values) {
                      for (String config2 : factors.get(j).values) {
                          pair_array temp = new pair_array(config1, config2);
                          uncovered.add(temp); //将这个属性对添加到未覆盖的map
                      }
                  }
              }
          }
  ```

  

* 将属性值的顺序进行一个随机排序

  ```java
  for (int i = 0; i < factors.size() - 1; i++) {
                      int p = random.nextInt(i + 1);
                      int tmp = seq[i];
                      seq[i] = seq[p];
                      seq[p] = tmp;
                  }
  ```



* 生成一个候选testcase

  ```java
  //依次遍历每一个属性，寻找能覆盖最多的值
                  //最外层循环，遍历每个属性
                  for (int i = 0; i < seq.length; i++) {
                      String tempvalue = factors.get(seq[i]).values.get(0);
                      int maxcover = 0;
                      //第二层循环，遍历每个属性值,寻找能覆盖最多的
                      for (String s : factors.get(seq[i]).values) {
                          int sum = 0;
                          //第三层循环：遍历每个pair，检测是否包含这个属性值和已有的属性值
                          for (pair_array a : uncovered) {
                              for (Map.Entry<String, String> entry : tempcase.record.entrySet()) {
                                  if (a.containsboth(entry.getValue(), s))
                                      sum++;
                              }
                          }
                          if (sum > maxcover) {
                              maxcover = sum;
                              tempvalue = s;
                          }
                      }
                      //在这条testcase添加这个属性以及相应的测试值
                      tempcase.record.put(factors.get(seq[i]).name, tempvalue);
                  }
                  candidatecase.add(tempcase);
              }
  ```



* 从候选testcase中选择最优的testcase

  ```java
  for (Test_case c : candidatecase) {
                  int sumcover = 0;
                  ArrayList<String> candidatevalue = new ArrayList<>();
                  for (Map.Entry<String, String> entry : c.record.entrySet()) {
                      candidatevalue.add(entry.getValue());
                  }
  
                  //循环每一个属性值的二元组
                  for (int i = 0; i < candidatevalue.size(); i++) {
                      for (int j = i; j < candidatevalue.size(); j++) {
                          //循环每一个pair检查是否包含
                          for (pair_array pair : uncovered) {
                              if (pair.containsboth(candidatevalue.get(i), candidatevalue.get(j)))
                                  sumcover++;
                          }
                      }
                  }
                  if (sumcover > bestcover) {
                      bestcover = sumcover;
                      bestcase = c;
                  }
              }
  ```

  

* 遍历未覆盖的二元属性值对集合，去掉已覆盖的二元属性值对

  ```java
  for (int i = uncovered.size() - 1; i >= 0; i--) {
                  pair_array temp = uncovered.get(i);
                  for (int j = 0; j < tempvalue.size(); j++) {
                      for (int k = j + 1; k < tempvalue.size(); k++) {
                          if (temp.containsboth(tempvalue.get(j), tempvalue.get(k)))
                              uncovered.remove(i);
                      }
                  }
              }
  ```

  



### 3.测试

#### 测试样例1：

输入：

```
A1:a1,a2,a3
B1:b1,b2
C1:c1,c2,c3,c4
D1:d1,d2,d3,d4
```

输出：

```
A1 D1 C1 B1 
a1 d1 c1 b1 
a2 d1 c2 b2 
a3 d2 c2 b1 
a1 d2 c3 b2 
a2 d3 c4 b1 
a3 d4 c1 b2 
a2 d4 c3 b1 
a3 d1 c4 b2 
a1 d3 c2 b2 
a2 d2 c1 b1 
a3 d3 c3 b1 
a1 d4 c4 b1 
a1 d3 c1 b1 
a1 d4 c2 b1 
a1 d1 c3 b1 
a1 d2 c4 b1 
```



#### 测试样例2：

输入：

```
品牌:hp,ThinkPad,HUAWEI,Lenovo,DELL,APPLE
能效等级:一级能耗,二级能耗,五级能耗
显卡:MX230,RTX3070Ti,RTX3080Ti
处理器:麒麟,AMD速龙,inteli5
类型:轻薄本,游戏本,全能本
是否支持IPV6:支持IPV6,不支持IPV6
```

输出：

```
品牌 能效等级 是否支持IPV6 处理器 显卡 类型 
hp 一级能耗 支持IPV6 麒麟 MX230 轻薄本 
hp 二级能耗 不支持IPV6 AMD速龙 游戏本 RTX3070Ti 
ThinkPad 五级能耗 不支持IPV6 inteli5 轻薄本 RTX3080Ti 
HUAWEI 二级能耗 支持IPV6 inteli5 全能本 MX230 
Lenovo 五级能耗 支持IPV6 AMD速龙 MX230 游戏本 
Lenovo 一级能耗 不支持IPV6 麒麟 全能本 RTX3070Ti 
DELL 一级能耗 支持IPV6 inteli5 游戏本 RTX3080Ti 
APPLE 一级能耗 支持IPV6 AMD速龙 RTX3070Ti 轻薄本 
ThinkPad 二级能耗 支持IPV6 麒麟 游戏本 RTX3080Ti 
HUAWEI 五级能耗 不支持IPV6 麒麟 RTX3070Ti 轻薄本 
DELL 二级能耗 不支持IPV6 麒麟 轻薄本 MX230 
APPLE 二级能耗 不支持IPV6 麒麟 游戏本 MX230 
hp 五级能耗 支持IPV6 AMD速龙 全能本 RTX3080Ti 
ThinkPad 一级能耗 支持IPV6 AMD速龙 MX230 全能本 
HUAWEI 一级能耗 支持IPV6 AMD速龙 游戏本 RTX3080Ti 
Lenovo 二级能耗 支持IPV6 inteli5 轻薄本 RTX3070Ti 
DELL 五级能耗 支持IPV6 AMD速龙 RTX3070Ti 全能本 
APPLE 五级能耗 支持IPV6 inteli5 全能本 RTX3080Ti 
hp 一级能耗 支持IPV6 inteli5 MX230 轻薄本 
ThinkPad 一级能耗 支持IPV6 麒麟 轻薄本 RTX3070Ti 
Lenovo 一级能耗 支持IPV6 麒麟 RTX3080Ti 轻薄本 
```



#### 测试结果：

本程序基本实现2-wayAETG算法，算法时间复杂度和空间复杂度尚可接受，且经过测试，对比未使用50个备选case只随机排序各属性一次和使用50个备选case挑选最优case的情况，使用备选case可以使项目输出的稳定性显著提升，特别是当被测对象拥有较少属性，每个属性的值较多时，使用这种算法程序输出的稳定性与效率优势较为明显。



### 4.扩展：3-way

未实现3-wayAETG，核心的修改是需要将二元属性值对修改为三元属性值对，且每次遍历寻找尽可能多的可覆盖的属性值对是，需要将双重循环改为三重循环，但算法主要思想和主题部分没有大的变动，因此3-wayAETG完全可以在本项目的基础上实现。