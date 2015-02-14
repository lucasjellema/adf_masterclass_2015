package nl.amis.hrm.model.xmldatacontrol;

import org.adfemg.datacontrol.xml.annotation.CalculatedAttr;
import org.adfemg.datacontrol.xml.annotation.ElementCustomization;
import org.adfemg.datacontrol.xml.data.XMLDCElement;

@ElementCustomization(target = "nl.amis.hrm.model.HrmDC.getXML.retrieveDepartmentsResponse.Department.staff")
    public class StaffCustomizer {


@CalculatedAttr
public String getFullName(XMLDCElement staff) {
    return staff.get("firstName") + " " + staff.get("lastName");
}
}
