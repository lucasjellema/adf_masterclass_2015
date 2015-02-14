Steps:

- create Servlet (with get and post section); handle post request
- create Staless EJB and have it injected into Servlet
- create JDBC Data Source on target App Server (jdbc/joesRestaurant)
- inject JDBC Data Source into EJB and perform simple query

next:
- build in delays at every layer
- build log report at every layer
- have order number and meal total specified at bottom layer
- create table (tablenumber, appetizer,drink, main, ordertime, completion time) that EJB inserts into
- create query to that EJB executes to derive meal total
- add trigger to table that performs 'meal preparation' (another delay)


delays:
- servlet (get order to mid office of waiters + fetch meal and bring to table)
- EJB (get order to kitchen + bring meal from kitchen to waiter-area)
- Database (meal preparation)




To make things asynch:

- 
- @Asynchronous //To make only this method async in EJB method (http://satishgopal.wordpress.com/2011/04/24/ejb-3-1-asynchronous-methods/)


Note: max of 2 physical connection for JDBC data source; demonstrate what happens when too many tables order at the same time



create table kitchen_orders
( table_number varchar2(20)
, menu_item    varchar2(50)
, price        number(5,2)
, appetizer_or_main varchar2(1)
, start_time   timestamp default systimestamp
, end_time     timestamp
, log_entry    varchar2(2000)
);
