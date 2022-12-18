import java.io.*;
import java.util.*;
import java.util.Random;


public class Main {
    public final static String inputfilepath = "D:\\grade3.1\\软件测试与保护\\AETG\\testcase2.txt";
    public final static String outputfilepath = "D:\\grade3.1\\软件测试与保护\\AETG\\output.txt";
    public final static int M = 50;  //定义每寻找一个testcase需要M个备选用
    public final static int factormaxnum = 20; //定义被测对象属性数量的最大值


    public static void main(String[] args) throws IOException {

        //读取文件
        File fileinput = new File(inputfilepath);
        FileInputStream fis = new FileInputStream(fileinput); //文件流读取
        BufferedReader br = new BufferedReader(new InputStreamReader(fis, "GBK"));
        String line;
        Random random = new Random(); // 随机种子

        ArrayList<Factor> factors = new ArrayList<>(); //存储被测对象的属性集
        ArrayList<pair_array> uncovered = new ArrayList<>();  //记录未覆盖的二元组
        ArrayList<Test_case> mytestcase = new ArrayList<>(); //记录我已生成的test_case集合

        while ((line = br.readLine()) != null) {
            String[] sentence;

            sentence = line.split("[:,]");
            Factor temp = new Factor(sentence[0]);
            for (int i = 1; i < sentence.length; i++) {
                temp.values.add(sentence[i]);
            }
            factors.add(temp);
        }
        br.close();

        //将所有未被覆盖的二元组存储起来
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

        //循环直到所有二元组都被覆盖
        while (true) {
            //寻找所有的频次表中出现最多的属性和值
            String maxfactor = factors.get(0).name;
            String maxvalue = factors.get(0).values.get(0);
            int maxnum = 0;
            int selectedindex = -1;
            for (Factor f : factors) {
                for (String s : f.values) {
                    int sum = 0;
                    for (pair_array a : uncovered) {
                        if (a.contains(s) == true) {
                            sum++;
                        }
                    }
                    if (sum > maxnum) {
                        maxnum = sum;
                        maxfactor = f.name;
                        maxvalue = s;
                        selectedindex = factors.indexOf(f);
                    }
                }
            }

            //开始50次循环，每次将剩下的属性随机排序成一个序列，生成50条候选的testcase
            ArrayList<Test_case> candidatecase = new ArrayList<>();
            for (int n = 0; n < M; n++) {
                Test_case tempcase = new Test_case();
                tempcase.record.put(maxfactor, maxvalue);

                int[] seq = new int[factormaxnum];
                int m = 0;
                for (int i = 0; i < factors.size(); i++) {
                    if (i != selectedindex) {
                        seq[m] = i;
                        m++;
                    }
                }
                for (int i = 0; i < factors.size() - 1; i++) {
                    int p = random.nextInt(i + 1);
                    int tmp = seq[i];
                    seq[i] = seq[p];
                    seq[p] = tmp;
                }

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

            Test_case bestcase = new Test_case();
            int bestcover = 0;

            //从50条候选的testcase中寻找最优的case
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

            //修改未覆盖的二元组，去掉best_case可以覆盖的二元组
            ArrayList<String> tempvalue = new ArrayList<>();
            for (Map.Entry<String, String> entry : bestcase.record.entrySet()) {
                tempvalue.add(entry.getValue());
            }
            //需要从后往前遍历，才能正确删除已经被覆盖的二元组
            for (int i = uncovered.size() - 1; i >= 0; i--) {
                pair_array temp = uncovered.get(i);
                for (int j = 0; j < tempvalue.size(); j++) {
                    for (int k = j + 1; k < tempvalue.size(); k++) {
                        if (temp.containsboth(tempvalue.get(j), tempvalue.get(k)))
                            uncovered.remove(i);
                    }
                }
            }

            mytestcase.add(bestcase);
            if (uncovered.size() == 0)
                break;
        }

        File outputfile = new File(outputfilepath);
        FileOutputStream fos = new FileOutputStream(outputfile); //文件流读取
        BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(fos, "GBK"));
        for (Map.Entry<String, String> entry : mytestcase.get(0).record.entrySet()) {
            bf.write(entry.getKey() + " ");
        }
        bf.write("\n");
        for (Test_case outputcase : mytestcase) {
            for (Map.Entry<String, String> entry : outputcase.record.entrySet()) {
                bf.write(entry.getValue() + " ");
            }
            bf.write("\n");
        }
        bf.close();
        System.out.printf("The size of the cover-array is %d",mytestcase.size());

    }
}

