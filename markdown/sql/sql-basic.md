## The Challenges of PostgreSQL
* Writing efficient queries to retrieve data (because nobody likes waiting for a slow database).
* Designing a database schema that actually makes sense (the ultimate data puzzle).
* Deciding when to use fancy features like indexing, partitioning, and replication without breaking everything.
* Managing and maintaining the database in production—backups, updates, and tuning the performance engine while it's still running.

## The Design Process
* What kind of things are we storing? (Is it users, or just cat pictures?)
* What properties do those things have?
* What type of data does each of those properties contain? (Strings, numbers, or existential dread?)

## Create Table
* Create a table called `cities` with specific columns:
```sql
CREATE TABLE cities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    population INTEGER,
    country VARCHAR(100),
    area INTEGER
);
```

## Insert Data
* Shoving some data into the `cities` table:
* We'll start with three world-famous cities:
```sql
INSERT INTO cities (name, population, country, area) VALUES
('New York', 8419600, 'USA', 783.8),
('Tokyo', 13929286, 'Japan', 2191),
('London', 8982000, 'UK', 1572);
```

## Retrieve Data with SELECT
* Grab all the columns from the `cities` table:
```sql
SELECT * FROM cities;
```

## Calculated Columns
* Calculate the population density (population divided by area) for each city:
```sql
SELECT name, (population::FLOAT / area) AS population_density FROM cities;
```

## String Operators and Functions
* Let's play with names—uppercase version included!
```sql
SELECT name, UPPER(name) AS uppercase_name FROM cities;
```
* A few common string functions to keep in your back pocket:
  * `||` : The magic glue (concatenation operator) for strings.
  * `LOWER(column_name)`: Whispers everything in lowercase.
  * `UPPER(column_name)`: SHOUTS EVERYTHING IN UPPERCASE.
  * `LENGTH(column_name)`: Counts the characters so you don't have to.
  * `SUBSTRING(column_name FROM start_position FOR length)`: Snips out a piece of the string.
  * `TRIM(column_name)`: Cleans up those annoying leading and trailing spaces.
  * `CONCAT(string1, string2, ...)`: Another way to stick strings together.

## Filtering with WHERE
* Find the big players—cities with more than 5 million people:
```sql
SELECT * FROM cities WHERE population > 5000000;
```
* The unofficial order of operations: `FROM` => `WHERE` => `SELECT` (we'll invite `GROUP BY` and `HAVING` to the party later).

## Comparison Operators
* `=` : Identical twins.
* `<>` or `!=` : Not even close.
* `>` : Bigger is better.
* `<` : Smaller is... smaller.
* `>=` : Greater than or equal to.
* `<=` : Less than or equal to.
* `BETWEEN value1 AND value2` : Staying inside the lines (inclusive).
* `IN (value1, value2, ...)` : "Are you on the guest list?"
* `NOT IN (value1, value2, ...)` : "You're definitely not on the list."

## Compound WHERE Clauses
```sql
SELECT name, area FROM cities WHERE area BETWEEN 500 AND 2000 AND population > 3000000;
```

## Calculated Columns in WHERE
* Filtering by population density (threshold: 3000):
```sql
SELECT * FROM cities WHERE (population::FLOAT / area) > 3000;
```

## Update Data
* Tokyo is growing! Let's update its population to 14,000,000:
```sql
UPDATE cities SET population = 14000000 WHERE name = 'Tokyo';
```

## Delete Data
* Goodbye, London! Removing it from the `cities` table:
```sql
DELETE FROM cities WHERE name = 'London';
```