package nl.amis.hrm.table;

public class Department {
    public Department(String name, Long id) {
        super();
        this.name = name;
      this.id = id;
      this.value = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }
    protected String name;
  protected Long id;
  protected Long value;

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }
}
