import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamAPI {
    public static void main(String[] args) {
        List<Staff> staffs = new ArrayList<>();

        staffs.add(new Staff(1001, "AA", 12,0));
        staffs.add(new Staff(1002, "BB",245,0));
        staffs.add(new Staff(1003, "CC",27,0));
        staffs.add(new Staff(1007, "DD",16,0));
        staffs.add(new Staff(1004, "EE",23,0));

        List<Integer> nums = Arrays.asList(1,3,5,9,0,2,5);
        List<Integer> collect = nums.stream().sorted().collect(Collectors.toList());
        for (Integer integer : collect) {
            System.out.println(integer);
        }
//
//        List<Staff> collect = staffs.stream().filter(staff -> staff.getId() > 1004).map(staff -> {
//            staff.setName("王八蛋");
//            return staff;
//        }).collect(Collectors.toList());
//        for (Staff staff : collect) {
//           // System.out.println(staff.toString());
//        }
        List<Staff> collect1 = staffs.stream().sorted(new Comparator<Staff>() {
            @Override
            public int compare(Staff o1, Staff o2) {
                return o1.getId()-o2.getId();
            }
        }).collect(Collectors.toList());
        for (Staff staff : collect1) {
            System.out.println(staff.toString());
        }

    }
}



class Staff{

    public Staff(int id, String name, int age,int pid) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.pid=pid;
    }

    private int id;
    private String name;
    private int age;
    private int pid;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", pid=" + pid +
                '}';
    }
}

