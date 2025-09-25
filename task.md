Some Guidance 😅

Ooooo, a challenger!

Welcome to the Some Guidance version of this project. Challenge yourself and see how far you can go without looking for detailed steps.



📣 Delete all your resources by the end of the day, even if you don't finish the entire project. 

If you're EVER stuck - ask the NextWork community.. Students like you are already asking questions about this project.s

Over 7 steps, you'll learn how to create a STUNNING dashboard of graphs and visualisations.

P.S. You can always switch to the Step-by-Step Guidance version of this project if you like. We won't tell 😉



It's always good to understand exactly what you're here to do.





If you're up for a bit of a challenge, quiz yourself on the key concepts up ahead in this project!



Don't be too nervous - you'll also get a quiz at the end of this project, to recap what you've learnt.



Let's start by getting the Netflix data files we'll need for this project! This is the foundation for all the cool visualizations we're about to create.








Download netflix_titles.csv. (right click, and select Save Link As). This file contains all the data we're analysing!



Download manifest.json. (right click, and select Save Link As). We'll explain why we need it in a minute!



Time to put our dataset in the cloud! We'll use S3 to store the files, which makes it super easy for QuickSight to access later.




In this step, get ready to:

Create an S3 bucket to store your dataset files.

Upload the CSV file to your bucket.

Modify and upload the manifest.json file to your bucket.









In your S3 console, create a new bucket called nextwork-quicksight-project-name (replace name with your name).



Upload the CSV file into the bucket.



Replace the URL in the manifest.json file with the S3 URL of your CSV file before uploading it.











Now we'll set up QuickSight - this is where the magic happens! It's AWS's tool that turns raw data into beautiful charts and graphs.




In this step, get ready to:

Sign up for a free trial of Amazon QuickSight - make sure not to sign up to any add-ons (you'll get charged if you do)

Configure QuickSight to access your S3 bucket.

Complete your account setup.









Navigate to Amazon QuickSight.



Sign up for a free trial of the Enterprise edition.



💡 Is this free?
QuickSight comes with a free trial so you won't be charged for this project - just make sure that you're using the AWS Paid Plan and that you delete your QuickSight account at the end of your session today. We'll remind you to do this at the end of this guide!

Also make sure to follow the sign up steps below closely! There are some optional upgrades that will end up charging your account in this sign up flow - we'll show you how to avoid them.





🚨 IMPORTANT STEP 🚨
Make sure you uncheck this offer to upgrade.





✋ Stop and double check
Did you uncheck the Add Paginated Reports checkbox? Make sure to do this so your AWS account doesn't get charged!

p.s. A shoutout to Dev in the NextWork community for suggesting we make this step super clear 👏





Make sure S3 is selected as a resource that QuickSight has access to, and select your S3 bucket.



Finish creating your QuickSight account.



It might take a minute for your account to be ready - time for a stretch 😉











Let's connect our Netflix data to QuickSight! Our data is still in our S3 bucket, and it's time for these two awesome services to meet.




In this step, get ready to:

Set up a new dataset in QuickSight.

Connect QuickSight to your S3 bucket using the manifest.json file.

Prepare for data visualization.









In your QuickSight menu, navigate to Datasets and create a new dataset using S3 data.





Source name: kaggle-netflix-data



manifest.json URL: the S3 URL of your manifest.json file.



🙋‍♀️ I ran into an error!
No stress - errors can happen all the time, especially when you're trying to connect your dataset with QuickSight.

Here are the top errors students run into at this step:





QuickSight doesn't have permissions to access that database.
OR
We cannot find or cannot access one or more of the buckets listed in the manifest.





Did you connect QuickSight to your S3 bucket when you set up your QuickSight account?



Check this by selecting the profile icon on the top right, selecting Manage QuickSight and then Security & permissions.



Under QuickSight access to AWS services, select Manage and add your S3 bucket. 




We can't parse the manifest file as a valid JSON.





Is your QuickSight account set up in the exact same region as your S3 bucket's region?



Check this by heading back to your S3 console, and noting the name of the region at the top right corner of the screen.



Compare this with the region name you see when you're in the QuickSight dashboard and select on the profile icon at the top right.



If you're in the right region, did you accidentally delete a bracket or change the formatting in manifest.json when you added your S3 bucket's URI? 




We can't connect to this S3 bucket. Make sure any S3 buckets you specify are associated with the AWS account used to create this QuickSight





Is your QuickSight email the same as your AWS account's email?



If they're not, try deleting your QuickSight account (instructions are in the 🗑️ Delete your resources step) and recreating your QuickSight account with the same email as AWS. 




Not enough SPICE capacity





Have you switched AWS Regions after creating your QuickSight account?



For each AWS account, you only get free SPICE capacity to process your data in a single region - the region where you created this QuickSight account. 




Still stuck? Ask the NextWork community!













💡 What is the source name for?
This source name is just a label to remind you what this piece of data is about! In this case, we sourced the csv field from a database called Kaggle, and the file is all about Netflix TV shows and movies.


💡 So what does the manifest.json file do?
The manifest.json file is like a map that tells Amazon QuickSight where your data lives and how to read your data. Just like how people can speak in many different languages, data can come in all different kinds of forms! manifest.json tells QuickSight what your dataset looks like, so QuickSight knows how to understand the data and show it in charts or graphs. Without this map, QuickSight might get confused and not show your data correctly!



Now we get to the creative part - it's time to create your first visualization! We'll start bringing those Netflix numbers to life with charts.




In this step, get ready to:

Create your first visualization using the dataset fields.

Explore different chart types and customization options.

Build a multi-visualization dashboard.





With QuickSight, you can sort, filter, and customise your data top create visualisations. You can also experiment with different types of graphs like bar charts, pie charts, line graphs, etc.





On the left hand navigation panel, the dataset's fields have already been imported.



Create your first visualisation - drag the release_year field into the Y Axis heading.







Turn this into a donut chart.



Resize your donut chart so it doesn't take up your entire screen/dashboard.



Create a new visual so your next visualisation doesn't replace the one you've just created.



Set up your next Horizontal bar chart comparing release_year with type.















Ready for a challenge? The Netflix team has some questions for us, and we'll answer them by creating some targeted visualizations.




In this step, get ready to:

Create multiple visualizations to answer specific data questions.

Apply filters to analyze subsets of your data.

Organize your visualizations in a dashboard layout.





Let's say you've just presented your first two QuickSight charts to Netflix's data team lead, and they're impressed and would like to see what else you can find! They give you a brief with 5 questions that you should be able to answer with QuickSight.

Continue adding to your dashboard (make sure not to replace your existing graph for each task). Some of these are more advanced requests than others, but give it a go!



💡 If you get stuck, scroll up and select Step-by-Step Guidance for all the instructions.





Remember to add a new chart instead of replacing the current one!

"I quite like the breakdown of TV shows/movies for each release year. Would it be possible to stack movies and TV shows in the same row, so you can visualise the % of each?"





Remember to add a new chart instead of replacing the current one!

"Now can you show me the same thing in a table? i.e. please show me the number of movies vs TV shows per release year in a table."





Remember to add a new chart instead of replacing the current one!

"On what day did Netflix add the largest number of movies/TV shows to their catalogue?"





Remember to add a new chart instead of replacing the current one!

"Of the TV shows and movies featured, how many were listed as 'Action & Adventure', 'TV Comedies', or 'Thrillers'? For simplicity, ignore the TV shows and movies that have multiple listings/categories."





Remember to add a new chart instead of replacing the current one!

"Of the TV shows and movies with the listing 'Action & Adventure', 'TV Comedies', or 'Thrillers', how many were released on 2015 or after?"













Welcome to your 🤫 exclusive 🤫 secret mission!

This secret mission, should you choose to accept it, is to conduct a data refresh to add new data into your visualizations.




💎 In this secret mission, get ready to:





Update your original dataset in S3.



Refresh your data in QuickSight.



Showcase your secret mission in your project documentation.







Wow! That dashboard is looking beautiful 😍

Let's wrap it up by adding some polish and making sure it's ready to share with others.




In this step, get ready to:

Add descriptive titles to your visualizations.

Publish your dashboard for sharing.

Export your dashboard as a PDF.









To round it all off, edit the titles in your charts so that anyone can understand them at a glance.







Publish your dashboard, and export it as a PDF. Make sure you download it!













WOAH! Did you just finish an AWS project?

Woohooooo!! You are an absolute legend and should be very proud of yourself. That was not easy work - QuickSight can get a bit wild - but you gave it a go, did your best, and absolutely crushed it.













Make sure you delete all your resources to avoid getting charged. This is a super important task for every project you set up.

We challenge you to try to give this a go yourself 💪

Do you think you can delete the resources you've created today?

QuickSight account

S3 bucket and objects








Fantastic! Challenging yourself like this is how real learning starts.



If you're feeling stuck, here's a step-by-step guide:




To terminate your QuickSight account





After returning to the QuickSight home page, select the user icon on the top right corner, and then Manage QuickSight:







Select Account Settings from the left hand navigation panel, and then Manage at the bottom of the page.







Switch off account termination, type confirm in the text field, and then select Delete account.







Success! You've successfully unsubscribed from QuickSight.



To delete your S3 bucket





Go to the S3 console and select your bucket.



Select Delete.



You'll notice that you can't delete the bucket because there are still objects inside. Select Empty bucket.







Type permanently delete in the text input field and select Empty.



Once the bucket is empty, select your bucket again and choose Delete.



Type your bucket name to confirm the deletion and select Delete bucket.







Now it's time to share and let people know just what an amazing job you've done.



1. Share it on LinkedIn 😎‍.

It's so easy to share your documentation - all you have to do is:





SelectMission Accomplished at the end of this page 👇







Download your documentation as a PDF.