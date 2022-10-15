import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Test {

    public static void main(String[] args) {
        // 2020-10-06
        LocalDate now = LocalDate.now();
        //叠加2天=第三天 2022-10-08
        LocalDate next3Day = now.plusDays(2);
        //00:00:00
        LocalTime min = LocalTime.MIN;
        //23:59:59
        LocalTime max = LocalTime.MAX;
        //2020-10-06 00:00:00
        LocalDateTime startTime = LocalDateTime.of(now, min);
        //2022-10-08 23:59:59
        LocalDateTime endTime = LocalDateTime.of(next3Day, max);
        String start = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(startTime);
        String end = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(endTime);
        System.out.println("start=>"+start);
        System.out.println("end=>"+end);
    }
}
