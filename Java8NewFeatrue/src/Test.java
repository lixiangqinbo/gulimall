import org.omg.CORBA.portable.InvokeHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) {
//        List<Student> studentList = new ArrayList<>();
//        studentList.add(new Student(1L, "张三", "网页设计，围棋，日语"));
//        studentList.add(new Student(2L, "李四", "网页设计，营养学，篮球"));
//        studentList.add(new Student(3L, "王五", "数学建模，象棋，法语"));
//
//        List<String> collect = studentList.stream().map(student -> student.getCorse().split(","))
//                .flatMap(Arrays::stream).distinct()
//                .collect(Collectors.toList());
//        for (String s : collect) {
//
//        }
//        Order<Integer> integerOrder = new Order<>(100);
//        Integer name = integerOrder.getName();
//        List<String> sss = integerOrder.getT("sss");
        Nike cloth1 = new Nike();
        SupperMan supperMan = new SupperMan();
//        Cloth cloth = new ProxyCloth(cloth1);
//        cloth.productCloth();
        DedecProxy dedecProxy = new DedecProxy();

        Cloth o = (Cloth)dedecProxy.proxyFactory(cloth1);
        o.productCloth();
        Human o1 = (Human)dedecProxy.proxyFactory(supperMan);
        o1.fly();
    }


}

 class Order<Q>{
    private Q name;

     public Order(Q name) {
         this.name = name;
     }

     public Q getName() {
         return name;
     }

     public void setName(Q name) {
         this.name = name;
     }

     public <T> List<T> getT(T t){

         return new ArrayList<T>();
     }
 }

/**
 * 静态代理模式
 */
 interface Cloth{
    void productCloth();
 }

 class ProxyCloth implements  Cloth{

    private Cloth cloth;

     public ProxyCloth(Cloth cloth) {
         this.cloth = cloth;
     }

     @Override
     public void productCloth() {
         cloth.productCloth();
     }
 }

 class Nike implements Cloth{

     @Override
     public void productCloth() {
         System.out.println("妈妈我要穿NIke");
     }
 }

/**
 * 动态代理
 */
interface Human{
     public void fly();
 }

 class SupperMan implements Human{

     @Override
     public void fly() {
         System.out.println("Supper Man Can fly");
     }
 }

 class DedecProxy{


     public Object proxyFactory(Object o){

         Myinterceptor h = new Myinterceptor(o);
         return Proxy.newProxyInstance(o.getClass().getClassLoader(), o.getClass().getInterfaces(), h);
     }

 }

 class Myinterceptor implements InvocationHandler {
     private Object object;

     public Myinterceptor(Object object) {
         this.object = object;
     }

     @Override
     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         Object invoke = method.invoke(object, args);
         return invoke;
     }
 }