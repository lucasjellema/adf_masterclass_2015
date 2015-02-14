package nl.amis.hrm.model.common;

import org.eclipse.persistence.sdo.SDODataObject;

@SuppressWarnings("serial")
public class EmployeesViewSDOImpl extends SDODataObject implements EmployeesViewSDO {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 12;

   public EmployeesViewSDOImpl() {}

   public java.lang.Integer getEmployeeId() {
      return getInt(START_PROPERTY_INDEX + 0);
   }

   public void setEmployeeId(java.lang.Integer value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getFirstName() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setFirstName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public java.lang.String getLastName() {
      return getString(START_PROPERTY_INDEX + 2);
   }

   public void setLastName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.lang.String getEmail() {
      return getString(START_PROPERTY_INDEX + 3);
   }

   public void setEmail(java.lang.String value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.lang.String getPhoneNumber() {
      return getString(START_PROPERTY_INDEX + 4);
   }

   public void setPhoneNumber(java.lang.String value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.sql.Timestamp getHireDate() {
      return (java.sql.Timestamp)get(START_PROPERTY_INDEX + 5);
   }

   public void setHireDate(java.sql.Timestamp value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }

   public java.lang.String getJobId() {
      return getString(START_PROPERTY_INDEX + 6);
   }

   public void setJobId(java.lang.String value) {
      set(START_PROPERTY_INDEX + 6 , value);
   }

   public java.math.BigDecimal getSalary() {
      return getBigDecimal(START_PROPERTY_INDEX + 7);
   }

   public void setSalary(java.math.BigDecimal value) {
      set(START_PROPERTY_INDEX + 7 , value);
   }

   public java.math.BigDecimal getCommissionPct() {
      return getBigDecimal(START_PROPERTY_INDEX + 8);
   }

   public void setCommissionPct(java.math.BigDecimal value) {
      set(START_PROPERTY_INDEX + 8 , value);
   }

   public java.lang.Integer getManagerId() {
      return getInt(START_PROPERTY_INDEX + 9);
   }

   public void setManagerId(java.lang.Integer value) {
      set(START_PROPERTY_INDEX + 9 , value);
   }

   public java.lang.Integer getDepartmentId() {
      return getInt(START_PROPERTY_INDEX + 10);
   }

   public void setDepartmentId(java.lang.Integer value) {
      set(START_PROPERTY_INDEX + 10 , value);
   }

   public java.util.List getEmployeesView() {
      return getList(START_PROPERTY_INDEX + 11);
   }

   public void setEmployeesView(java.util.List value) {
      set(START_PROPERTY_INDEX + 11 , value);
   }

   public java.util.List getDepartmentsView() {
      return getList(START_PROPERTY_INDEX + 12);
   }

   public void setDepartmentsView(java.util.List value) {
      set(START_PROPERTY_INDEX + 12 , value);
   }


}

