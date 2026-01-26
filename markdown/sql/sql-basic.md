## challenges of postgres
* writing efficient queries to retrieve data
* designing the schema or structure of the database
* understanding when to use advance features like indexing, partitioning, and replication
* managing and maintaining the database over time in a production environment (backups, updates, performance tuning, scaling)

## process
* what kind of thing are we storing?
* what properties do those things have?
* what type of data does each of those properties contain?

## Create table
*cretae a table called cities with columns
```sql
CREATE TABLE cities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    population INTEGER,
    country VARCHAR(100),
    area INTEGER
);
```

## Insert data
* insert data into cities table 
* insert three cities
```sql
INSERT INTO cities (name, population, country, area) VALUES
('New York', 8419600, 'USA', 783.8),
('Tokyo', 13929286, 'Japan', 2191),
('London', 8982000, 'UK', 1572);
```

## Retrieve data with select
* select all columns from cities table
```sql
SELECT * FROM cities;
```
## calculated columns
* select name and population density (population/area) from cities table
```sql
SELECT name, (population::FLOAT / area) AS population_density FROM cities;
```

## string opeator and functions
* select name and uppercase name from cities table
```sql
SELECT name, UPPER(name) AS uppercase_name FROM cities;
```
* couple common string functions
  * || : Concatenation operator to combine strings
  * LOWER(column_name): Converts all characters in the specified column to lowercase.
  * UPPER(column_name): Converts all characters in the specified column to uppercase.
  * LENGTH(column_name): Returns the length of the string in the specified column.
  * SUBSTRING(column_name FROM start_position FOR length): Extracts a substring from the specified column starting at start_position for the given length.
  * TRIM(column_name): Removes leading and trailing spaces from the string in the specified column.
  * CONCAT(string1, string2, ...): Concatenates multiple strings together.
## filtering with where
* select cities with population greater than 5 million
```sql
SELECT * FROM cities WHERE population > 5000000;
```

* oreder: from => where => select (for now, later will add group by, having)

## comparison operators
* = : Equal to
* <> or != : Not equal to
* > : Greater than
* < : Less than
* >= : Greater than or equal to
* <= : Less than or equal to
* BETWEEN value1 AND value2 : Within a range (inclusive)
* IN (value1, value2, ...) : Matches any value in a list
* NOT IN (value1, value2, ...) : Does not match any value in a list

## compound where clauses
```sql
SELECT name, area FROM cities WHERE area BETWEEN 500 AND 2000 AND population > 3000000;

```


## calculated columns in where
* select cities with population density greater than 3000
```sql
SELECT * FROM cities WHERE (population::FLOAT / area) > 3000;
```

## update data
* update population of Tokyo to 14000000
```sql
UPDATE cities SET population = 14000000 WHERE name = 'Tokyo';
```

## delete data
* delete city London from cities table
```sql
DELETE FROM cities WHERE name = 'London';
```