<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Website Delivery with CloudFront

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-networks-cloudfront)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

## Website Delivery with CloudFront

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-networks-cloudfront_1dddddwe)

---

## Introducing Today's Project!
  
In this project, I’m here to demonstrate how to use CloudFront to distribute traffic to my website and minimize loading time. I’ll also compare different hosting methods and analyze their performance. My main goal is to learn about Content Delivery Networks (CDNs) and get hands-on with the presentation layer of a three-tier architecture website—understanding how CDNs fit into the overall structure, which includes the presentation, logic, and data layers.

### Tools and concepts

In this project, I learnt about several key AWS services and concepts:

- Amazon S3: Used for storing and hosting static website files.
- Amazon CloudFront: A Content Delivery Network (CDN) that speeds up content delivery by caching files at edge locations worldwide.
- S3 Bucket Policies: Used to control access permissions for files stored in S3.
- Origin Access Control (OAC): Secures access between - CloudFront and S3 by ensuring only CloudFront can retrieve bucket contents.
- Static Website Hosting: How to host a website directly from an S3 bucket, including configuring permissions and bucket settings.
- CDN Performance and Security: The benefits of using CloudFront over direct S3 hosting, including faster load times and more secure access controls.
I also learned about the importance of proper permission settings, differences between public and private access, and how caching improves website performance for global users.

### Project reflection

This project took me approximately 2 hours to complete. The most challenging part was troubleshooting permission errors between S3 and CloudFront, especially when figuring out the right bucket policies and access control settings to avoid Access Denied or 403 errors. It was most rewarding to finally see my website successfully hosted and delivered through CloudFront, knowing I had configured everything securely and optimized for fast global performance.

I chose to do this project today because I wanted to learn more about hosting static websites on AWS, improve my cloud skills, and understand the differences between serving content directly from S3 versus using CloudFront as a CDN. It was a great opportunity to get hands-on experience with cloud services and strengthen my understanding of web hosting, permissions, and performance optimization.

---

## Set Up S3 and Website Files

I started the project by creating an S3 bucket to store all my website’s files securely and reliably in the cloud. I can’t use CloudFront alone for this task because CloudFront is a content delivery network (CDN) that distributes and caches content, but it needs an origin location for the files. In my case, that origin is Amazon S3—CloudFront will deliver the website content from S3 to users around the world, improving load times and performance.

 
The three files that make up my website are:  
- index.html, which contains the main structure and content of my website.  
- style.css, which defines the visual appearance and layout by applying styles to the HTML elements.  
- script.js, which adds interactivity and dynamic features to the website using JavaScript.

I validated that my website files work by opening index.html in my browser to make sure the layout, styles, and functionality all display correctly. This let me confirm that the HTML, CSS, and JavaScript files are working together as expected before uploading them to S3.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-networks-cloudfront_qgo7wcd3)

---

## Exploring Amazon CloudFront

 Amazon CloudFront is a content delivery network (CDN) that speeds up the distribution of both static and dynamic web content—such as .html, .css, .js, and image files. CloudFront works by caching your website content on multiple servers located around the world. When someone visits your site, their request is routed to the nearest edge location, which reduces latency and delivers your content faster.

Businesses and developers use CloudFront because caching helps store copies of files in temporary locations closer to users, improving load times and overall performance. This makes websites more responsive and reliable, especially for audiences in different geographic regions.

To use Amazon CloudFront, I set up distributions, a distribution in Amazon CloudFront is a set of instructions that tells CloudFront how to deliver your content to users. When you set up a distribution, you specify details like the origin (where your files are stored—in my case, the newly created S3 bucket), caching settings, and security options. Setting up a distribution for my single website means CloudFront knows where to fetch my files and how to serve them quickly and securely to visitors around the world.
  
My CloudFront distribution’s default root object is set to `index.html`. This means that when users access my website through CloudFront without specifying a file name in the URL (for example, just the domain name), CloudFront will automatically serve the `index.html` file as the default homepage. This ensures visitors always see the main page of my site, even if they don’t specify an exact file path.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-networks-cloudfront_qgo7wcdt)

---

## Handling Access Issues
  
When I tried visiting my distributed website, I ran into an access denied error because my S3 bucket is private by default and I haven’t configured the proper permissions for CloudFront to access it. Although my origin access was set to public, I didn’t set up Origin Access Control (OAC) or an appropriate bucket policy. CloudFront needs explicit permission to read files from the S3 bucket—without this, it can’t serve my website content and returns an access denied error. To fix this, I need to grant CloudFront permission through a bucket policy or by using OAC settings.

My distribution’s origin access settings were set to public. This led to the access denied error because making the origin public doesn’t automatically change the permissions of the objects in your S3 bucket—by default, those objects remain private for security reasons. To fix this, you have to manually set your S3 objects to public so CloudFront can access and serve your website content. In summary, even if the origin is public, CloudFront can’t access your site’s files until the objects themselves are also set to public.

To resolve the error, I set up origin access control (OAC). OAC  is a special user for CloudFront that prevents this. An OAC lets you keep your S3 bucket and objects not publicly accessible, while still making sure they can be accessed through CloudFront.

OAC also gives you granular control over how CloudFront accesses the content. For example, you can add other authentication or security settings to make sure only legitimate users can access your content.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-networks-cloudfront_egrhntyu)

---

## Updating S3 Permissions

Once I set up my Origin Access Control (OAC), I still needed to update my S3 bucket policy because the bucket policy is what actually grants CloudFront permission to access the files in my bucket. OAC establishes a secure connection between CloudFront and S3, but without an updated bucket policy that explicitly allows CloudFront’s identity to read the objects, access will still be denied. Updating the bucket policy ensures only CloudFront (and not the public) can retrieve and serve my website content securely.

Creating an OAC automatically gives me a policy I could copy, which grants the CloudFront service permission to retrieve objects (s3:GetObject) from your S3 bucket (nextwork-three-tier-dukem), but only when the request comes specifically from your CloudFront distribution with the ARN arn:aws:cloudfront::841162690953:distribution/E29AF12EHK58JS. The policy uses a condition to ensure that only requests originating from this CloudFront distribution are allowed, keeping your S3 bucket private from the public and securely restricting access to just CloudFront.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-networks-cloudfront_eg98ntyu)

---

## S3 vs CloudFront for Hosting

I initially had a 403 Forbidden error with static website hosting because the permissions on my S3 bucket or objects did not allow public access. By default, S3 buckets and their contents are private for security reasons. To successfully host a static website directly from S3, you need to update your bucket policy or object permissions to make your website files publicly readable. Without these changes, users will get a 403 Forbidden error when trying to access the site via the S3 website URL.

I still ran into an error because unchecking "Block all public access" for my S3 bucket only removes the automatic blocking—it doesn't actually grant any permissions to access the objects inside. By default, all objects in an S3 bucket remain private, and simply disabling the block doesn’t make them public.

To make your website files accessible, you need to update the bucket policy to explicitly allow public read access (s3:GetObject). The bucket policy is what tells AWS who can access your files and what actions they’re allowed to take. When you add a policy that allows public read access, you’re specifically instructing AWS to let anyone on the internet view your website content. Without this policy, access will still be denied, even if public access is no longer blocked.

I could finally see my S3 hosted website when I Add the following statement to S3 bucket's policy window: 

{
     "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::nextwork-three-tier-dukem/*"
}


This works because this policy explicitly grants public read access to all objects in my S3 bucket. By adding this statement, I allowed anyone on the internet (“Principal”: “*”) to perform the s3:GetObject action and view my website files. Without this policy, S3 keeps objects private by default, preventing users from accessing the site. With public read allowed, my static website became accessible through the S3 website URL.

Compared to the permission settings for my CloudFront distribution, using S3 meant I had to set my files and bucket policy to allow public read access so anyone could view my site. This is less secure, since it makes your content publicly accessible to everyone.

With CloudFront, I could keep my S3 bucket private and only grant CloudFront permission to access the files through Origin Access Control (OAC) and a restrictive bucket policy. This approach is more secure because it ensures only CloudFront can serve my content, and my files aren’t exposed directly to the public.

I preferred CloudFront’s permission settings because they give me better security and control over who can access my website’s files.

---

## S3 vs CloudFront Load Times

Load time means how long it takes for a website’s content to be delivered and displayed in the user’s browser after they visit the site. The load times for the CloudFront site(102ms) were faster than the S3 site (271ms) - in my internet because CloudFront is a global Content Delivery Network (CDN) that caches copies of your website files at edge locations around the world. When a user visits your site through CloudFront, their request is served from the nearest edge server, reducing latency and speeding up delivery. In contrast, S3 static website hosting serves files directly from the AWS data center where your bucket is located, which can be much farther away from the user, resulting in slower load times.
  
A business would prefer CloudFront when performance, global reach, and security are priorities. CloudFront is ideal for sites with users around the world because it uses edge locations to cache and deliver content quickly, reduces latency, and provides more advanced security features such as HTTPS, origin protection, and access controls.

S3 static website hosting might be sufficient when the website is simple, has low traffic, or is intended for users in a specific region. It’s best for basic sites, prototypes, internal tools, or when minimal configuration and cost are important, since it’s easier to set up and manage but doesn’t offer the same speed or security as CloudFront.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-networks-cloudfront_12verpuh)

---

---
