package view;

import java.util.List;

public class Filter {
    
    private String name;
    private int    recordCount;
    private FilterValue selected;
    private String whereClause;
    private List<FilterValue> filterValues;
    
    public Filter() {
    }


    public String getName() {
        return name;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public FilterValue getSelected() {
        return selected;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public void setSelected(FilterValue selected) {
        this.selected = selected;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public void setFilterValues(List<FilterValue> filterValues) {
        this.filterValues = filterValues;
    }

    public List<FilterValue> getFilterValues() {
        return filterValues;
    }
}
