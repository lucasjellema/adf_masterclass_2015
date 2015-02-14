declare
  procedure drop_preference
  ( p_name varchar2
  ) as
  begin
    ctx_ddl.drop_preference(p_name);
  exception
    when others then
      null;
  end;

  procedure drop_stoplist
  ( p_name varchar2
  ) as
  begin
    ctx_ddl.drop_stoplist(p_name);
  exception
    when others then
      null;
  end;

  procedure drop_index
  ( p_name varchar2
  ) as
  begin
    execute immediate 'drop index ' || p_name;
  exception
    when others then
      null;
  end;
begin

  -- Drop old indices...
  drop_index('emp_search_index');
  drop_index('dep_search_index');

  -- Drop old preferences...
  drop_preference('emp_datastore');
  drop_preference('dep_datastore');
  drop_preference('wordlist');
  drop_preference('lexer');
  drop_stoplist('stoplist');

  -- Configure preferences...
  ctx_ddl.create_preference('emp_datastore', 'user_datastore');
  ctx_ddl.set_attribute('emp_datastore', 'procedure', 'ot_search.create_emp_search_item');
  ctx_ddl.set_attribute('emp_datastore', 'output_type', 'varchar2');

  ctx_ddl.create_preference('dep_datastore', 'user_datastore');
  ctx_ddl.set_attribute('dep_datastore', 'procedure', 'ot_search.create_dep_search_item');
  ctx_ddl.set_attribute('dep_datastore', 'output_type', 'varchar2');

  ctx_ddl.create_preference('wordlist','basic_wordlist');
  ctx_ddl.set_attribute('wordlist', 'prefix_index', 'true');

  ctx_ddl.create_preference('lexer', 'basic_lexer');
  ctx_ddl.set_attribute('lexer', 'skipjoins', '.-''');
  ctx_ddl.set_attribute('lexer', 'base_letter', 'true');

  ctx_ddl.create_stoplist('stoplist');
  ctx_ddl.add_stopword('stoplist', 'te');
  ctx_ddl.add_stopword('stoplist', 'vs');

  -- Create the indices...
  execute immediate 'create index emp_search_index on employees(last_name)
                      indextype is ctxsys.context
                      parameters (''datastore emp_datastore wordlist wordlist lexer lexer stoplist stoplist sync (on commit)'')';

  execute immediate 'create index dep_search_index on departments(department_name)
                      indextype is ctxsys.context
                      parameters (''datastore dep_datastore wordlist wordlist lexer lexer stoplist stoplist sync (on commit)'')';
end;