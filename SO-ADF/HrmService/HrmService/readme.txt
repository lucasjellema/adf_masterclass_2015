database definitions - in HR schema:

create or replace type employee_t force as object
(EMPLOYEE_ID    NUMBER(6)    
,FIRST_NAME              VARCHAR2(20) 
,LAST_NAME      VARCHAR2(25) 
, email            VARCHAR2(25) 
,PHONE_NUMBER            VARCHAR2(20) 
,HIRE_DATE        DATE         
,JOB           VARCHAR2(35) 
, SALARY                  NUMBER(8,2)  
);

create or replace type employees_tbl_t as table of employee_t;

create or replace type department_t force as object (
dEPARTMENT_ID   NUMBER(4)    
,DEPARTMENT_NAME VARCHAR2(30) 
,MANAGER employee_t
,LOCATION varchar2(100)
, employees employees_tbl_t
) force;

create or replace type departments_tbl_t as table of department_t;

create or replace 
package hrm_api
as

function get_departments
return departments_tbl_t
;

end hrm_api;
/


create or replace 
package body hrm_api
as

function get_departments
return departments_tbl_t
is
  l_departments departments_tbl_t;
begin
  select department_t(department_id, department_name
  , (select employee_t(  EMPLOYEE_ID,FIRST_NAME,LAST_NAME , email ,PHONE_NUMBER    
                                    ,HIRE_DATE, (select j.job_title from jobs j where j.JOB_id = m.job_id), SALARY) 
     from employees m where m.employee_id = d.manager_id
    )
  , (select l.city||', '||c.country_name  from locations l join countries c on (l.country_id= c.country_id) where l.location_id = d.location_id)
  , (select cast(collect( employee_t(  EMPLOYEE_ID,FIRST_NAME,LAST_NAME , email ,PHONE_NUMBER    
                                    ,HIRE_DATE, (select j.job_title from jobs j where j.JOB_id = e.job_id), SALARY      
  )) as employees_tbl_t) from employees e where e.department_id = d.department_id)
  )
  bulk collect into l_departments
  from  departments d
  ;
  return l_departments ;
end get_departments;
 
end hrm_api;
/

select hrm_api.get_departments 
from dual
/


