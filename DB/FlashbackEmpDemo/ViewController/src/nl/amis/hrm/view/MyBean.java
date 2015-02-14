package nl.amis.hrm.view;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.binding.DCBindingContainer;

import oracle.adfinternal.view.faces.model.binding.FacesCtrlActionBinding;

public class MyBean {
    public void changeHistoryWindow(ValueChangeEvent ve) {
            DCBindingContainer bindings =
                (DCBindingContainer)getExpressionValue("#{bindings}");
            FacesCtrlActionBinding queryHistoryView =
                (FacesCtrlActionBinding)bindings.findCtrlBinding("ExecuteWithParams");
            queryHistoryView.getParamsMap().put("bind_flashbacktime",
                                                ve.getNewValue());
            queryHistoryView.execute();
        }

        public Object getExpressionValue(String el) {
            ValueBinding vb =
                FacesContext.getCurrentInstance().getApplication().createValueBinding(el);
            return vb.getValue(FacesContext.getCurrentInstance());
        }}
