package demo.adfhtml.view.i18n;

import javax.faces.event.ValueChangeEvent;

public class LocaleBean {
   
   private String language="en";

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void handleLanguageSwitch(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        System.out.println("new language "+valueChangeEvent.getNewValue());
    }
}