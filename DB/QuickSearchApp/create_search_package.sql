create or replace 
package ot_search as 

  procedure create_emp_search_item 
  ( p_rowid in rowid  
  , p_out in out nocopy varchar2
  );
    
  function get_emp_rowid 
  ( p_id in employees.employee_id%type  
  ) return rowid;
  
  function get_emp_search_item 
  ( p_rowid in rowid  
  ) return varchar2;
  
  procedure create_dep_search_item 
  ( p_rowid in rowid  
  , p_out in out nocopy varchar2
  );
  
  function get_dep_rowid 
  ( p_id in departments.department_id%type  
  ) return rowid;
  
  function get_dep_search_item 
  ( p_rowid in rowid  
  ) return varchar2;
  
end ot_search;
/

create or replace 
package body ot_search as

  procedure create_emp_search_item 
  ( p_rowid in rowid  
  , p_out in out nocopy varchar2
  ) as
  begin
    p_out := get_emp_search_item(p_rowid);
  end create_emp_search_item;
    
  function get_emp_rowid 
  ( p_id in employees.employee_id%type  
  ) return rowid as 
  begin
    for r in (select rowid from employees where employee_id = p_id)
    loop
      return r.rowid;
    end loop;
    return null;
  end get_emp_rowid;
  
  function get_emp_search_item 
  ( p_rowid in rowid  
  ) return varchar2 as 
  begin
    for b in (select e.first_name
                   , e.last_name
                   , e.email
                   , e.phone_number
                   , j.job_title
              from employees e
                   left join jobs j using (job_id)
              where e.rowid = p_rowid)
    loop
      return b.first_name || ' ' || b.last_name || ' (' || b.email || ', ' || b.phone_number || ', ' || b.job_title || ')';
    end loop;
    raise no_data_found;
  end get_emp_search_item;
  
  procedure create_dep_search_item 
  ( p_rowid in rowid  
  , p_out in out nocopy varchar2
  ) as
  begin
    p_out := get_dep_search_item(p_rowid);
  end create_dep_search_item;
  
  function get_dep_rowid 
  ( p_id in departments.department_id%type  
  ) return rowid as 
  begin
    for r in (select rowid from departments where department_id = p_id)
    loop
      return r.rowid;
    end loop;
    return null;
  end get_dep_rowid;
  
  function get_dep_search_item 
  ( p_rowid in rowid  
  ) return varchar2 as 
  begin
    for b in (select d.department_name
                   , e.first_name
                   , e.last_name
              from departments d
                   left join employees e on (d.manager_id = e.employee_id)
              where d.rowid = p_rowid)
    loop
      return b.department_name || ' (' || b.first_name || ' ' || b.last_name || ')';
    end loop;
    raise no_data_found;
  end get_dep_search_item;
end ot_search;
/
