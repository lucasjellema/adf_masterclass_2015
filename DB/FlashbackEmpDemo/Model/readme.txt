as SYS:


grant execute on dbms_flashback_archive to scott;
grant execute on dbms_flashback to scott;

CREATE FLASHBACK ARCHIVE DEFAULT one_year TABLESPACE users
  QUOTA 100M RETENTION 1 YEAR;

grant flashback archive on one_year to scott


-- if we want to work with custom history

exec dbms_flashback_archive.create_temp_history_table('SCOTT', 'EMP');
grant FLASHBACK_ARCHIVE_ADMINISTER to SCOTT
-- this statement once in a database instance
-- This will extend mappings to the past so that import of old history can be done. Goes back to 01-JAN-88.
EXEC DBMS_FLASHBACK_ARCHIVE.extend_mappings();

-- after some history has been created by SCOTT in table temp_history:

EXEC DBMS_FLASHBACK_ARCHIVE.IMPORT_HISTORY('SCOTT','EMP');


as SCOTT:

ALTER TABLE emp FLASHBACK ARCHIVE one_year;



creating the past:

insert into temp_history 
(RID , STARTSCN , ENDSCN  , XID, OPERATION
,EMPNO, sal
)
values (NULL, timestamp_to_scn(to_date('01-JAN-06')), timestamp_to_scn(to_date('31-DEC-06')), NULL, 'U', 1, 1000);

commit;


-- create emoployee
-- hired as Clerk in 2001 in dept 10, salary 2200
-- moved still as Clerk in 2003 in dept 20
-- promoted to Salesman in 2006 with new salary 3400
-- salary increated in 2008 to 3800
-- promoted to manager in 2010
-- left the company in 2012


insert into temp_history 
(RID , STARTSCN , ENDSCN  , XID, OPERATION ,EMPNO, ename, job, hiredate, sal, deptno
)
values (NULL, timestamp_to_scn(to_date('01-04-2001', 'DD-MM-YYYY')), timestamp_to_scn(to_date('01-07-2003', 'DD-MM-YYYY')), NULL, 'I'
, 1567, 'SELLERS','CLERK', to_date('01-04-2001', 'DD-MM-YYYY'), 2200, 10);

insert into temp_history 
(RID , STARTSCN , ENDSCN  , XID, OPERATION
,EMPNO, ename, job, hiredate, sal, deptno
)
values (NULL, timestamp_to_scn(to_date('01-07-2003', 'DD-MM-YYYY')), timestamp_to_scn(to_date('01-10-2006', 'DD-MM-YYYY')), NULL, 'U'
, 1567, 'SELLERS','CLERK', to_date('01-04-2001', 'DD-MM-YYYY'), 2200, 20);


insert into temp_history 
(RID , STARTSCN , ENDSCN  , XID, OPERATION
,EMPNO, ename, job, hiredate, sal, deptno
)
values (NULL, timestamp_to_scn(to_date('01-10-2006', 'DD-MM-YYYY')), timestamp_to_scn(to_date('01-04-2008', 'DD-MM-YYYY')), NULL, 'U'
, 1567, 'SELLERS','SALESMAN', to_date('01-04-2001', 'DD-MM-YYYY'), 3400, 20);

insert into temp_history 
(RID , STARTSCN , ENDSCN  , XID, OPERATION
,EMPNO, ename, job, hiredate, sal, deptno
)
values (NULL, timestamp_to_scn(to_date('01-04-2008', 'DD-MM-YYYY')), timestamp_to_scn(to_date('01-06-2010', 'DD-MM-YYYY')), NULL, 'U'
, 1567, 'SELLERS','SALESMAN', to_date('01-04-2001', 'DD-MM-YYYY'), 3800, 20);


insert into temp_history 
(RID , STARTSCN , ENDSCN  , XID, OPERATION
,EMPNO, ename, job, hiredate, sal, deptno
)
values (NULL, timestamp_to_scn(to_date('01-06-2010', 'DD-MM-YYYY')), timestamp_to_scn(to_date('31-10-2012', 'DD-MM-YYYY')), NULL, 'U'
, 1567, 'SELLERS','MANAGER', to_date('01-04-2001', 'DD-MM-YYYY'), 3800, 20);


insert into temp_history 
(RID , STARTSCN , ENDSCN  , XID, OPERATION
,EMPNO, ename, job, hiredate, sal, deptno
)
values (NULL, timestamp_to_scn(to_date('31-10-2012', 'DD-MM-YYYY')), NULL, NULL, 'D'
, 1567, 'SELLERS','MANAGER', to_date('01-04-2001', 'DD-MM-YYYY'), 3800, 20);

commit;


