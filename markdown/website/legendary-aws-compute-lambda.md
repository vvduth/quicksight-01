<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Fetch Data with AWS Lambda

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-compute-lambda)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

## Fetch Data with AWS Lambda

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-lambda_p9thryj2" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Introducing Today's Project!

In this project, I will demonstrate how to:

- Create a database table to store user data.
- Build a serverless function to retrieve user data.
- Write tests to validate that the function can fetch data from DynamoDB.
- Secure the serverless function with proper permissions.
- Secure the database with an inline policy.

I'm doing this project to learn how to design, build, and secure a backend system using serverless technologies and best practices for cloud security and testing.

### Tools and concepts
  
The key services and concepts I learnt in this project were:

- **AWS Lambda:** A serverless compute service that lets you run code without managing servers. I used Lambda to build backend logic that processes requests and interacts with other AWS services.
- **Amazon DynamoDB:** A fully managed NoSQL database service. I used DynamoDB to store and retrieve user data efficiently and securely.
- **IAM Roles and Policies:** Identity and Access Management roles and permission policies control what resources my Lambda function can access. I learnt to grant least-privilege permissions, use managed and inline policies, and troubleshoot access errors.
- **AWS SDK:** The software development kit that enables my Lambda function to securely connect to AWS services like DynamoDB and perform operations such as `GetItem`.
- **Testing and Debugging:** I learnt to create test events for Lambda, interpret error messages, and validate my function’s behavior, which is essential for reliable dev

### Project reflection

This project took me approximately a few hours to complete. The most challenging part was troubleshooting permission errors and making sure my Lambda function could securely access just the right DynamoDB table—figuring out IAM roles and writing a precise inline policy took some research and testing. It was most rewarding to see everything working together: my Lambda function successfully retrieving user data from DynamoDB, knowing I’d set up secure, least-privilege access, and understanding how serverless components can power scalable web apps.

I chose to do this project today because I wanted to learn how to build secure, serverless applications using AWS Lambda and DynamoDB. I was interested in understanding how cloud functions interact with managed databases, how to set up permissions properly, and how to troubleshoot real-world development challenges. Doing this project today allowed me to gain hands-on experience with these key AWS services and improve my cloud development skills.

---

## Project Setup

To set up my project, I created a database table called **UserData** in DynamoDB. The partition key for this table is **userId**, which means each user’s data is uniquely identified and accessed using their userId value. This setup allows me to efficiently store and retrieve individual user records based on their unique identifiers.
  
In my DynamoDB table, I added one item after the database became active by typing the following JSON into the editor:
```json
{
  "userId": "1",
  "name": "Test User",
  "email": "test@example.com"
}
```
This created a sample user record in my table.

DynamoDB is **schemaless**, which means you don’t have to define a fixed structure for your data ahead of time. Each item in the table can have different attributes, so you can easily add or modify data as your application evolves. The only requirement is that each item must have the primary key you specified (in this case, `userId`).

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-lambda_a112c3d5" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### AWS Lambda

AWS Lambda is AWS Lambda is a service that lets you run code without needing to manage any computers/servers - Lambda will manage them for you.

Lambda runs your code only when you need it to (so you're not paying for any idle time).

It also scales automatically, from a few requests per day to thousands per second - all you need to do is supply your code in one of the languages that Lambda supports. I'm using Lambda in this project to be the backend of my app.

---

## AWS Lambda Function
  
My Lambda function has an **execution role**, which is an IAM (Identity and Access Management) role assigned specifically to the function. This role defines what actions the Lambda function is allowed to perform in AWS, such as accessing other services like DynamoDB, S3, or writing logs to CloudWatch.

By default, AWS creates a basic execution role for Lambda that only allows writing logs to CloudWatch. That's why, when I tested my Lambda function, I could see error messages in the logs—because the function had permission to write those logs, but not necessarily to access other services like DynamoDB unless I added those permissions to the role. Properly configuring the execution role is essential for giving the function the right access while keeping it secure.

My code block defines a Lambda function that retrieves user data from a DynamoDB table called **UserData** using the AWS SDK for JavaScript.

- The first half of the code sets up DynamoDB clients and extracts the `userId` from the incoming event.
- It then builds a request to get an item from DynamoDB where the key matches the provided `userId`.
- In the `try` block, it sends the request and logs the raw DynamoDB response.
- If a user is found, it logs and returns the user data; if not, it logs that no user data was found and returns `null`.
- If there’s an error (like a missing item or permission problem), it catches the error, logs it, and returns the error object.

In summary, this Lambda function takes a `userId`, queries DynamoDB for matching user information, and returns the result, while handling any errors and providing helpful logs for troubleshooting.
  
The AWS SDK (Software Development Kit) is a collection of libraries provided by Amazon that makes it easy to interact with AWS services from your application code. The SDK handles authentication, request formatting, and error handling so you can focus on building features.

My code uses the AWS SDK to connect to DynamoDB, send queries, and retrieve user data directly from my Lambda function. By using the SDK, I can securely and efficiently access AWS resources without having to manually manage API calls or signatures.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-lambda_a1b2c3d5" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Function Testing

To test whether my Lambda function works, I created a test event in the Lambda console using this JSON:  
```json
{"userId": "1"}
```
This test simulates an API request to fetch the user with ID 1 from my DynamoDB table. If the test is successful, I will see the information of the user with id 1 (such as name and email) returned in the test results. This confirms that my Lambda function can correctly query DynamoDB and retrieve user data based on the provided userId.

I ran into this error because, although the Lambda function was able to execute (so the test showed “success”), it didn’t actually retrieve the data I wanted. The function’s response was a 400 “AccessDenied” error because—even though I created an execution role for the Lambda function—I haven’t given it explicit permission to access my DynamoDB table. 

DynamoDB is currently blocking my Lambda function from reading any items from the table, since the necessary permissions aren’t in place. Without those permissions, Lambda can’t access the data and has no choice but to report that its access was denied. 

To fix this, I need to update the Lambda function’s execution role so it includes permission to read from my DynamoDB table.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-lambda_u1v2w3x4" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Function Permissions

To resolve the AccessDenied error, I read the error message and realized I needed to provide the `dynamoDB:GetItem` action. I went to the configuration of my Lambda function, selected its execution role, and added a policy that allows the `GetItem` action. In this case, I used the **AmazonDynamoDBReadOnlyAccess** policy because it provides all the necessary read permissions (including `GetItem`, `Scan`, and `Query`) for DynamoDB tables, ensuring my Lambda function can safely read data without being able to modify or delete anything. This helps secure my database while allowing the function to perform the required read operations.

There were many DynamoDB permission policies I could choose from, but I didn’t pick **AWSLambdaDynamoDBExecutionRole** and **AWSLambdaInvocation-DynamoDB** even though their names mention Lambda and DynamoDB. When I expanded those two policies, I couldn’t spot the `GetItem` permission.

- **AWSLambdaDynamoDBExecutionRole** gives Lambda permission to read from DynamoDB streams—essentially a real-time feed of changes to your table (like new, updated, or deleted items) over the past 24 hours. It doesn’t grant permission to read individual items from the table using `GetItem`.
- **AWSLambdaInvocation-DynamoDB** is used to automatically trigger Lambda functions in response to events captured in DynamoDB streams. You’d use this if you want Lambda to automatically react to data changes (for example, sending a notification when a user adds a new item to their cart).
  
I also didn’t pick policies like **AWSLambdaDynamoDBExecutionRole** or **AWSLambdaInvocation-DynamoDB** because those are designed for working with DynamoDB streams or triggering Lambda functions based on table changes—not for reading items directly from a DynamoDB table.

**AmazonDynamoDBReadOnlyAccess** was the right choice because it gives my Lambda function permission to safely read data from any DynamoDB table, including using the `GetItem`, `Query`, and `Scan` actions. This ensures my function can retrieve user data without being able to modify or delete anything, which helps maintain security and follows best practices for granting the minimum necessary permissions.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-lambda_3ethryj2" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Final Testing and Reflection

To validate my new permission settings, I re-ran the Lambda function test after updating the execution role to include read access to DynamoDB. The result was a success—I received the user info:

```json
{
  "email": "test@example.com",
  "name": "Test User",
  "userId": "1"
}
```

because my Lambda function now has the correct permissions to read data from the DynamoDB table. The function could access the table, retrieve the item with userId "1", and return the expected user details. This confirms that the permission issue was resolved and the function works as intended.

Web apps are a popular use case for using Lambda and DynamoDB. For example, I could:

- Build a user registration and login system where Lambda handles authentication and DynamoDB stores user profiles.
- Create a serverless REST API for managing products, orders, or comments, with Lambda acting as the backend logic and DynamoDB storing the data.
- Implement real-time notifications or updates by combining Lambda with DynamoDB Streams, so users get instant feedback when data changes.
- Develop scalable analytics dashboards where Lambda functions aggregate and process data from DynamoDB in response to user queries.
- Enable personalized content delivery in a web app, where Lambda fetches user preferences from DynamoDB and customizes the user experience.

These patterns help create applications that are highly scalable, cost-efficient, and easy to maintain, taking advantage of the serverless architecture and managed database.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-lambda_p9thryj2" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Enahancing Security
  
I am replacing my Lambda function’s permission policy because I want to make my project more secure by using an **inline policy** that only allows access to the specific `UserData` table. The previous managed policy (like `AmazonDynamoDBReadOnlyAccess`) gave my Lambda function permission to read from all DynamoDB tables in my AWS account, which is more access than I need. By writing an inline policy, I can restrict Lambda to only read from the `UserData` table, preventing it from accessing any other tables. This follows the principle of least privilege and helps protect my other data resources.

To create the permission policy, I used an **inline policy** like this:
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "DynamoDBReadAccess",
            "Effect": "Allow",
            "Action": [
                "dynamodb:GetItem"
            ],
            "Resource": [
                "arn:aws:dynamodb:YOUR-REGION:YOUR-ACCOUNT-ID:table/UserData"
            ]
        }
    ]
}
```

because this method lets me precisely control which actions my Lambda function can perform and which DynamoDB table it can access. By specifying only the `dynamodb:GetItem` action and restricting the resource to just the `UserData` table, I follow the principle of least privilege—my Lambda function cannot read from or modify any other tables in my AWS account. This setup is more secure and tailored to my application’s requirements than using a broad managed policy.

When updating a Lambda function’s permission policies, you could risk accidentally breaking your function’s ability to access required resources—especially if the new policies are too restrictive. I validated that my Lambda function still works by re-running a test event after updating the permission policy. I checked that the function was able to successfully retrieve data from the `UserData` table, confirming that my new, tighter policy still allows the function to perform its intended task without losing necessary access.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-lambda_1qthryj2" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

---
