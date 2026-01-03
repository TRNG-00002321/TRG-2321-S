# SQL Commands Cheat Sheet

## Data Definition Language (DDL)

```sql
-- Create table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    age INT CHECK (age >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Alter table
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
ALTER TABLE users DROP COLUMN phone;
ALTER TABLE users MODIFY COLUMN name VARCHAR(200);

-- Drop table
DROP TABLE users;
DROP TABLE IF EXISTS users;

-- Truncate (delete all data, keep structure)
TRUNCATE TABLE users;
```

## Data Manipulation Language (DML)

```sql
-- Insert
INSERT INTO users (name, email, age) 
VALUES ('Alice', 'alice@email.com', 25);

INSERT INTO users (name, email, age) VALUES
    ('Bob', 'bob@email.com', 30),
    ('Charlie', 'charlie@email.com', 35);

-- Update
UPDATE users SET age = 26 WHERE name = 'Alice';
UPDATE users SET age = age + 1;  -- All rows

-- Delete
DELETE FROM users WHERE id = 1;
DELETE FROM users WHERE age < 18;
```

## Data Query Language (DQL)

```sql
-- Basic SELECT
SELECT * FROM users;
SELECT name, email FROM users;
SELECT DISTINCT city FROM users;

-- WHERE clause
SELECT * FROM users WHERE age > 25;
SELECT * FROM users WHERE name = 'Alice';
SELECT * FROM users WHERE age BETWEEN 20 AND 30;
SELECT * FROM users WHERE name IN ('Alice', 'Bob');
SELECT * FROM users WHERE email LIKE '%@gmail.com';
SELECT * FROM users WHERE phone IS NULL;
SELECT * FROM users WHERE age > 20 AND city = 'NYC';

-- Sorting
SELECT * FROM users ORDER BY name ASC;
SELECT * FROM users ORDER BY age DESC;
SELECT * FROM users ORDER BY city, name;

-- Limiting
SELECT * FROM users LIMIT 10;
SELECT * FROM users LIMIT 10 OFFSET 20;

-- Aliases
SELECT name AS user_name, email AS contact FROM users;
SELECT u.name, o.total FROM users u, orders o;
```

## Aggregate Functions

```sql
SELECT COUNT(*) FROM users;
SELECT COUNT(DISTINCT city) FROM users;
SELECT SUM(amount) FROM orders;
SELECT AVG(age) FROM users;
SELECT MIN(price), MAX(price) FROM products;

-- GROUP BY
SELECT city, COUNT(*) as user_count 
FROM users 
GROUP BY city;

-- HAVING (filter groups)
SELECT city, COUNT(*) as count 
FROM users 
GROUP BY city 
HAVING count > 10;
```

## JOINs

```sql
-- INNER JOIN (matching rows only)
SELECT users.name, orders.total
FROM users
INNER JOIN orders ON users.id = orders.user_id;

-- LEFT JOIN (all from left, matching from right)
SELECT users.name, orders.total
FROM users
LEFT JOIN orders ON users.id = orders.user_id;

-- RIGHT JOIN (all from right, matching from left)
SELECT users.name, orders.total
FROM users
RIGHT JOIN orders ON users.id = orders.user_id;

-- FULL OUTER JOIN (all from both)
SELECT users.name, orders.total
FROM users
FULL OUTER JOIN orders ON users.id = orders.user_id;

-- Multiple joins
SELECT u.name, o.total, p.name as product
FROM users u
JOIN orders o ON u.id = o.user_id
JOIN products p ON o.product_id = p.id;
```

## Subqueries

```sql
-- Subquery in WHERE
SELECT * FROM users 
WHERE id IN (SELECT user_id FROM orders WHERE total > 100);

-- Subquery in FROM
SELECT avg_total FROM (
    SELECT AVG(total) as avg_total FROM orders
) as subquery;

-- Correlated subquery
SELECT * FROM users u
WHERE EXISTS (
    SELECT 1 FROM orders o WHERE o.user_id = u.id
);
```

## Set Operations

```sql
-- UNION (unique rows from both)
SELECT name FROM customers
UNION
SELECT name FROM employees;

-- UNION ALL (all rows including duplicates)
SELECT name FROM customers
UNION ALL
SELECT name FROM employees;

-- INTERSECT (rows in both)
SELECT name FROM customers
INTERSECT
SELECT name FROM employees;

-- EXCEPT (rows in first but not second)
SELECT name FROM customers
EXCEPT
SELECT name FROM employees;
```

## Indexes

```sql
-- Create index
CREATE INDEX idx_email ON users(email);
CREATE UNIQUE INDEX idx_unique_email ON users(email);
CREATE INDEX idx_name_city ON users(name, city);

-- Drop index
DROP INDEX idx_email ON users;
```

## Views

```sql
-- Create view
CREATE VIEW active_users AS
SELECT * FROM users WHERE status = 'active';

-- Use view
SELECT * FROM active_users;

-- Drop view
DROP VIEW active_users;
```

## Transaction Control (TCL)

```sql
-- Start transaction
START TRANSACTION;
-- or
BEGIN;

-- Commit changes
COMMIT;

-- Rollback changes
ROLLBACK;

-- Savepoint
SAVEPOINT my_savepoint;
ROLLBACK TO my_savepoint;
```

## Common Patterns

```sql
-- Pagination
SELECT * FROM users 
ORDER BY id 
LIMIT 20 OFFSET 40;  -- Page 3, 20 per page

-- Search
SELECT * FROM products 
WHERE name LIKE '%phone%' 
   OR description LIKE '%phone%';

-- Date filtering
SELECT * FROM orders 
WHERE created_at >= '2024-01-01' 
  AND created_at < '2024-02-01';

-- Null handling
SELECT COALESCE(phone, 'N/A') FROM users;
SELECT * FROM users WHERE phone IS NOT NULL;
```
