<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Load Data into DynamoDB

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-databases-dynamodb)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

## Load Data into a DynamoDB Table

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-dynamodb_b481c730" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Introducing Today's Project!

### What is Amazon DynamoDB?

Amazon DynamoDB is a fully managed NoSQL database service that stores data in key-value or document format. It is useful because it can handle massive amounts of data with very low latency, automatically scales up or down based on demand, and removes the need for managing servers or infrastructure. DynamoDB is ideal for applications that require fast, flexible, and highly available data storage, like real-time web apps, gaming, IoT, and mobile apps.

### How I used Amazon DynamoDB in this project

In today's project, I used Amazon DynamoDB to create multiple tables, load data into them using AWS CloudShell, and then view, query, and update the data. This allowed me to practice storing structured and semi-structured data, understanding partition and sort keys, and exploring how DynamoDB manages data efficiently for fast access without needing to manage servers.

### One thing I didn't expect in this project was...

One thing I didn’t expect in this project was how easy it is to load large amounts of data quickly using JSON files and the batch-write CLI command, and how DynamoDB automatically organizes data with partition and sort keys for fast queries.

### This project took me...

This project took me 60 minutes.

---

## Create a DynamoDB table

DynamoDB tables organize data using items and attributes. Each table contains multiple items (like rows in a relational database), and each item has attributes (like columns) that store the actual data. Every table must have a primary key (either a simple partition key or a composite of partition key and sort key) to uniquely identify each item, which also determines how DynamoDB distributes and retrieves data efficiently.

An attribute in DynamoDB is a single piece of data or a column in a table item. Each item (similar to a row in relational databases) can have one or more attributes, which store values like strings, numbers, or binary data. Attributes define the data structure for each item in your table.  In this case, our item is Nikko and the attribute is the number of projects Nikko completed.

Each item in DynamoDB can have multiple attributes. But, unlike relational databases where each row in a table must have the same columns, DynamoDB items can have their own unique set of attributes.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-dynamodb_a3cefee0" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Read and Write Capacity

Read capacity units (RCUs) and write capacity units (WCUs) are measures of how much throughput your DynamoDB table can handle. RCUs define how many items your table can read per second, while WCUs define how many items it can write per second. They help you plan for performance and avoid throttling when many requests are made to the database at the same time.

Amazon DynamoDB's Free Tier gives you 25 GB of data storage, 25 write capacity units (WCUs), and 25 read capacity units (RCUs) per month, which is enough to handle roughly 200 million requests monthly at no cost. I turned off auto scaling because, for this project, I want to keep the throughput predictable and stay within the Free Tier limits, avoiding unexpected changes in capacity that could cause charges.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-dynamodb_ef47dd8f" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Using CLI and CloudShell

AWS CloudShell is shell in your AWS Management Console, which means it's a space for you to run code! The awesome thing about AWS CloudShell is that it already has AWS CLI pre-installed.

AWS CLI (Command Line Interface) is a software that lets you create, delete and update AWS resources with commands instead of clicking through your console.

You usually have to install AWS CLI into your computer to use it, but in our case, CloudShell already has CLI installed for us (thank you CloudShell 🙏).

I ran a CLI command in AWS CloudShell that created four DynamoDB tables:

ContentCatalog Table – has a numeric attribute called Id.

Forum Table – has a partition key called Name.

Post Table – has a partition key ForumName and a sort key Subject. Sort keys let you organize and query related items efficiently.

Comment Table – has a partition key Id and a sort key CommentDateTime, which helps store multiple comments in order by time.

These tables give me a basic structure to organize and query different types of data for my project.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-dynamodb_81e0258b" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Loading Data with CLI

I ran a series of CLI commands in AWS CloudShell to download, extract, and load data into DynamoDB:

# Download the sample data
curl -O https://storage.googleapis.com/nextwork_course_resources/courses/aws/AWS%20Project%20People%20projects/Project%3A%20Query%20Data%20with%20DynamoDB/nextworksampledata.zip

# Unzip the downloaded file
unzip nextworksampledata.zip

# Go into the extracted folder
cd nextworksampledata


Inside nextworksampledata, there are 4 JSON files formatted for DynamoDB:

"Forum" specifies the table the data belongs to.

"PutRequest" tells DynamoDB to add a new item.

Each PutRequest contains the attributes of the new item.

Finally, I ran this command to insert multiple items at once:

aws dynamodb batch-write-item --request-items file://filename.json


This efficiently loads data into the DynamoDB tables.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-dynamodb_791c600b" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Observing Item Attributes

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-dynamodb_b481c731" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

I checked a ContentCatalog item, which had the following attributes:
Id – Unique numeric identifier for the resource.
Title – Name of the project or video.
URL – Link to access the resource online.
Price – Cost
Difficulty – Skill level required for projects (e.g., "Easy peasy").
Published – Whether the resource is published or available (true/false).
ProjectCategory – Categorization like Storage, Analytics, AI/ML for projects.
ContentType – Type of content: "Project", "Video", "Shorts", or "Live Project Demo".

I checked another ContentCatalog item, which had a different set of attributes Id, ContentTyoe, Price, Services, Title, URL and Video Type. It does not have author or Student Complete.

---

## Benefits of DynamoDB

A benefit of DynamoDB over relational databases is flexibility, because DynamoDB is schema-less, which means each item in a table doesn’t need to have the same attributes. You can easily add new fields or remove them without changing the whole table structure. This makes it ideal for rapidly changing data, unstructured data, or applications where each record may have different types of information. Unlike relational databases that require predefined schemas, DynamoDB adapts to the needs of modern applications without downtime for schema changes.

Another benefit over relational databases is speed, because DynamoDB is fully managed, key-value and document-based, which allows it to quickly retrieve or write data using primary keys without needing complex joins or queries. Its automatic scaling, in-memory caching (with DynamoDB Accelerator), and SSD storage also help reduce latency and handle millions of requests per second. This makes it much faster than traditional relational databases for high-traffic applications where performance and low-latency access are critical.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-dynamodb_b481c730" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

---
