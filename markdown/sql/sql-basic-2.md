## scenarios
* deasign a db for a instagram like application (inage sharing, user profiles, comments, likes, tagss etc)

## first few observation from UI (inspired by instagram)
* user profile 
* user have followers and can follow other users
* list of photos
* user can like a photo
* user can comment on a photo

## preliminary table design
* users, photos, comments, likes for now.


## relationships
* user and photos: one to many, a user can have many photos
* photos and user: many to one, a photo belongs to one user
* one-to-one relationship:  each user has one driver's license
* many-to-many relationship:  students and courses, a student can enroll in many courses, and a course can have many students enrolled. or players and football teams, a player can play for multiple teams over their career, and a team has many players.

## PK and FK
* primary key (PK): unique identifier for each record in a table, the goal is to identify each row uniquely
* foreign key (FK): a field (or collection of fields) in one table that refers, identifies a record (usually in another table). the goal is to establish and enforce a link between the data in the two tables.
### auto generated PK (in postgres)
* serial: auto incrementing integer
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
);

INSERT INTO users (username) VALUES ('john_doe');
INSERT INTO users (username) VALUES ('jane_smith');
INSERT INTO users (username) VALUES ('alice_jones');
INSERT INTO users (username) VALUES ('bob_brown');
```

* photo table with FK to users table
* syntax for FK reference
* REFERENCES parent_table(parent_column)
```sql
CREATE TABLE photos (
    id SERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    user_id INTEGER REFERENCES users(id)
);

INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo1.jpg', 1);
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo2.jpg',2);
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo3.jpg',1);
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo4.jpg',3);
```

### run query on associated data
* select all photos with user_id = 4
```sql
SELECT * FROM photos WHERE user_id = 4;
```
* list all photos with details of the user who uploaded each photo
```sql
SELECT url, username FROM photos
JOIN users ON photos.user_id = users.id;
```


## foreign key constraints around insertion
* we insert a photo that refers to a user that doesnt exist
```sql
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo5.jpg', 132432324320);
```
* => we will get an error.

* we insert a photo that isn t linked to any user
```sql
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo6.jpg', NULL);
```

## foreign key constraints around deletion
* we try to delete a user that has photos associated with them
```sql
DELETE FROM users WHERE id = 1;
```
* => we will get an error.
* avoid this by some ways:
  * on delete cascade: this will automatically delete all associated photos when a user is deleted
  * on delete restrict: this will prevent deletion of a user if they have associated photos aka throw an error
  * on delete set null: this will set the user_id in photos to NULL when the associated user is deleted
  * on delete no action: this is the default behavior, which means no action is taken, and an error is thrown if there are associated records.
  * on delete set default: this will set the user_id in photos to its default value when the user is deleted.
* remember to place on delete clause in the FK definition
```sql
CREATE TABLE photos (
    id SERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE
);
```
## adding some cpre complexity
* now we have data consistency with FK constraints
* add one more table for comments
* comments: id , photo_id (FK to photos), user_id (FK to users), content
```sql
CREATE TABLE comments (
  id SERIAL PRIMARY KEY,
  contents VARCHAR(240),
  user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
  photo_id INTEGER REFERENCES photos(id) ON DELETE CASCADE
);
```

## queries with join and aggregation
* join: produce values by merging together columns from two or more tables based on a related column between them, used most time that u are asked to find data that involves multiple tables.
* aggregation: perform a calculation on a set of values to return a single value, often used with grouping data to summarize information.
* fpr each comment , show the contents and user name of the commenter
```sql
SELECT comments.contents, users.username FROM comments
JOIN users ON comments.user_id = users.id;
```
* what join actually does? 
* bbehind the scenes, it creates a temporary table that combines rows from both tables based on the join condition.

* for each comment, show the content of the comment, the url of the photo.
```sql
SELECT comments.contents, photos.url FROM comments
JOIN photos ON comments.photo_id = photos.id;
```

## notes on join 
* if column names are same in both tables, we need to specify the table name to avoid ambiguity
```sql
SELECT comments.id, users.id FROM comments
JOIN users ON comments.user_id = users.id;
-- we need to specify comments.id and users.id to avoid ambiguity
```

## missing data in join
insert into photos without user_id
```sql
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo7.jpg', NULL);
```
* now if we run the join query again
```sql
SELECT url, username FROM photos
JOIN users ON photos.user_id = users.id;
```
* we will not see the photo with NULL user_id in the result
* also user that has no photos will not appear in the result
* why? because join only includes rows that have matching values in both tables


### four kinds of joins
* inner join: returns only the rows with matching values in both tables
* left outer join: returns all rows from the left table and matching rows from the right table, if no match, NULLs for right table columns
* right outer join: returns all rows from the right table and matching rows from the left table, if no match, NULLs for left table columns
* full join: returns all rows when there is a match in either left or right table, if no match, NULLs for missing side columns

## where with join
* users can commeny on photos they they posted.
* list the url of photho and content of comments where this comment is made by the owner of the photo
```sql
select photos.url, comments.contents from comments
join photos on comments.photo_id = photos.id
where comments.user_id = photos.user_id;
```

### three way join
* list the url of photo, content of comment and username of commenter where  where this comment is made by the owner of the photo aka comments.user_id = photos.user_id 
```sql
select photos.url, comments.contents, users.username from comments
join photos on comments.photo_id = photos.id
join users on comments.user_id = users.id
and users.id = photos.user_id
where comments.user_id = photos.user_id;
```

### group by and aggregation
* group by : reduce manby rows down to fewer rows based on unique values in one or more columns, done by using GROUP BY clause
* aggregation functions: COUNT(), SUM(), AVG(), MIN(), MAX(), => reduces many values down to a single value
* i.e
  ```sql
    SELECT user_id 
    FROM comments
    GROUP BY user_id;
    ```
* explain: this will group all comments by user_id, so each unique user_id will appear once in the result set.
* you can only select columns that are in the GROUP BY clause or use aggregation functions on other columns.
```sql
SeLECT MIN(id)
FROM comments
```
* combine grouping and aggregation
```sql
select user_id, MAX(id) 
from comments
group by user_id;
```

* behing the scene with group by, sql create temporary table that groups rows based on unique values in the specified column(s) in GROUP BY clause. the aggregation functions then operate on these groups to produce a single value for each group.
* i.e count number of comments per user
```sql
select user_id, COUNT(id) as comment_count
from comments
group by user_id;
```

* gotcha with count: null values are ignored by count
* if u want to count nulls, use COUNT(*) instead of COUNT(column_name)
```sql
select user_id, COUNT(*) as comment_count
from comments
group by user_id;
```

* find nukber of comment for each photo
  
```sql

SELECT photo_id, COUNT(*)
FROM comments
GROUP BY photo_id;
```


## order 
* from => join => where => group by => having = > select => order by   
## having
* filter groups based on aggregate conditions
* always come after group by
* i.e find the number of comments  for each photo where photo_id is less than 3 and number of comments is greater than 2
```sql
select photo_id, COUNT(*) as comment_count
from comments
where photo_id < 3
GROUP BY photo_id
HAVING COUNT(*) > 2;
```

* list all users who has commented on the first 2 photos and have made more than 2 comments on those photos
```sql
select user_id, COUNT(*) as comment_count
from comments
WHERE comments.photo_id IN (1,2)
GROUP BY comments.user_id
HAVING COUNT(*) > 2;
```

* find users here user has commented on the first 50 photos and have made more than 20 comments on those photos
```sql
select user_id, COUNT(*) as comment_count
from comments
where comments.photo_id < 50
GROUP BY comments.user_id
HAVING COUNT(*) > 20;
```

### sorting records:
* order by clause is used to sort the result set based on one or more columns
* default sorting order is ascending (ASC)
* apply secondary sorting by adding more columns to the order by clause
```sql
SELECT user_id, COUNT(*) as comment_count
FROM comments
WHERE comments.photo_id IN (1,2)
GROUP BY comments.user_id
HAVING COUNT(*) > 2
ORDER BY comment_count DESC, user_id ASC;
```

## offset and limit
* limit : restrict the number of rows returned in the result set
* offset : skip a specified number of rows before starting to return rows in the result set
```sql
SELECT * FROM users
OFFSET 40 LIMIT 10;
```