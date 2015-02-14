package nl.amis.hrm.model.common;

public interface EmployeesViewSDO extends java.io.Serializable {

   public java.lang.Integer getEmployeeId();

   public void setEmployeeId(java.lang.Integer value);

   public java.lang.String getFirstName();

   public void setFirstName(java.lang.String value);

   public java.lang.String getLastName();

   public void setLastName(java.lang.String value);

   public java.lang.String getEmail();

   public void setEmail(java.lang.String value);

   public java.lang.String getPhoneNumber();

   public void setPhoneNumber(java.lang.String value);

   public java.sql.Timestamp getHireDate();

   public void setHireDate(java.sql.Timestamp value);

   public java.lang.String getJobId();

   public void setJobId(java.lang.String value);

   public java.math.BigDecimal getSalary();

   public void setSalary(java.math.BigDecimal value);

   public java.math.BigDecimal getCommissionPct();

   public void setCommissionPct(java.math.BigDecimal value);

   public java.lang.Integer getManagerId();

   public void setManagerId(java.lang.Integer value);

   public java.lang.Integer getDepartmentId();

   public void setDepartmentId(java.lang.Integer value);

   public java.util.List getEmployeesView();

   public void setEmployeesView(java.util.List value);

   public java.util.List getDepartmentsView();

   public void setDepartmentsView(java.util.List value);


}

