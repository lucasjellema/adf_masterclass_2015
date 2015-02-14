package demo.adfhtml.view;

import java.util.ArrayList;
import java.util.List;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.logging.ADFLogger;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;


public class MatchHelperBean {
    
    private oracle.jbo.domain.Number selectedMatchId;
    private String filteringTags;
    
    
    ADFLogger _logger = ADFLogger.createADFLogger(this.getClass());
    public MatchHelperBean() {
        super();
    }

    public void setSelectedMatchId(Number selectedMatchId) {
        _logger.warning("Match has been selected ");
        _logger.info("Match has been selected "+(selectedMatchId==null?"null": selectedMatchId.toString()));
              this.selectedMatchId = selectedMatchId;
    }

    public Number getSelectedMatchId() {
        _logger.fine("MatchId has been retrieved;now log with the current match id ");
        if (selectedMatchId!=null) {
        _logger.fine("MatchId has been retrieved "+selectedMatchId==null?"null": selectedMatchId.toString());
        }
        return selectedMatchId;
    }

    public void setFilteringTags(String filteringTags) {
        _logger.warning("filtering tag selection has been updated to"+filteringTags+"; go and refresh bind variables ");
        this.filteringTags = filteringTags;

        // get reference to the parent view object via the iterator binding
        DCIteratorBinding iter = ADFHelper.findIterator("RemainingMatches1Iterator");
        ViewObject remainingMatchesVO = iter.getViewObject();
        // set bind parameter value on parent view object
        remainingMatchesVO.ensureVariableManager().setVariableValue("b_selected_tags",      filteringTags); 
        iter.executeQuery();

        // get reference to the parent view object via the iterator binding
        DCIteratorBinding iterTags = ADFHelper.findIterator("RemainingTagCloudViewObj1Iterator");
        ViewObject remainingTagCloudVO = iterTags.getViewObject();
        // set bind parameter value on parent view object
        remainingTagCloudVO.ensureVariableManager().setVariableValue("b_selected_tags",      filteringTags); 
        iterTags.executeQuery();

        // now tell the world about the changed remaining tagcloud - now that the set of matches has been changed
        
        BindingContext bindingContext = BindingContext.getCurrent();
        BindingContainer bindingContainer = bindingContext.getCurrentBindingsEntry();
        OperationBinding binding = bindingContainer.getOperationBinding("publishAvailableTagsCollectionChangedEvent");
        _logger.warning(" operation binding is found "+binding);
        binding.getParamsMap().put("payload", getTags());
        binding.execute();
        _logger.warning("AvailableTagsCollectionChangedEvent has been published");

    }

    public List<Tag> getTags() {
        List<Tag> tags = new ArrayList<Tag>();
        DCIteratorBinding tagiter = ADFHelper.findIterator("RemainingTagCloudViewObj1Iterator");
        Row[] rows = tagiter.getAllRowsInRange();  
        
        for (Row row : rows) {  
            tags.add(new Tag((String)row.getAttribute("Tag"), ((oracle.jbo.domain.Number)row.getAttribute("Occurrences")).intValue(), (Boolean)true));            
        } 
        
        return tags;
    }


    public String getFilteringTags() {
        return filteringTags;
    }
}
