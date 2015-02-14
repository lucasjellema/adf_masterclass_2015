package demo.adfhtml.view;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.myfaces.trinidad.util.ComponentReference;


public class OTNBridge {

    private ComponentReference root;

    public UIComponent getRoot() {
        return root == null ? null : root.getComponent();
    }

    public void setRoot(UIComponent comp) {
        root = ComponentReference.newUIComponentReference(comp);
    }

    public String getInitialiseBridge() {
        String clientId = getClientId();
        if (clientId != null) {
            ADFHelper.sendJavascript("bootstrapGuestModules('" + clientId +
                                     "');");
        }
        return null;
    }

    private String getClientId() {
        UIComponent root = getRoot();
        return root == null ? null :
               root.getClientId(FacesContext.getCurrentInstance());
    }

    public void toGuest(String guestId, String message) {
        String clientId = getClientId();
        if (clientId != null) {
            ADFHelper.sendJavascript("sendMessageToGuest('" + clientId +
                                     "', '" + guestId + "', " + message +
                                     ");");
        }
    }
}
