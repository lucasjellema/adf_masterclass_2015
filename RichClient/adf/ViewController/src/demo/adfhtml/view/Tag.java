package demo.adfhtml.view;

public class Tag {
    private String tag;
    private Integer occurrences;
    private Boolean generated;

    public Tag(String tag, Integer occurrences, Boolean generated) {
        super();
        this.tag = tag;
        this.occurrences = occurrences;
        this.generated = generated;
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

    public void setGenerated(Boolean generated) {
        this.generated = generated;
    }

    public Boolean getGenerated() {
        return generated;
    }
}
