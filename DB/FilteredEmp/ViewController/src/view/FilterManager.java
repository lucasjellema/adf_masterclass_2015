package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.component.UIComponent;

import java.util.Map;

import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.adfinternal.view.faces.model.binding.FacesCtrlHierNodeBinding;

import oracle.binding.BindingContainer;

import oracle.jbo.Row;
import oracle.jbo.uicli.binding.JUFormBinding;
import oracle.jbo.uicli.binding.JUIteratorBinding;

public class FilterManager {

    private String filterIterator;
    private String tofilterIterator;
    private List<String> componentsToRefresh;
    private Map<String, Map> selectedFilters = new HashMap<String, Map>();

    public FilterManager() {
    }

    private void refreshFilterAndTable() {
        for (String component : componentsToRefresh) {
            partiallyRefreshComponent(component);
        }
    }

    private void partiallyRefreshComponent(String componentId) {
        UIComponent component =
            FacesContext.getCurrentInstance().getViewRoot().findComponent(componentId);
        if (component != null) {
            AdfFacesContext context = AdfFacesContext.getCurrentInstance();
            context.addPartialTarget(component);
        }
    }

    private void refresh() {
        String whereClause = "159=159 ";
        JUFormBinding bc =
            (JUFormBinding)BindingContext.getCurrent().getCurrentBindingsEntry();
        JUIteratorBinding filterIter = bc.findIterBinding(filterIterator);
        // reset where clause parameters
        Object[] params = filterIter.getViewObject().getWhereClauseParams();
        for (Object param : params) {
            filterIter.getViewObject().setNamedWhereClauseParam((String)((Object[])param)[0], null);
        }
        for (String filterName : selectedFilters.keySet()) {
            whereClause = whereClause.concat(" and ".concat(((String)selectedFilters.get(filterName).get("FilterWhereClause")).replaceFirst(":" +
                             (String)selectedFilters.get(filterName).get("FilterBindParameter"),
                             (String)selectedFilters.get(filterName).get("FilterValue"))));
            filterIter.getViewObject().setNamedWhereClauseParam((String)selectedFilters.get(filterName).get("FilterBindParameter"),
                                                                selectedFilters.get(filterName).get("FilterValue"));
        }
        // set where clause on FilterIterator and FilterableIterator
        JUIteratorBinding toFilter = bc.findIterBinding(tofilterIterator);
        toFilter.getViewObject().setWhereClause(whereClause);
        // execute query for both the Filters and the ToBeFiltered iterator
        toFilter.executeQuery();
        filterIter.executeQuery();
        // refresh page components
        refreshFilterAndTable();
    }

    public void setSelectFilterValue(FacesCtrlHierNodeBinding filterValue) {
        // find out which filter, filter value and where clause
        Row row = filterValue.getRow();
        Map selectedFilter = new HashMap();
        selectedFilter.put("FilterValue", row.getAttribute("FilterValue"));
        selectedFilter.put("FilterWhereClause",
                           row.getAttribute("WhereClause"));
        selectedFilter.put("FilterBindParameter",
                           row.getAttribute("BindParameter"));
        selectedFilters.put((String)row.getAttribute("Filter"),
                            selectedFilter);

        refresh();
    }

    public void setFilterIterator(String filterIterator) {
        this.filterIterator = filterIterator;
    }

    public String getFilterIterator() {
        return filterIterator;
    }

    public void setTofilterIterator(String tofilterIterator) {
        this.tofilterIterator = tofilterIterator;
    }

    public String getTofilterIterator() {
        return tofilterIterator;
    }

    public void setSelectedFilters(Map<String, Map> selectedFilters) {
        this.selectedFilters = selectedFilters;
    }

    public Map<String, Map> getSelectedFilters() {
        return selectedFilters;
    }

    public void setResetAllFilters(Object o) {
        selectedFilters = new HashMap<String, Map>();
        refresh();
    }

    public void setRemoveFilter(String filterName) {
        selectedFilters.remove(filterName);
        refresh();
    }

    public void setComponentsToRefresh(List<String> componentsToRefresh) {
        this.componentsToRefresh = componentsToRefresh;
    }

    public List<String> getComponentsToRefresh() {
        return componentsToRefresh;
    }
}
