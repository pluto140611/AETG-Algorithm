import java.io.*;
import java.util.*;
import java.util.Random;


public class Main {
    public final static String inputfilepath = "D:\\grade3.1\\��������뱣��\\AETG\\testcase2.txt";
    public final static String outputfilepath = "D:\\grade3.1\\��������뱣��\\AETG\\output.txt";
    public final static int M = 50;  //����ÿѰ��һ��testcase��ҪM����ѡ��
    public final static int factormaxnum = 20; //���屻������������������ֵ


    public static void main(String[] args) throws IOException {

        //��ȡ�ļ�
        File fileinput = new File(inputfilepath);
        FileInputStream fis = new FileInputStream(fileinput); //�ļ�����ȡ
        BufferedReader br = new BufferedReader(new InputStreamReader(fis, "GBK"));
        String line;
        Random random = new Random(); // �������

        ArrayList<Factor> factors = new ArrayList<>(); //�洢�����������Լ�
        ArrayList<pair_array> uncovered = new ArrayList<>();  //��¼δ���ǵĶ�Ԫ��
        ArrayList<Test_case> mytestcase = new ArrayList<>(); //��¼�������ɵ�test_case����

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

        //������δ�����ǵĶ�Ԫ��洢����
        for (int i = 0; i < factors.size(); i++) {
            for (int j = i + 1; j < factors.size(); j++) {
                for (String config1 : factors.get(i).values) {
                    for (String config2 : factors.get(j).values) {
                        pair_array temp = new pair_array(config1, config2);
                        uncovered.add(temp); //��������Զ���ӵ�δ���ǵ�map
                    }
                }
            }
        }

        //ѭ��ֱ�����ж�Ԫ�鶼������
        while (true) {
            //Ѱ�����е�Ƶ�α��г����������Ժ�ֵ
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

            //��ʼ50��ѭ����ÿ�ν�ʣ�µ�������������һ�����У�����50����ѡ��testcase
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

                //���α���ÿһ�����ԣ�Ѱ���ܸ�������ֵ
                //�����ѭ��������ÿ������
                for (int i = 0; i < seq.length; i++) {
                    String tempvalue = factors.get(seq[i]).values.get(0);
                    int maxcover = 0;
                    //�ڶ���ѭ��������ÿ������ֵ,Ѱ���ܸ�������
                    for (String s : factors.get(seq[i]).values) {
                        int sum = 0;
                        //������ѭ��������ÿ��pair������Ƿ�����������ֵ�����е�����ֵ
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
                    //������testcase�����������Լ���Ӧ�Ĳ���ֵ
                    tempcase.record.put(factors.get(seq[i]).name, tempvalue);
                }
                candidatecase.add(tempcase);
            }

            Test_case bestcase = new Test_case();
            int bestcover = 0;

            //��50����ѡ��testcase��Ѱ�����ŵ�case
            for (Test_case c : candidatecase) {
                int sumcover = 0;
                ArrayList<String> candidatevalue = new ArrayList<>();
                for (Map.Entry<String, String> entry : c.record.entrySet()) {
                    candidatevalue.add(entry.getValue());
                }

                //ѭ��ÿһ������ֵ�Ķ�Ԫ��
                for (int i = 0; i < candidatevalue.size(); i++) {
                    for (int j = i; j < candidatevalue.size(); j++) {
                        //ѭ��ÿһ��pair����Ƿ����
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

            //�޸�δ���ǵĶ�Ԫ�飬ȥ��best_case���Ը��ǵĶ�Ԫ��
            ArrayList<String> tempvalue = new ArrayList<>();
            for (Map.Entry<String, String> entry : bestcase.record.entrySet()) {
                tempvalue.add(entry.getValue());
            }
            //��Ҫ�Ӻ���ǰ������������ȷɾ���Ѿ������ǵĶ�Ԫ��
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
        FileOutputStream fos = new FileOutputStream(outputfile); //�ļ�����ȡ
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

