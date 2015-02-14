package demo.adfhtml.view.skin;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;

import javax.faces.model.SelectItem;

import oracle.adf.controller.ControllerContext;
import oracle.adf.share.ADFContext;

import org.apache.myfaces.trinidad.skin.Skin;
import org.apache.myfaces.trinidad.skin.SkinFactory;

/*
 * Thanks to Frank Nimphius for this class:  https://blogs.oracle.com/jdevotnharvest/entry/how-to_dynamically_detect_and_list_skin_names_in_a_managed_bean
 */
public class SkinManager {
    public SkinManager() {
        if (skinFamily == null) {
            skinFamily = "skyros";
        }
    }

private String skinFamily ="skyros" ;

    //method referenced from a selectOneChoice f:selectItems component

    public List getSkinChoices() {
        List choices = new ArrayList();
        String skinFamily = null;
        String skinLabel = null;
        SkinFactory sf = SkinFactory.getFactory();
        FacesContext context = FacesContext.getCurrentInstance();
        for (Iterator i = sf.getSkinIds(); i.hasNext(); ) {
            String skinID = (String)i.next();
            Skin skin = sf.getSkin(context, skinID);
            skinFamily = skin.getFamily();
            skinLabel = skinFamily;
            if (skin.getRenderKitId().indexOf("desktop") > 0) {
                choices.add(new SelectItem(skinFamily, skinLabel));
            }
        }
        return choices;
    }

    //method referenced from a selectOneChoice valueChangeListener

    public void onNewSkinSelection(ValueChangeEvent valueChangeEvent) {
        ADFContext adfctx = ADFContext.getCurrent();
        Map sessionScope = adfctx.getSessionScope();
        String selectedSkin = (String)valueChangeEvent.getNewValue(); 
        this.setSkinFamily(selectedSkin);
//        sessionScope.put("skinFamily", (String)valueChangeEvent.getNewValue());
        redirectToSelf();
    }
    //redirect that needs to be performed in case a selected
    //skin shouldbe applied

    private void redirectToSelf() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = fctx.getExternalContext();
        String viewId = fctx.getViewRoot().getViewId();
        ControllerContext controllerCtx = null;
        controllerCtx = ControllerContext.getInstance();
        String activityURL = controllerCtx.getGlobalViewActivityURL(viewId);
        try {
            ectx.redirect(activityURL);
            fctx.responseComplete();
        } catch (IOException e) {
            //Can't redirect
            e.printStackTrace();
            fctx.renderResponse();
        }
    }

    public void setSkinFamily(String skinFamily) {
        this.skinFamily = skinFamily;
    }

    public String getSkinFamily() {
        return skinFamily;
    }
}
