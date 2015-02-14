package demo.adfhtml.view;

public class Tag {
    private String tag;
    private Integer occurrences;

    public Tag(String tag, Integer occurrences) {
        super();
        this.tag = tag;
        this.occurrences = occurrences;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setOccurrences(Integer occurrences) {
        this.occurrences = occurrences;
    }

    public Integer getOccurrences() {
        return occurrences;
    }
}
