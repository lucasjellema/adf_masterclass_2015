package demo.adfhtml.view;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.binding.BindingContainer;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;


public class ADFHelper {

    /**
     * Find an iterator binding in the current binding container by name.
     *
     * @param name iterator binding name
     * @return iterator binding
     */
    public static DCIteratorBinding findIterator(String name) {
        return getDCBindingContainer().findIteratorBinding(name);
    }

    /**
     * Return the current page's binding container.
     * @return the current page's binding container
     */
    public static BindingContainer getBindingContainer() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    /**
     * Return the Binding Container as a DCBindingContainer.
     * @return current binding container as a DCBindingContainer
     */
    public static DCBindingContainer getDCBindingContainer() {
        return (DCBindingContainer)getBindingContainer();
    }

    public static Object evaluateEL(String el) {
        return ADFContext.getCurrent().getExpressionEvaluator().evaluate(el);
    }

    public static void sendJavascript(String js) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExtendedRenderKitService erks =
            Service.getRenderKitService(context, ExtendedRenderKitService.class);
        erks.addScript(context, js);
    }
}


