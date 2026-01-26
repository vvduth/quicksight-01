## Working with a New Dataset
* We’re shifting gears from social media to E-commerce. Here’s the structure of our new tables:
```sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  first_name VARCHAR,
  last_name VARCHAR
);

CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  name VARCHAR,
  department VARCHAR,
  price INTEGER,
  weight INTEGER
);

CREATE TABLE orders (
  id SERIAL PRIMARY KEY,
  user_id INTEGER REFERENCES users(id),
  product_id INTEGER REFERENCES products(id),
  paid BOOLEAN
);
```

## Handling Sets with UNION
* `UNION`: Combines the results of two queries into one list. It's like a data-driven high-five between two `SELECT` statements.
* Example: Find the 4 most expensive products AND the 4 products with the highest price-to-weight ratio:
```sql
(SELECT * FROM products
 ORDER BY price DESC
 LIMIT 4)
UNION
(SELECT * FROM products
 ORDER BY (price::FLOAT / weight) DESC
 LIMIT 4);
```
* **Pro Tip**: Use `UNION ALL` if you want to keep duplicates. Standard `UNION` is picky and removes them.
* **The Golden Rule**: Both queries must have the same number of columns and matching data types.

## Finding Common Ground with INTERSECT
* `INTERSECT`: Only returns rows that appear in BOTH results. It's the Venn diagram overlap of your dreams.
```sql
(SELECT * FROM products
 ORDER BY price DESC
 LIMIT 4)
INTERSECT 
(SELECT * FROM products
 ORDER BY (price::FLOAT / weight) DESC
 LIMIT 4);
```
* Use `INTERSECT ALL` if you’re a fan of duplicates.

## The Process of Elimination: EXCEPT
* `EXCEPT`: Returns rows from the first query that ARE NOT in the second. It’s the "I don't want these items" operator.
```sql
(SELECT * FROM products
 ORDER BY price DESC
 LIMIT 4)
EXCEPT 
(SELECT * FROM products
 ORDER BY (price::FLOAT / weight) DESC
 LIMIT 4);
```

## What is a Subquery?
* A query nested inside another query. Think of it as "Inception" for databases.
* Subqueries can live in `SELECT`, `FROM`, `WHERE`, and more.
* **Goal**: List the name and price of all products that are more expensive than the most expensive item in the 'Toys' department.

### The Two-Step Manual Process (The Old Way):
1. Find the highest price in 'Toys':
```sql
SELECT MAX(price) FROM products WHERE department = 'Toys';
```
2. Use that number in your next query:
```sql
SELECT name, price
FROM products
WHERE price > [insert result from step 1];
```

### The Subquery Way (The Smart Way):
* Combine them into one powerful statement:
```sql
SELECT name, price
FROM products
WHERE price > (
    SELECT MAX(price) 
    FROM products 
    WHERE department = 'Toys'
);
```