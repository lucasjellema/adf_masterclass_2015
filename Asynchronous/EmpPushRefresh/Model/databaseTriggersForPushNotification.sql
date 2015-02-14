create or replace 
procedure push_dml_notification_towebapp(p_table_name in varchar2) 
is
 l_push_url varchar2(100):= 'http://10.10.10.21:8001/EmpPushRefresh-ViewController-context-root/dmleventprocessor';
  req   utl_http.req;
  resp  utl_http.resp;
BEGIN
req := utl_http.begin_request(l_push_url||'?payload='||p_table_name||'_'||to_char(sysdate,'HH24MISS'));
  resp := utl_http.get_response(req);
  utl_http.end_response(resp);
end;



create or replace trigger  emp_as_iud_pushwa 
after insert or update or delete on emp
declare
  l_job_id number;
begin
  -- only push when the transaction is committed
  -- the job is rolled back when the transaction is
  -- todo: add multiplexing
  dbms_job.submit
  ( job => l_job_id 
  , what => 'push_dml_notification_towebapp(''EMP'');'
  );
end;

update emp set sal = sal - 500

-- Flashback Query to undo all salary changes in the last hour
update emp 
set sal = 
(select sal from emp  as of timestamp (systimestamp - 1/24) e_o
where emp.empno = e_o.empno)


-- in promptu nudge from database to middle tier
declare
 l_push_url varchar2(100):= 'http://10.10.10.21:8001/EmpPushRefresh-ViewController-context-root/dmleventprocessor';
  req   utl_http.req;
  resp  utl_http.resp;
BEGIN
req := utl_http.begin_request(l_push_url||'?payload=EMP');
  resp := utl_http.get_response(req);
  utl_http.end_response(resp);
end;


create or replace trigger  dept_as_iud 
after insert or update or delete on dept
declare
  l_job_id number;
begin
  -- only push when the transaction is committed
  -- the job is rolled back when the transaction is
  -- todo: add multiplexing
  dbms_job.submit
  ( job => l_job_id 
  , what => 'push_dml_notification(''DEPT'');'
  );
end;



-- as SYS:

CONN / AS SYSDBA

begin
  dbms_network_acl_admin.create_acl
  ( acl => 'IsoCountriesList.xml',
    description => 'Countries list on www.iso.org',
    principal => 'SCOTT',
    is_grant => true,
    privilege => 'connect',
    start_date => null,
    end_date => null
  );
DBMS_NETWORK_ACL_ADMIN.ADD_PRIVILEGE
  ( acl => 'IsoCountriesList.xml',
    principal => 'SCOTT',
    is_grant => true,
    privilege => 'resolve'
  );
  DBMS_NETWORK_ACL_ADMIN.ASSIGN_ACL
  ( acl => 'IsoCountriesList.xml',
    host => '10.10.10.21'
  );
end;