package demo.adfhtml.view;

import javax.faces.context.FacesContext;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;


public class ADFHelper {

    public static void sendJavascript(String js) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExtendedRenderKitService erks =
            Service.getRenderKitService(context, ExtendedRenderKitService.class);
        erks.addScript(context, js);
    }
}


