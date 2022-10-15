import java.util.List;

public class Student {

    private String name;
    private String corse;
    private Long id;

    public Student( Long id,String name, String corse) {
        this.name = name;
        this.corse = corse;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCorse() {
        return corse;
    }

    public void setCorse(String corse) {
        this.corse = corse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
