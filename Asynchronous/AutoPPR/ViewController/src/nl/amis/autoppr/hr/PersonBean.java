package nl.amis.autoppr.hr;

import java.util.Date;

import nl.amis.autoppr.AutoPPRSupport;

public class PersonBean {
    
    private String firstName ="Joe";
    private String lastName;
    private Date birthdate;
    private String town;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }
    private final AutoPPRSupport autoPPRSupport = new AutoPPRSupport();

    public void setLastName(String lastName) {
        this.lastName = lastName;
        autoPPRSupport.notifyConsumers("lastName", lastName);
        autoPPRSupport.notifyConsumers("fullName", firstName+" "+lastName);
    }

    public String getLastName() {
        autoPPRSupport.rememberConsumerForAttribute("lastName");
        return lastName;
    }

    public String getFullName() {
        autoPPRSupport.rememberConsumerForAttribute("fullName");
        return firstName+" "+lastName ;
    }


    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getTown() {
        return town;
    }

    public PersonBean() {
        super();
    }
}
