package viewnl.amis.hr;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.output.RichOutputFormatted;

import oracle.adf.view.rich.context.AdfFacesContext;

import org.apache.myfaces.trinidad.util.ComponentReference;

public class EmployeeBean {
   private String name;
   private Date birthdate;
   private String country;
   private String function;
   private Integer salary;

private int buttonPressCount=0;

    public int getButtonPressCount() {
        return buttonPressCount;
    }

    // based on http://www.jobinesh.com/2011/06/safely-storing-uicomponent-component.html
    private ComponentReference summaryComponentReference;   

    public UIComponent getSummaryComponent(){
       return summaryComponentReference == null ?
                null : summaryComponentReference.getComponent();
    }

    public void setSummaryComponent(UIComponent component) {
        summaryComponentReference =
            ComponentReference.newUIComponentReference(component);
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getFunction() {
        return function;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Integer getSalary() {
        return salary;
    }

    public void submitDetails(ActionEvent actionEvent) {
        buttonPressCount++;
        AdfFacesContext.getCurrentInstance().addPartialTarget(getSummaryComponent());  
    }

}
