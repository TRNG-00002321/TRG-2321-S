# SQL Interview Questions & Answers

## Beginner Level

**Q1: What is SQL and what are the different types of SQL commands?**

> **Answer:** SQL (Structured Query Language) is the standard language for managing and manipulating relational databases.
>
> **Command Categories:**
>
> | Category | Commands | Purpose |
> |----------|----------|---------|
> | DDL (Data Definition) | CREATE, ALTER, DROP, TRUNCATE | Define structure |
> | DML (Data Manipulation) | INSERT, UPDATE, DELETE | Modify data |
> | DQL (Data Query) | SELECT | Retrieve data |
> | DCL (Data Control) | GRANT, REVOKE | Permissions |
> | TCL (Transaction Control) | COMMIT, ROLLBACK, SAVEPOINT | Transactions |
>
> ```sql
> -- DDL
> CREATE TABLE users (id INT, name VARCHAR(100));
> 
> -- DML
> INSERT INTO users VALUES (1, 'Alice');
> 
> -- DQL
> SELECT * FROM users;
> 
> -- DCL
> GRANT SELECT ON users TO analyst;
> 
> -- TCL
> BEGIN TRANSACTION;
> UPDATE accounts SET balance = balance - 100 WHERE id = 1;
> COMMIT;
> ```

---

**Q2: Explain PRIMARY KEY, FOREIGN KEY, and UNIQUE constraints.**

> **Answer:**
>
> ```sql
> CREATE TABLE departments (
>     id INT PRIMARY KEY,          -- Unique, not null, one per table
>     name VARCHAR(100) UNIQUE     -- Unique, can be null, multiple allowed
> );
> 
> CREATE TABLE employees (
>     id INT PRIMARY KEY,
>     email VARCHAR(255) UNIQUE,
>     dept_id INT,
>     FOREIGN KEY (dept_id) REFERENCES departments(id)
>     -- References primary key of another table
> );
> ```
>
> **Differences:**
>
> | Constraint | Unique | Null Allowed | Per Table |
> |------------|--------|--------------|-----------|
> | PRIMARY KEY | Yes | No | One |
> | UNIQUE | Yes | Yes (one null) | Multiple |
> | FOREIGN KEY | No | Yes | Multiple |
>
> **Foreign Key actions:**
> ```sql
> FOREIGN KEY (dept_id) REFERENCES departments(id)
>     ON DELETE CASCADE      -- Delete employees when dept deleted
>     ON UPDATE SET NULL     -- Set null when dept id changes
> ```

---

**Q3: What is the difference between WHERE and HAVING?**

> **Answer:**
> - **WHERE:** Filters rows before grouping
> - **HAVING:** Filters groups after aggregation
>
> ```sql
> -- WHERE filters individual rows
> SELECT * FROM orders
> WHERE amount > 100;
> 
> -- HAVING filters after GROUP BY
> SELECT customer_id, SUM(amount) as total
> FROM orders
> WHERE status = 'completed'    -- Filters rows first
> GROUP BY customer_id
> HAVING SUM(amount) > 1000;    -- Filters groups
> 
> -- Execution order:
> -- FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY
> ```
>
> **Key rule:** HAVING is for aggregate functions (SUM, COUNT, AVG, etc.)

---

**Q4: Explain different types of JOINs.**

> **Answer:**
>
> ```sql
> -- Sample tables
> -- users: (1, Alice), (2, Bob), (3, Charlie)
> -- orders: (101, 1), (102, 1), (103, 4)
> 
> -- INNER JOIN: Only matching rows from both tables
> SELECT u.name, o.order_id
> FROM users u
> INNER JOIN orders o ON u.id = o.user_id;
> -- Alice, 101 | Alice, 102
> 
> -- LEFT JOIN: All from left, matching from right (nulls if no match)
> SELECT u.name, o.order_id
> FROM users u
> LEFT JOIN orders o ON u.id = o.user_id;
> -- Alice, 101 | Alice, 102 | Bob, NULL | Charlie, NULL
> 
> -- RIGHT JOIN: All from right, matching from left
> SELECT u.name, o.order_id
> FROM users u
> RIGHT JOIN orders o ON u.id = o.user_id;
> -- Alice, 101 | Alice, 102 | NULL, 103
> 
> -- FULL OUTER JOIN: All from both tables
> SELECT u.name, o.order_id
> FROM users u
> FULL OUTER JOIN orders o ON u.id = o.user_id;
> -- Combines LEFT and RIGHT results
> 
> -- CROSS JOIN: Cartesian product (all combinations)
> SELECT u.name, p.product_name
> FROM users u
> CROSS JOIN products p;
> ```
>
> **Visual:**
> ```
> INNER:  Only intersection
> LEFT:   Left circle + intersection
> RIGHT:  Right circle + intersection
> FULL:   Both circles
> ```

---

**Q5: What is normalization? Explain 1NF, 2NF, 3NF.**

> **Answer:** Normalization organizes data to reduce redundancy and improve integrity.
>
> **1NF (First Normal Form):**
> - Atomic values (no lists or arrays in cells)
> - Each row unique (primary key)
>
> ```sql
> -- Bad (not 1NF)
> | id | name  | phones              |
> | 1  | Alice | 123-456, 789-012    |
> 
> -- Good (1NF)
> | id | name  | phone    |
> | 1  | Alice | 123-456  |
> | 1  | Alice | 789-012  |
> ```
>
> **2NF (Second Normal Form):**
> - Meet 1NF
> - No partial dependencies (non-key depends on entire primary key)
>
> ```sql
> -- Bad (partial dependency): product_name depends only on product_id
> | order_id | product_id | product_name | quantity |
> 
> -- Good (2NF): Separate tables
> Orders: | order_id | product_id | quantity |
> Products: | product_id | product_name |
> ```
>
> **3NF (Third Normal Form):**
> - Meet 2NF
> - No transitive dependencies (non-key depends only on key, not other non-keys)
>
> ```sql
> -- Bad: city depends on zip_code, not on id
> | id | zip_code | city     |
> 
> -- Good (3NF): Separate lookup table
> Users: | id | zip_code |
> Locations: | zip_code | city |
> ```

---

## Intermediate Level

**Q6: What are aggregate functions and GROUP BY?**

> **Answer:**
>
> ```sql
> -- Common aggregate functions
> SELECT 
>     COUNT(*) as total_orders,
>     COUNT(DISTINCT customer_id) as unique_customers,
>     SUM(amount) as total_revenue,
>     AVG(amount) as avg_order_value,
>     MIN(amount) as smallest_order,
>     MAX(amount) as largest_order
> FROM orders;
> 
> -- GROUP BY: Aggregate per group
> SELECT 
>     category,
>     COUNT(*) as product_count,
>     AVG(price) as avg_price
> FROM products
> GROUP BY category;
> 
> -- Multiple grouping columns
> SELECT 
>     year, month,
>     SUM(revenue) as total
> FROM sales
> GROUP BY year, month
> ORDER BY year, month;
> 
> -- With HAVING
> SELECT 
>     customer_id,
>     COUNT(*) as order_count
> FROM orders
> GROUP BY customer_id
> HAVING COUNT(*) >= 5
> ORDER BY order_count DESC;
> ```
>
> **Rule:** SELECT can only include grouped columns or aggregate functions.

---

**Q7: Explain subqueries and when to use them.**

> **Answer:**
>
> ```sql
> -- Subquery in WHERE
> SELECT * FROM products
> WHERE price > (SELECT AVG(price) FROM products);
> 
> -- Subquery in FROM (derived table)
> SELECT avg_order.*
> FROM (
>     SELECT customer_id, AVG(amount) as avg_amount
>     FROM orders
>     GROUP BY customer_id
> ) as avg_order
> WHERE avg_amount > 100;
> 
> -- Subquery with IN
> SELECT * FROM users
> WHERE id IN (
>     SELECT DISTINCT customer_id FROM orders
>     WHERE order_date > '2024-01-01'
> );
> 
> -- Correlated subquery (references outer query)
> SELECT * FROM employees e
> WHERE salary > (
>     SELECT AVG(salary) 
>     FROM employees 
>     WHERE department_id = e.department_id
> );
> 
> -- EXISTS (checks if subquery returns rows)
> SELECT * FROM customers c
> WHERE EXISTS (
>     SELECT 1 FROM orders o 
>     WHERE o.customer_id = c.id
> );
> ```
>
> **When to use:**
> - Filter by aggregate results
> - Complex comparisons
> - Check existence
> - Derived calculations

---

**Q8: What are indexes and how do they improve performance?**

> **Answer:** Indexes are data structures that speed up data retrieval at the cost of storage and write performance.
>
> ```sql
> -- Create index
> CREATE INDEX idx_email ON users(email);
> CREATE UNIQUE INDEX idx_unique_email ON users(email);
> 
> -- Composite index (multiple columns)
> CREATE INDEX idx_name_city ON users(last_name, first_name, city);
> 
> -- Drop index
> DROP INDEX idx_email ON users;
> ```
>
> **How indexes work:**
> - Without index: Full table scan (O(n))
> - With index: B-tree lookup (O(log n))
>
> **When to create indexes:**
> - Columns in WHERE clauses
> - JOIN columns (foreign keys)
> - ORDER BY columns
> - Columns with high cardinality
>
> **When NOT to index:**
> - Small tables
> - Frequently updated columns
> - Low cardinality (e.g., boolean)
> - Wide columns (long text)
>
> **Index types:**
> - **B-tree:** Default, general purpose
> - **Hash:** Equality comparisons only
> - **Full-text:** Text search
> - **Spatial:** Geographic data

---

**Q9: Explain transactions and ACID properties.**

> **Answer:** A transaction is a sequence of operations treated as a single unit.
>
> ```sql
> BEGIN TRANSACTION;
> 
> UPDATE accounts SET balance = balance - 100 WHERE id = 1;
> UPDATE accounts SET balance = balance + 100 WHERE id = 2;
> 
> -- If all succeed
> COMMIT;
> 
> -- If any fails
> ROLLBACK;
> 
> -- Savepoint for partial rollback
> SAVEPOINT transfer_complete;
> -- ... more operations ...
> ROLLBACK TO transfer_complete;
> ```
>
> **ACID Properties:**
>
> | Property | Meaning | Example |
> |----------|---------|---------|
> | **Atomicity** | All or nothing | Transfer: both debits and credits or neither |
> | **Consistency** | Valid state to valid state | Balance can't go negative if rule exists |
> | **Isolation** | Transactions don't interfere | Concurrent transfers don't corrupt data |
> | **Durability** | Committed = permanent | Power failure won't lose committed data |
>
> **Isolation levels:**
> - READ UNCOMMITTED (dirty reads possible)
> - READ COMMITTED (no dirty reads)
> - REPEATABLE READ (consistent reads)
> - SERIALIZABLE (full isolation, slowest)

---

**Q10: What are views and stored procedures?**

> **Answer:**
>
> **Views:** Virtual tables based on SELECT query.
> ```sql
> -- Create view
> CREATE VIEW active_customers AS
> SELECT id, name, email
> FROM customers
> WHERE status = 'active';
> 
> -- Use like a table
> SELECT * FROM active_customers WHERE name LIKE 'A%';
> 
> -- Benefits:
> -- - Simplify complex queries
> -- - Security (hide columns)
> -- - Abstraction layer
> ```
>
> **Stored Procedures:** Reusable SQL code blocks.
> ```sql
> -- Create procedure
> CREATE PROCEDURE GetCustomerOrders(IN customer_id INT)
> BEGIN
>     SELECT o.order_id, o.order_date, o.amount
>     FROM orders o
>     WHERE o.customer_id = customer_id
>     ORDER BY o.order_date DESC;
> END;
> 
> -- Call procedure
> CALL GetCustomerOrders(123);
> 
> -- Benefits:
> -- - Reusability
> -- - Security (parameterized)
> -- - Performance (pre-compiled)
> -- - Business logic in database
> ```

---

## Advanced Level

**Q11: Explain window functions (OVER, PARTITION BY, ROW_NUMBER).**

> **Answer:** Window functions perform calculations across related rows without collapsing them.
>
> ```sql
> -- ROW_NUMBER: Unique sequential number
> SELECT 
>     name,
>     department,
>     salary,
>     ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) as rank
> FROM employees;
> -- Each department gets its own ranking
> 
> -- RANK vs DENSE_RANK
> -- RANK: 1, 2, 2, 4 (skips after ties)
> -- DENSE_RANK: 1, 2, 2, 3 (no skips)
> 
> -- Running total
> SELECT 
>     order_date,
>     amount,
>     SUM(amount) OVER (ORDER BY order_date) as running_total
> FROM orders;
> 
> -- Moving average
> SELECT 
>     date,
>     value,
>     AVG(value) OVER (
>         ORDER BY date 
>         ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
>     ) as moving_avg_3day
> FROM metrics;
> 
> -- LAG/LEAD: Access previous/next rows
> SELECT 
>     month,
>     revenue,
>     LAG(revenue, 1) OVER (ORDER BY month) as prev_month,
>     revenue - LAG(revenue, 1) OVER (ORDER BY month) as change
> FROM monthly_sales;
> ```
>
> **Common window functions:**
> - ROW_NUMBER(), RANK(), DENSE_RANK()
> - LAG(), LEAD()
> - FIRST_VALUE(), LAST_VALUE()
> - SUM(), AVG(), COUNT() with OVER

---

**Q12: How do you optimize slow SQL queries?**

> **Answer:**
>
> **1. Analyze execution plan:**
> ```sql
> EXPLAIN ANALYZE SELECT * FROM users WHERE email = 'test@test.com';
> ```
>
> **2. Add appropriate indexes:**
> ```sql
> -- Index columns in WHERE, JOIN, ORDER BY
> CREATE INDEX idx_email ON users(email);
> ```
>
> **3. Avoid SELECT *:**
> ```sql
> -- Bad
> SELECT * FROM orders;
> -- Good
> SELECT order_id, amount, status FROM orders;
> ```
>
> **4. Use EXISTS instead of IN for large datasets:**
> ```sql
> -- Often faster
> SELECT * FROM customers c
> WHERE EXISTS (SELECT 1 FROM orders WHERE customer_id = c.id);
> ```
>
> **5. Limit results:**
> ```sql
> SELECT * FROM orders ORDER BY date DESC LIMIT 100;
> ```
>
> **6. Avoid functions on indexed columns:**
> ```sql
> -- Bad (can't use index)
> WHERE YEAR(created_at) = 2024
> -- Good (uses index)
> WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01'
> ```
>
> **7. Optimize JOINs:**
> - Join on indexed columns
> - Start with smallest table
> - Filter early with WHERE
>
> **Common performance killers:**
> - Missing indexes
> - SELECT * on large tables
> - N+1 queries (loop with query)
> - Unoptimized subqueries
> - Implicit type conversions
