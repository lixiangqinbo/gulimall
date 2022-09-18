import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Java8NewFeatrue {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("aa","bb","cc","da");
        List<String> list1 = equalsStr(list, new Predicate<String>() {
            @Override
            public boolean test(String s) {

                return s.contains("a");
            }
        });
        System.out.println(list1.toString());
    }


    //给定一个一个规则，过滤集合中的字符，此规则由Predicate的方法决定
    public static List<String> equalsStr(List<String> list, Predicate<String> predicate) {
        List<String> list1 = new ArrayList<>();
        for (String s : list) {
            if (predicate.test(s))
                list1.add(s);
        }
        return list1;
    }
}
