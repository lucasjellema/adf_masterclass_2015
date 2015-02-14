package adfplus.quicksearch.controller.bean;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.model.AutoSuggestUIHints;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaManager;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;


/**
 * Implements Quick search based on Oracle Text searching.
 */
public class QuickSearchBean implements Serializable {
    private static final ADFLogger sLog = ADFLogger.createADFLogger(QuickSearchBean.class);

    private static final String VC_NAME = "QuickSearchViewCriteria";

    /**
     * The name of the iteratorBinding that is used in the form.
     */
    private String iteratorBindingName;

    /**
     * The name of the iteratorBinding that should be used for the search. Note that this must be a
     * unique ViewUsage in the ApplicationModule (because of the shared iterator state).
     * Currently you must manually duplicate the ViewUsage in the ApplicationModule, but it is
     * of course possible to do this automatically in ADF BC base classes.
     */
    private String searchIteratorBindingName;

    /**
     * The name of the attribute that is marked with ORACLE_TEXT_SEARCH_ATTRIBUTE.
     */
    private String searchAttribute;

    private String searchValue;

    private final Map<String, Key> translations = new HashMap<String, Key>();
    private String lastSearchValue;
    private List<SelectItem> lastSuggestList;

    /**
     * Provides the quick suggestItems.
     *
     * @param facesContext
     * @param hints
     * @return
     */
    public List<SelectItem> suggestItems(FacesContext facesContext, AutoSuggestUIHints hints) {
        return search(hints.getSubmittedValue());
    }

    private synchronized List<SelectItem> search(String searchValue) {
        if (searchValue.equals(lastSearchValue)) {
            sLog.fine("Search equals previous search, not searching.");
        } else {
            sLog.fine("Starting new search for: {0}", searchValue);

            DCIteratorBinding iter = getSearchIteratorBinding();
            applySearchCriteria(iter, searchAttribute, searchValue);

            translations.clear();
            lastSuggestList = new ArrayList<SelectItem>();
            lastSearchValue = searchValue;

            Row[] rows = iter.getAllRowsInRange();
            for (Row row : rows) {
                String description = (String)row.getAttribute(searchAttribute);
                lastSuggestList.add(new SelectItem(description));
                translations.put(description, row.getKey());

                sLog.finest("Found: {0}", description);
            }
            if (rows.length == 1) {
                sLog.fine("Only one result found, accepting \"{0}\" as searchValue.", searchValue);
                translations.put(searchValue, rows[0].getKey());
            }
        }

        return lastSuggestList;
    }

    private synchronized Key getKeyFromSearch(String searchValue) {
        if (!translations.containsKey(searchValue) && !searchValue.equals(lastSearchValue)) {
            search(searchValue);
        }

        return translations.get(searchValue);
    }

    /**
     * Try to find a unique row to jump to based on the current search value. If found, jump.
     *
     * @return
     */
    public String go() {
        String searchValue = getSearchValue();
        if (searchValue == null || searchValue.length() == 0) {
            sLog.fine("SearchValue is empty, skipping search.");
        } else {
            Key key = getKeyFromSearch(searchValue);

            if (key != null) {
                gotoRow(getIteratorBinding(), key);
            }
        }

        return null;
    }

    private void applySearchCriteria(DCIteratorBinding iter, String searchAttribute,
                                     String value) {
        ViewObject vo = iter.getRowSetIterator().getRowSet().getViewObject();

        // Apply view criteria to search.
        ViewCriteria vc = vo.createViewCriteria();
        vc.setName(VC_NAME);
        ViewCriteriaRow vcr = vc.createViewCriteriaRow();
        vcr.ensureCriteriaItem(searchAttribute).setValue(value);
        vc.add(vcr);

        vo.getViewCriteriaManager().applyViewCriteria(vc);
        iter.executeQuery();
    }

    private void gotoRow(DCIteratorBinding iter, Key key) {
        ViewObject vo = iter.getRowSetIterator().getRowSet().getViewObject();

        ViewCriteriaManager vcm = vo.getViewCriteriaManager();
        ViewCriteria vc = vcm.getViewCriteria(VC_NAME);
        if (vc != null) {
            vcm.resetCriteria(vc);
        }

        Row[] rows = vo.findByKey(key, 1);
        if (rows != null && rows.length > 0) {
            vo.setCurrentRow(rows[0]);
        }
    }

    private DCIteratorBinding getIteratorBinding() {
        DCBindingContainer bc =
            (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        return bc.findIteratorBinding(getIteratorBindingName());
    }

    private DCIteratorBinding getSearchIteratorBinding() {
        DCBindingContainer bc =
            (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        return bc.findIteratorBinding(getSearchIteratorBindingName());
    }

    public void setIteratorBindingName(String iteratorBinding) {
        this.iteratorBindingName = iteratorBinding;
    }

    public String getIteratorBindingName() {
        return iteratorBindingName;
    }

    public void setSearchAttribute(String searchAttribute) {
        this.searchAttribute = searchAttribute;
    }

    public String getSearchAttribute() {
        return searchAttribute;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchIteratorBindingName(String searchIteratorBindingName) {
        this.searchIteratorBindingName = searchIteratorBindingName;
    }

    public String getSearchIteratorBindingName() {
        return searchIteratorBindingName;
    }
}
