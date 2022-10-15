

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Java8 {

    public static void main(String[] args) {
//        List<String> list = Arrays.asList("aa","bb","cc","da");
//        List<String> list1 = equalsStr(list, new Predicate<String>() {
//            @Override
//            public boolean test(String s) { //给断言定规则
//
//                return s.contains("a");
//            }
//        });
//        List<String> list2 = equalsStr(list, s -> s.contains("a"));
//
//        System.out.println(list2.toString());
//        Consumer<String > consumer0 = new Consumer<String>() {
//            @Override
//            public void accept(String s) {
//                System.out.println(s);
//            }
//        };
//        consumer0.accept("你好a,我是老版本的写法！");
//
//        Consumer<String > consumer = s -> System.out.println(s);
//        consumer.accept("你好！我是拉姆达的写法！");
//
//        Consumer<String > consumer1 = System.out::println;
//        consumer1.accept("你好！我是牛逼的写法");
//        System.out.println("***************************************************");
//
//        Supplier<User> supplier0 = new Supplier<User>() {
//            @Override
//            public User get() {
//
//                return new User("APACHE");
//            }
//        };
//        System.out.println("我是原始的方法："+supplier0.get().toString());
//
//        Supplier<User> supplier = () -> new User("ss");
//        System.out.println("我是原始的方法："+supplier0.get().toString());
//
//        Supplier<User> supplier1 = User::new;
//        System.out.println("我是原始的方法："+supplier0.get().toString());
//        System.out.println("***************************************************");
//        Comparable<String > comparable = new Comparable<String>() {
//            @Override
//            public int compareTo(String o) {
//
//                return Integer.parseInt(o);
//            }
//        };
//        System.out.println(comparable.compareTo("23"));
//
//        Comparable<String > comparable1=o -> Integer.parseInt(o);
//        System.out.println(comparable1.compareTo("23"));
//
//        Comparable<String > comparable2 = Integer::parseInt;
//        System.out.println(comparable2.compareTo("23"));
//        System.out.println("***************************************************");
//        User user = new User("ss");
//        Function<User,String> function = new Function<User, String>() {
//            @Override
//            public String apply(User user) {
//
//                return user.getName();
//            }
//        } ;
//        System.out.println(function.apply(user));
//
//        Function<User,String> function1 = (user1)->user.getName();
//        System.out.println(function1.apply(user));
//
//        Function<User,String> function2 = User::getName;
//        System.out.println(function2.apply(user));
//        System.out.println("***************************************************");
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

    //给定一个一个规则，过滤集合中的字符，此规则由Predicate的方法决定
    public static void andTest() {

        Consumer<String > consumer = s -> System.out.println("你好！");

        Consumer<String > consumer1 = System.out::println;
        consumer1.accept("你好！");
    }

}

class User{

    private   String name;
    private List<Student> students;
    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }


    public User(String name, List<Student> students) {
        this.name = name;
        this.students = students;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    }

