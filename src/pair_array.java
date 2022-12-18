/*
该类用于记录所有的属性值对，
 */

public class pair_array {
    private String config1;
    private String config2;

    public pair_array(String c1, String c2) {
        config1 = c1;
        config2 = c2;
    }

    //该函数用于检测该二元组是否包含一个属性值
    public boolean contains(String input) {
        if (config1.equals(input) || config2.equals(input)) {
            return true;
        } else
            return false;
    }

    //该函数用于检测该二元组是否包含两个属性值
    public boolean containsboth(String s1, String s2) {
        if (config1.equals(s1) && config2.equals(s2))
            return true;
        else if (config1.equals(s2) && config2.equals(s1))
            return true;
        else
            return false;
    }
}
