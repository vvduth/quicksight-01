Here’s why I had to troubleshoot after deploying my bot:

After deployment, I received an error: **“Invalid bot config: access denied while invoking Lambda function.”** This happened because the AWS Lambda function didn’t have the correct permissions, so it couldn’t pass data back to my chatbot.

**Why was there an error?**
- The Lambda function’s permissions were not set up correctly via CloudFormation.
- Lex needed explicit permission to invoke the Lambda, but the resource-based policy was missing or misconfigured.

**How did I fix it?**
1. **Created and deployed a new Lambda function** called `BankingBotEnglish`, using Python 3.12 (or later).
2. **Added the same code** from my CloudFormation template to this new function.
3. **Redirected my Lex Version 1 Alias** to use the new Lambda function.
4. **Adjusted permissions:**  
   - Navigated to the Permissions tab in Lambda.
   - Added a new resource-based policy statement to grant Lex access:
     - **Service:** Other
     - **Statement ID:** my-custom-permission-amazonlexchatbot
     - **Principal:** lexv2.amazonaws.com
     - **Source name:** (Used the ARN from the error message, e.g., arn:aws:lex:ap-southeast-2:640168412593:bot-alias/*)
     - **Action:** lambda:InvokeFunction

**Result:**  
Once the right permissions were set, Lex could successfully invoke the Lambda function, and my bot worked as expected!

---

**Summary for the end user:**  
Sometimes, automated deployments (like with CloudFormation) don’t set all the tricky permissions you need for services to talk to each other. Troubleshooting and manually fixing the Lambda resource policy ensures your chatbot and backend logic work smoothly together.