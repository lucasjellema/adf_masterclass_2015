package demo.adfhtml.view.i18n;
import java.io.IOException;

import java.util.Locale;

import javax.el.ELContext;

import javax.el.ExpressionFactory;

import javax.el.ValueExpression;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class LocaleSettingViewHandler extends ViewHandler {

    private final ViewHandler base;
    private final String LANGUAGE_EL_EXPRESSION = "#{localeBean.language}";

    public LocaleSettingViewHandler(ViewHandler base) {
        this.base = base;
    }

    @Override
    public Locale calculateLocale(FacesContext context) {
        String language = (String)evaluateEL(LANGUAGE_EL_EXPRESSION);
        if (language==null) {
          language ="en";
        }
        return new Locale(language);
    }

    public static Object evaluateEL(String el) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        ExpressionFactory expressionFactory =
            facesContext.getApplication().getExpressionFactory();
        ValueExpression exp =
            expressionFactory.createValueExpression(elContext, el,
                                                    Object.class);

        return exp.getValue(elContext);
    }

    public String calculateRenderKitId(FacesContext facesContext) {
        return base.calculateRenderKitId(facesContext);
    }

    public UIViewRoot createView(FacesContext facesContext, String string) {
        return base.createView(facesContext, string);
    }

    public String getActionURL(FacesContext facesContext, String string) {
        return base.getActionURL(facesContext, string);
    }

    public String getResourceURL(FacesContext facesContext, String string) {
        return base.getResourceURL(facesContext, string);
    }

    public void renderView(FacesContext facesContext,
                           UIViewRoot uiViewRoot) throws IOException {
        base.renderView(facesContext, uiViewRoot);

    }

    public UIViewRoot restoreView(FacesContext facesContext, String string) {
        return base.restoreView(facesContext, string);
    }

    public void writeState(FacesContext facesContext) throws IOException {
        base.writeState(facesContext);
    }
}