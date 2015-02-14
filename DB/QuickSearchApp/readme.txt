Created with:
- Oracle Database 11g XE (works with 10g)
- Oracle ADF 11g 11.1.1.4

By: Paco van der Linden

Described in: http://www.adfplus.com/2012/05/google-like-search-and-lovs-using.html 

To start application:

- run create_search_package.sql in HR schema, test with: select ot_search.get_emp_search_item(rowid) from employees (should show searchable info for each row)

- Grant execute on ctxsys.CTX_DDL to HR user from local sys account (grant EXECUTE on CTXSYS.CTX_DDL to HR)

- run create_indices.sql, test with: select ot_search.get_dep_search_item(rowid) from departments where contains(department_name, 'ad%') > 0

- Configure the DB connection in JDeveloper.

- Run the project.