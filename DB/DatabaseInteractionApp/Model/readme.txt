create in any schema:


create type social_profile_t as object
( linkedin_account varchar2(100)
, twitter_account  varchar2(100)
, facebook_account varchar2(100)
, msn_account      varchar2(100)
, personal_blog    varchar2(100)
);


create type email_address_t
as object
( mail_address    varchar2(100)
, work_or_private varchar2(1) 
);

create type email_address_tbl
as table of email_address_t;

create or replace 
type person_t force as object
( first_name      varchar2(30)
, last_name       varchar2(30)
, birthdate       date
, gender          varchar2(1)
, social_profile  social_profile_t
, email_addresses email_address_tbl
);

create or replace 
type people_tbl_t as table of person_t;


create or replace 
function get_the_people
return people_tbl_t
is
  l_people people_tbl_t:= people_tbl_t
                          ( person_t( 'John', 'Doe'
                                    , to_date('15-09-1969', 'DD-MM-YYYY'),'M'
									                  , social_profile_t( 'JohnDoe', 'JohnDoe_921','JohnnyDoe', 'jd', null )
												            , email_address_tbl( email_address_t( 'john.doe@thefirm.com','W')
												                               , email_address_t( 'johnnyd.at.home@cheap.mail','P')
																                       )
									                  )
									  );
  l_index number;
begin
  l_people.extend;
  l_people( l_people.last) := person_t( 'Jane', 'Dopey'
                                      , to_date('27-12-1969', 'DD-MM-YYYY'),'F'
                                      , social_profile_t( 'MeJane', 'Snowwhite','GIJane', 'jd2', null )
												              , email_address_tbl( email_address_t( 'jane.dopey@hotmail.com','W')
												                                 , email_address_t( 'janedreamson@mail4all.com','P')
																                         )                                      
                                      );
  l_people(1).birthdate:= to_date('21-05-1971', 'DD-MM-YYYY');
  return l_people;
end get_the_people;
	


select get_the_people 
from dual
/




create or replace package cache_mgr
as

procedure put_in_cache
( p_key    in varchar2
, p_value  in varchar2
);

PROCEDURE reset_context;

end cache_mgr;

create or replace application_context my_app_cache using cache_mgr
/

note: require grant on create any context to 

create or replace package body cache_mgr
as

procedure put_in_cache
( p_key    in varchar2
, p_value  in varchar2
) is
begin
  DBMS_SESSION.SET_CONTEXT( 'my_app_cache',p_key ,p_value);
end put_in_cache;

PROCEDURE reset_context IS
  BEGIN
    DBMS_SESSION.clear_context('my_app_cache');
  END reset_context;

end cache_mgr;

begin
cache_mgr.put_in_cache('some_key', 'my_value');
end; 

select *
from   my_app_cache
/

select sys_context('my_app_cache', 'some_key')
from   dual
/


create or replace
view vemployees
as
select * 
from   employees
where  first_name like nvl(sys_context('my_app_cache', 'firstName'),'')||'%'


begin
cache_mgr.put_in_cache('firstName', 'B');
end; 




create or replace
view vemployees
as
select * 
from   employees as of timestamp (sysdate - nvl(to_number(sys_context('my_app_cache', 'history')),0)) 
where  first_name like nvl(sys_context('my_app_cache', 'firstName'),'')||'%'
and    last_name like nvl(sys_context('my_app_cache', 'lastName'),'')||'%'
and    salary < nvl(sys_context('my_app_cache', 'maxSal'),9999999)
and    salary > nvl(sys_context('my_app_cache', 'minSal'),-1)
and    department_id = nvl(sys_context('my_app_cache', 'department'),department_id)

