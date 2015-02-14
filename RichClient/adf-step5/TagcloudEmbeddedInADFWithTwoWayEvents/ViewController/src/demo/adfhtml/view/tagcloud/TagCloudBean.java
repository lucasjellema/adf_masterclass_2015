package demo.adfhtml.view.tagcloud;

import demo.adfhtml.view.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import oracle.adf.model.BindingContext;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.render.ClientEvent;
import oracle.adf.view.rich.util.ResetUtils;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import org.apache.myfaces.trinidad.util.ComponentReference;


public class TagCloudBean {

    private static final ADFLogger _logger =
        ADFLogger.createADFLogger(TagCloudBean.class);


    private List<Tag> tags = new ArrayList<Tag>();
    private OTNBridge otnBridge;

    private ComponentReference tagCloudUIComponent;
    private String mostRecentMessage = "No messages received yet";


    public void tagcloudEvent(ClientEvent evt) {
        ResetUtils.reset(getTagCloudUIComponent());

        Map<String, Object> params = evt.getParameters();
        _logger.log("Received message from " + params.get("id"));
        Map<String, String> msg = (Map<String, String>)params.get("msg");
        String tagId = msg.get("clicked");
        _logger.log("Clicked tag: " + tagId);
        publishTagClickedEvent(tagId);

        setMostRecentMessage("Clicked tag: " + tagId);
        AdfFacesContext.getCurrentInstance().addPartialTarget(getTagCloudUIComponent().findComponent("log"));
    }

    public void publishTagClickedEvent(String clickedTag) {
        BindingContext bindingContext = BindingContext.getCurrent();
        BindingContainer bindingContainer =
            bindingContext.getCurrentBindingsEntry();
        OperationBinding binding =
            bindingContainer.getOperationBinding("publishTagClickedEvent");
        binding.getParamsMap().put("payload", clickedTag);
        binding.execute();
    }

    public void handleNewTagEvent(Tag tag, String clientId) {
        if (otnBridge.getClientId().startsWith(clientId)) {
            // handle the event if the client id is our clientid
            tags.add(tag);
            updateTags();
        }
    }


    public String getInitialise() {
        getOtnBridge().getInitialiseBridge();
        updateTags();
        return null;
    }

    private void updateTags() {
        StringBuilder sb = new StringBuilder("{tags:[");
        for (Tag tag : getTags()) {
            if (sb.length() > 7) {
                sb.append(",");
            }
            sb.append("{\"id\":\"");
            sb.append(tag.getTag());
            sb.append("\",\"text\":\"");
            sb.append(tag.getTag()).append(" (").append(tag.getOccurrences()).append(")");
            sb.append("\",\"value\":");
            sb.append(tag.getOccurrences() + 10);
            sb.append("}");
        }
        sb.append("]}");
        getOtnBridge().toGuest("tc1", sb.toString());
    }


    public void setOtnBridge(OTNBridge otnBridge) {
        this.otnBridge = otnBridge;
    }

    public OTNBridge getOtnBridge() {
        return otnBridge;
    }


    public void setMostRecentMessage(String mostRecentMessage) {
        this.mostRecentMessage = mostRecentMessage;
    }

    public String getMostRecentMessage() {
        return mostRecentMessage;
    }

    public UIComponent getTagCloudUIComponent() {
        return tagCloudUIComponent == null ? null :
               tagCloudUIComponent.getComponent();
    }

    public void setTagCloudUIComponent(UIComponent tagCloudComponent) {
        tagCloudUIComponent =
                ComponentReference.newUIComponentReference(tagCloudComponent);
        getOtnBridge().setRoot(tagCloudComponent);
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getTags() {
        return tags;
    }

}
