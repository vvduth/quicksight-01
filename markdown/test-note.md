## 💡 What does Selecting Restrict to slot  mean?

✍️ What does the Restrict to slot values setting mean?
This slot type has restricted slot values, which means makes sure that only the values that you specify will count as a valid accountType!

Otherwise, Amazon Lex might use machine learning to accept other values that it frequently encounters from users.

In my  case, I don't want Lex to use machine learning to accept other values. I have a set list of bank account types that I offer - Checking, Savings, and Credit accounts - and I don't offer any other bank account types.

If Lex starts accepting other values outside of these three, users could end up having conversations about bank account types I don't actually offer! To prevent that from happening, I'll restrict Lex to only acknowledge the bank account types I set up here.

✍️ How did you set up failure responses?
I also used failure responses to. advance settings for my failtures response, by default, if i answer my date of birth wrong many times when the bot asked for it (cheese



what's YOUR birthday?!



i have no clue



not today), the bot will will give my  all three variations of the slot prompt, before it finally switches to the failure response and then  switches to the FallbackIntent message right after my  failure response.... A default setting I changed was Advanced options under the dateOfBirth slot =>  Slot capture: failure response section => Next step in conversation  => slecte the Intent  dropdown => Elicit a slot.



Select dateOfBirth under Slot.



Select the option to Skip elicitation prompt, which makes sure your bot doesn't ask your user for their birthday twice in a row. ...
 
