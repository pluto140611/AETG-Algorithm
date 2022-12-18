import java.util.*;

/*
    用于描述用户输入的被测对象的其中一个属性
 */
public class Factor {
    public ArrayList<String> values;  //属性的值
    public String name;  //属性
//    public HashMap<String, Integer> fre; //记录每个属性值出现的频率

    public Factor(String attri) {
        this.name = attri;
        values = new ArrayList<>();
//        fre = new HashMap<>();
    }
}


