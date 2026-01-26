

-- Calculate revenue for each phone by multiplying price with units_sold
SELECT name , price * units_sold as revenue FROM phones; 


SELECT name , price  from phones WHERE units_sold > 5000;

SELECT name, manufacturer FROM phones WHERE manufacturer = 'Apple' OR manufacturer = 'Samsung';

-- calculate with where claise
SELECT name, price * units_sold as total_revenue FROM phones where price * units_sold > 1000000 ;

UPDATE phones SET units_sold =  8543 WHERE name = 'N8';

SELECT * FROM phones;

DELETE FROM phones WHERE manufacturer = 'Samsung';



-- Create table called 'boats'
CREATE TABLE boats (
    -- Note that this environment doesn't support 'SERIAL' keyword
    -- so 'AUTOINCREMENT' is used instead. Postgres always uses 'SERIAL'
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR
);


-- Insert two boats 
INSERT INTO boats (name)
VALUES ('Rogue Wave'), ('Harbor Master');


-- Create table called 'crew_members'
CREATE TABLE crew_members (
    -- Note that this environment doenst support 'SERIAL' keyword
    -- so 'AUTOINCREMENT' is used instead. Postgres always uses 'SERIAL'
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name VARCHAR,
    -- >>>>>>>> TODO #1 HERE!!!
    boat_id INTEGER REFERENCES boats(id)
    
);

-- Insert three crew members
INSERT INTO crew_members (first_name, boat_id)
VALUES ('Alex', 1), ('Lucia', 1), ('Ari', 2);

SELECT * FROM crew_members WHERE boat_id = 1;


SELECT title, name
FROM books
JOIN authors ON books.author_id = authors.id;

SELECT title, name
FROM authors
LEFT JOIN books ON books.author_id = authors.id;

SELECT title, name, rating
FROM books
JOIN authors ON books.author_id = authors.id
JOIN reviews ON books.author_id = reviews.reviewer_id
AND books.id = reviews.book_id

;


-- join and group by
SELECT author_id, COUNT(*) , name
FROM books
JOIN authors ON books.author_id = authors.id
GROUP BY name;




SELECT manufacturer, SUM(price * units_sold) 
FROM phones
GROUP BY manufacturer
HAVING SUM(price * units_sold) > 2000000
;


SELECT paid, COUNT(*) 
FROM orders
GROUP BY paid;


SELECT users.first_name, users.last_name, orders.paid
from users
JOIN orders ON users.id = orders.user_id
WHERE orders.paid = true;

SELECT name
FROM phones
ORDER by price DESC
LIMIT 2
OFFSET 1;