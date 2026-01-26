## The Scenario
* Designing a database for an Instagram-like application (where filters and FOMO reside together). We'll need to handle image sharing, user profiles, comments, likes, tags, and more.

## Observations from the UI (Research or Boredom?)
* User profiles: Everyone wants a bio and a catchy username.
* Followers & Following: The social hierarchy of the digital age.
* A list of photos: The core of the app.
* Liking photos: Because a tap is worth a thousand words.
* Commenting: For when a tap isn't enough.

## Preliminary Table Design
* For now, we'll start with the basics: `users`, `photos`, `comments`, and `likes`.

## Relationships 101
* **One-to-Many**: A user can post a mountain of photos, but each photo (hopefully) belongs to just one user.
* **Many-to-One**: The flip side—many photos can point back to a single proud uploader.
* **One-to-One**: Like a user and their driver's license (one person, one license).
* **Many-to-Many**: Students and courses—a student can enroll in many courses, and a course can have many students. Or football players and teams—players switch teams, and teams have many players.

## PK vs. FK (The Dynamic Duo)
* **Primary Key (PK)**: A unique identifier for each record. Think of it as a fingerprint for every row in your table.
* **Foreign Key (FK)**: A field that refers to a record in another table. It’s the tether that keeps your data connected and consistent.

### Auto-generated PKs in Postgres
* `SERIAL`: The "Easy Button" for auto-incrementing integers.
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL
);

INSERT INTO users (username) VALUES ('john_doe');
INSERT INTO users (username) VALUES ('jane_smith');
INSERT INTO users (username) VALUES ('alice_jones');
INSERT INTO users (username) VALUES ('bob_brown');
```

### Linking Photos to Users
* Defining a Foreign Key reference: `REFERENCES parent_table(parent_column)`.
```sql
CREATE TABLE photos (
    id SERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    user_id INTEGER REFERENCES users(id)
);

INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo1.jpg', 1);
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo2.jpg', 2);
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo3.jpg', 1);
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo4.jpg', 3);
```

### Querying Associated Data
* Find all photos uploaded by user #4:
```sql
SELECT * FROM photos WHERE user_id = 4;
```
* Multi-table magic: List all photos alongside the username of the person who uploaded them:
```sql
SELECT url, username FROM photos
JOIN users ON photos.user_id = users.id;
```

## Foreign Key Constraints: The Safety Net
* **Insertion Constraints**: Try inserting a photo for a user who doesn't exist (e.g., user #999999).
```sql
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo5.jpg', 132432324320);
```
* Result: An error! Postgres won't let you create orphans.

* **Null References**: You can, however, insert a photo that isn't linked to any user:
```sql
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo6.jpg', NULL);
```

## Foreign Key Constraints: Deletion Dilemmas
* Try deleting a user who still has photos associated with them:
```sql
DELETE FROM users WHERE id = 1;
```
* Result: Error! Postgres is protecting your data integrity.

### Handling Deletions Gracefully:
* `ON DELETE CASCADE`: If the user goes, their photos go with them. No trace left behind.
* `ON DELETE RESTRICT`: The "Stop Right There" approach—throws an error if there's dependent data.
* `ON DELETE SET NULL`: Sets the `user_id` to `NULL` but keeps the photo record.
* `ON DELETE NO ACTION`: The default "Let's see what happens" (usually results in an error if violated).
* `ON DELETE SET DEFAULT`: Sets the `user_id` to its default value.

* Place the clause directly in the FK definition:
```sql
CREATE TABLE photos (
    id SERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE
);
```

## Adding Complexity: The Comments Table
* A comment needs a photo to live on and a user to blame for it.
```sql
CREATE TABLE comments (
  id SERIAL PRIMARY KEY,
  contents VARCHAR(240),
  user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
  photo_id INTEGER REFERENCES photos(id) ON DELETE CASCADE
);
```

## Joins and Aggregation: The Power Tools
* **JOIN**: Merges rows from two or more tables based on a shared column. 
* **Aggregation**: Crushes a set of values into a single result (count, sum, average).

* Show every comment along with the name of the person who wrote it:
```sql
SELECT comments.contents, users.username FROM comments
JOIN users ON comments.user_id = users.id;
```
* Pro Tip: Behind the scenes, SQL creates a temporary "mega-table" by matching up rows based on your condition.

* Show every comment and the photo URL it belongs to:
```sql
SELECT comments.contents, photos.url FROM comments
JOIN photos ON comments.photo_id = photos.id;
```

## A Note on Ambiguity
* If both tables have a column named `id`, you **must** specify which one you want.
```sql
SELECT comments.id, users.id FROM comments
JOIN users ON comments.user_id = users.id;
```

## The Mystery of Missing Data in Joins
* If we insert a photo without a `user_id`:
```sql
INSERT INTO photos (url, user_id) VALUES ('http://example.com/photo7.jpg', NULL);
```
* A standard `JOIN` (Inner Join) will ignore this photo because it has no match in the `users` table.

### The Four Flavors of Joins:
* **INNER JOIN**: Only the perfect matches make the cut.
* **LEFT OUTER JOIN**: Keeps everything from the left table, even if there's no match on the right (NULLs fill the gaps).
* **RIGHT OUTER JOIN**: Keeps everything from the right table.
* **FULL JOIN**: Everyone is invited! Returns all rows from both sides, matching where possible.

## WHERE Clauses with Joins
* Find comments where the author of the comment is also the person who posted the photo:
```sql
SELECT photos.url, comments.contents FROM comments
JOIN photos ON comments.photo_id = photos.id
WHERE comments.user_id = photos.user_id;
```

### The Legendary Three-Way Join
* List the photo URL, comment content, and the username of the commenter—for cases where the commenter owns the photo:
```sql
SELECT photos.url, comments.contents, users.username FROM comments
JOIN photos ON comments.photo_id = photos.id
JOIN users ON comments.user_id = users.id
WHERE comments.user_id = photos.user_id;
```

## GROUP BY and Aggregation
* `GROUP BY`: Squashes many rows into fewer rows based on unique values in a column.
* **Aggregation Functions**: `COUNT()`, `SUM()`, `AVG()`, `MIN()`, `MAX()`.

* Example: See unique user IDs from the comments table:
```sql
SELECT user_id 
FROM comments
GROUP BY user_id;
```
* **Cardina Rule**: You can only `SELECT` columns that are in your `GROUP BY` clause or used inside an aggregate function.

* Find the number of comments each user has made:
```sql
SELECT user_id, COUNT(*) AS comment_count
FROM comments
GROUP BY user_id;
```
* **Gotcha with COUNT**: `COUNT(column_name)` ignores NULLs. Use `COUNT(*)` if you want to count every single row, NULLs and all.

## Order of Operations
* `FROM` => `JOIN` => `WHERE` => `GROUP BY` => `HAVING` => `SELECT` => `ORDER BY`

## Filtering Groups with HAVING
* `HAVING` is like `WHERE`, but it works on your grouped results.
* Example: Find photos with more than 2 comments, but only for photos with an ID less than 3:
```sql
SELECT photo_id, COUNT(*) AS comment_count
FROM comments
WHERE photo_id < 3
GROUP BY photo_id
HAVING COUNT(*) > 2;
```

## Sorting the Chaos: ORDER BY
* `ORDER BY` sorts your results. The default is `ASC` (ascending).
```sql
SELECT user_id, COUNT(*) AS comment_count
FROM comments
WHERE comments.photo_id IN (1,2)
GROUP BY comments.user_id
HAVING COUNT(*) > 2
ORDER BY comment_count DESC, user_id ASC;
```

## OFFSET and LIMIT: Perfect for Pagination
* `LIMIT`: "Only give me X rows."
* `OFFSET`: "Skip the first Y rows."
```sql
SELECT * FROM users
OFFSET 40 LIMIT 10;
```