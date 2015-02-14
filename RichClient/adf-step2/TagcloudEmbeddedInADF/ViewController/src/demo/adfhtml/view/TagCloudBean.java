package demo.adfhtml.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.render.ClientEvent;
import oracle.adf.view.rich.util.ResetUtils;

import org.apache.myfaces.trinidad.util.ComponentReference;


public class TagCloudBean {

    private static final ADFLogger _logger =
        ADFLogger.createADFLogger(TagCloudBean.class);


    private OTNBridge otnBridge;

    private ComponentReference tagCloudUIComponent;
    private String mostRecentMessage ="No messages received yet";


    public void tagcloudEvent(ClientEvent evt) {
        ResetUtils.reset(getTagCloudUIComponent());

        Map<String, Object> params = evt.getParameters();
        _logger.log("Received message from " + params.get("id"));
        Map<String, String> msg = (Map<String, String>)params.get("msg");
        String tagId = msg.get("clicked");
        _logger.log("Clicked tag: " + tagId);
        setMostRecentMessage("Clicked tag: " + tagId);
        AdfFacesContext.getCurrentInstance().addPartialTarget(getTagCloudUIComponent().findComponent("log"));
    }

    public String getInitialise() {
        getOtnBridge().getInitialiseBridge();
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("Elephant",35));
        tags.add(new Tag("Lion",65));
        tags.add(new Tag("Zebra",21));
        tags.add(new Tag("Rhinoceros",95));
        tags.add(new Tag("Tiger",12));
        tags.add(new Tag("Mammoth",3));
        getOtnBridge().toGuest("tc1", getTagsAsJsonMessage(tags));
        return null;
    }
    private String getTagsAsJsonMessage(List<Tag> tags) {
        StringBuilder sb = new StringBuilder("{tags:[");
        for (Tag tag : tags) {
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
        return sb.toString();
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

}
