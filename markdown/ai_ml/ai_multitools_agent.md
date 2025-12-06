## overview
' today we are goong to build a custimer serverice agent, till a a dummy agent, which of course is miising manay functionaliy,
 missing extensive testing 
source code: https://github.com/vvduth/quicksight-01/tree/main/code/multitools-agent

 ## about the project 
- I also got a dummy sqlite dataabe running in this project. in this file I crreated some dunmy data to test with.
And in there, I'm creating some dummy tables, one for customers, one for

orders. Every customer has a first name, a last name and a pin,

and the pin here will be stored as plaintext in that

database. You would not want to do that in production.

You would want to hash it there, but to keep things simple, I'll store

it as plaintext here, as you'll see. I got a simple

orders table which has order IDs, customer IDs, a

date for each order, and also a product, a name, and

an amount for every order. And then I clear

these tables whenever I start this program to then repopulate them with

some dummy data. And as mentioned, these are the

plaintext pins which, again, you wouldn't store as plaintext in a

production database. That's important. But for this demo, it's fine.

def create_db_and_tables():
    conn = sqlite3.connect("dummy_database.db")
    cursor = conn.cursor()

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS customers (
        id INTEGER PRIMARY KEY,
        first_name TEXT NOT NULL,
        last_name TEXT NOT NULL,
        pin TEXT NOT NULL
    )
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS orders (
        id INTEGER PRIMARY KEY,
        customer_id INTEGER NOT NULL,
        date TEXT NOT NULL,
        product_name TEXT NOT NULL,
        amount REAL NOT NULL,
        FOREIGN KEY (customer_id) REFERENCES customers (id)
    )
    """)

    cursor.execute("DELETE FROM orders")
    cursor.execute("DELETE FROM customers")

    customers = [
        ("John", "Doe", "1234"),
        ("Jane", "Smith", "5678")
    ]
    cursor.executemany("INSERT INTO customers (first_name, last_name, pin) VALUES (?, ?, ?)", customers)

    customer_ids = [row[0] for row in cursor.execute("SELECT id FROM customers")]
    orders = [
        (customer_ids[0], (datetime.now() - timedelta(days=10)).isoformat(), "Laptop", 1200.00),
        (customer_ids[0], (datetime.now() - timedelta(days=45)).isoformat(), "Mouse", 25.00),
        (customer_ids[1], (datetime.now() - timedelta(days=5)).isoformat(), "Keyboard", 75.00)
    ]
    cursor.executemany("INSERT INTO orders (customer_id, date, product_name, amount) VALUES (?, ?, ?, ?)", orders)

    conn.commit()
    conn.close()

## all the tools/fucn i defined to the agent:
* create_db_and_tables import from database.py mention above
* verify_customer: verifies customer using their name and pin, return customer id if valid return -1 if invalid
 ```python
def verify_customer(name: str, pin: str) -> int:
    """
    Verifies a customer's identity using their name and PIN.
    Returns the customer ID if verified, or -1 if not found.
    """
    conn = sqlite3.connect(DB_FILE)
    cursor = conn.cursor()
    first_name, last_name = name.lower().split()
    cursor.execute(
        "SELECT id FROM customers WHERE LOWER(first_name) = ? AND LOWER(last_name) = ? AND pin = ?",
        (first_name, last_name, pin),
    )
    result = cursor.fetchone()
    conn.close()
    if result:
        return result[0]
    return -1 
```
* get_orders which get customer id ad pram and ruen order history for that customer
```python
    def get_orders(customer_id: int) -> List[dict]:
    """
    Retrieves the order history for a given customer.
    """
    conn = sqlite3.connect(DB_FILE)
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()
    cursor.execute(
        "SELECT * FROM orders WHERE customer_id = ?", (customer_id,))
    orders = [dict(row) for row in cursor.fetchall()]
    conn.close()
    return orders
```
* check_refund_eligibility which checks if an order is eligible for refund.an order is eligible if it was placed within the last 30 days.
```python
def check_refund_eligibility(customer_id: int, order_id: int) -> bool:
    """
    Checks if an order is eligible for a refund.
    An order is eligible if it was placed within the last 30 days.
    """
    conn = sqlite3.connect(DB_FILE)
    cursor = conn.cursor()
    cursor.execute(
        "SELECT date FROM orders WHERE id = ? AND customer_id = ?", (
            order_id, customer_id)
    )
    result = cursor.fetchone()
    conn.close()
    if not result:
        return False
    order_date = datetime.fromisoformat(result[0])
    return (datetime.now() - order_date).days <= 30

```
* issue_refund which issues a refund for a given customer id and order id. in rality, this will trigger some payment gateway logic, but in this dummy app we just print out a message to the console.
```python
def issue_refund(customer_id: int, order_id: int) -> bool:
    """
    Issues a refund for an order.
    """
    # in reality, this would be stored in some database
    print(f"Refund issued for order {order_id} for customer {customer_id}")
    return True
```  
* share_feedback which allows a customer to share feedback. in this app we just orint out usertomer i dan dthe feedback to the console.
```python
def share_feedback(customer_id: int, feedback: str) -> str:
    """
    Allows a customer to share feedback.
    """
    # in reality, this would be stored in some database
    print(f"Feedback received from customer {customer_id}: {feedback}")
    return "Thank you for your feedback!"
```

=> that all functions we need for this app
## structure the agent
* before junmping int o the main() function, let declare the tools we just defined as tool for the agent to use.
* why we do this? because when we create the agent later, we will pass in these tools so that the agent can call them when needed.
```python
available_functions = {
    "verify_customer": verify_customer,
    "get_orders": get_orders,
    "check_refund_eligibility": check_refund_eligibility,
    "issue_refund": issue_refund,
    "share_feedback": share_feedback,
}
```

define the tools with the fucntion name and description and parameters they take so the agent can understand how to use them.

```python
tools = [
    {
        "type": "function",
        "name": "verify_customer",
        "description": "Verifies a customer's identity using their full name and PIN.",
        "parameters": {
            "type": "object",
            "properties": {
                "name": {
                    "type": "string",
                    "description": "The customer's full name, e.g., 'John Doe'.",
                },
                "pin": {"type": "string", "description": "The customer's PIN."},
            },
            "additionalProperties": False,
            "required": ["name", "pin"],
        },
    },
    {
        "type": "function",
        "name": "get_orders",
        "description": "Retrieves the order history for a verified customer.",
        "parameters": {
            "type": "object",
            "properties": {
                "customer_id": {
                    "type": "integer",
                    "description": "The customer's unique ID.",
                }
            },
            "additionalProperties": False,
            "required": ["customer_id"],
        },
    },
    {
        "type": "function",
        "name": "check_refund_eligibility",
        "description": "Checks if an order is eligible for a refund based on the order date.",
        "parameters": {
            "type": "object",
            "properties": {
                "customer_id": {
                    "type": "integer",
                    "description": "The customer's unique ID.",
                },
                "order_id": {
                    "type": "integer",
                    "description": "The unique ID of the order.",
                },
            },
            "additionalProperties": False,
            "required": ["customer_id", "order_id"],
        },
    },
    {
        "type": "function",
        "name": "issue_refund",
        "description": "Issues a refund for an order.",
        "parameters": {
            "type": "object",
            "properties": {
                "customer_id": {
                    "type": "integer",
                    "description": "The customer's unique ID.",
                },
                "order_id": {
                    "type": "integer",
                    "description": "The unique ID of the order.",
                },
            },
            "additionalProperties": False,
            "required": ["customer_id", "order_id"],
        },
    },
    {
        "type": "function",
        "name": "share_feedback",
        "description": "Allows a customer to provide feedback about their experience.",
        "parameters": {
            "type": "object",
            "properties": {
                "customer_id": {
                    "type": "integer",
                    "description": "The customer's unique ID.",
                },
                "feedback": {
                    "type": "string",
                    "description": "The feedback text from the customer.",
                },
            },
            "additionalProperties": False,
            "required": ["customer_id", "feedback"],
        },
    },
]

```
* after that, we can create a def execute_tool_call(tool_call) -> str: to execute the tool call when the agent decides to use a tool and return the result as a string. the fucn will get the tool name and arguments from the tool_call object, look up the corresponding function from available_functions dictionary, and call it with the provided arguments. if the function call is successful, it returns the result as a string. if there's an error during the function call, it catches the exception and returns an error message.

```python
def execute_tool_call(tool_call) -> str:
    """
    Executes a tool call and returns the output.
    """
    fn_name = tool_call.name
    fn_args = json.loads(tool_call.arguments)

    if fn_name in available_functions:
        function_to_call = available_functions[fn_name]
        try:
            print(f"Calling {fn_name} with arguments: {fn_args}")
            # The return value of the function is converted to a string to be compatible with the API.
            return str(function_to_call(**fn_args))
        except Exception as e:
            return f"Error calling {fn_name}: {e}"

    return f"Function {fn_name} not found."

```

## main function: 
* save the best for last. in the main function, we will create the agent using the OpenAI model, the tools we defined earlier, and the execute_tool_call function to handle tool calls.
* first, define the system messages to set the behavior of the agent. in this case, we want the agent to be a friendly and helpful customer service agent who always verifies the customer's identity before providing any sensitive information.
```python
 messages = [
        {
            "role": "developer",
            "content": """
                You are a friendly and helpful customer service agent. 
                You must ALWAYS verify the customer's identity before providing any sensitive information. 
                You MUST NOT expose any information to unverified customers.
                You MUST NOT provide any information that is not related to the customer's question.
                DON'T guess any information - neither customer nor order related (or anything else).
                If you can't perform a certain customer or order-related task, you must direct the user to a human agent.
                Ask for confirmation before performing any key actions.
                If you can't help a customer or if a customer is asking for something that is not related to the customer service, you MUST say "I'm sorry, I can't help with that."
            """}
    ]

```

and then, we can start the main interaction loop where the agent will continuously prompt the user for input, process the input using the OpenAI model, and respond accordingly. if the agent decides to call a tool, it will use the execute_tool_call function to perform the action and append the result to the conversation history.
```python
print("Welcome to the customer service chatbot! How can we help you today? Please type 'exit' to end the conversation.")
    while True:
        user_input = input(
            "Your input: ")
        if user_input == "exit":
            break

        messages.append({"role": "user", "content": user_input})

        for _ in range(5):  # limit tool call / assistant cycles to prevent infinite loops
            response = client.responses.create(
                model='gpt-4o',
                input=messages,
                # you can pass tools here, visit the documentation for more details
                tools=tools,
            )

            output = response.output

            for reply in output:
                messages.append(reply)

                if reply.type != "function_call":
                    # only print the message content if it's a regular message
                    print(reply.content[0].text)
                else:
                    # try to execute the tool call
                    tool_output = execute_tool_call(reply)
                    ## append the tool output to the messages
                    messages.append({
                        "type": "function_call_output",
                        "call_id": reply.call_id,
                        "output": str(tool_output),
                    })
            # check if the last messge is not a dictionary (which means it's a regular message)
            # brack out of for tool call loop and ask user for next input
            if not isinstance(messages[-1], dict) and messages[-1].type == "message":
                break
```

Result:
```bash 
PS C:\Users\ducth\git_repos\quicksight-01\code\multitools-agent> uv run main.py
Welcome to the customer service chatbot! How can we help you today? Please type 'exit' to end the conversation.   
Your input: hello, I have a request
Hello! How can I assist you today?
Your input: I want to see my order history
I'd be happy to help with that. Can you please provide your full name and PIN for verification?
Your input: fullname: John Doe PIN:1234
Calling verify_customer with arguments: {'name': 'John Doe', 'pin': '1234'}
Thank you for verifying your identity, John. I will now retrieve your order history.
Calling get_orders with arguments: {'customer_id': 1}
Here is your order history:

1. **Laptop**
   - Date: November 26, 2025
   - Amount: $1200.00

2. **Mouse**
   - Date: October 22, 2025
   - Amount: $25.00

Is there anything else you'd like to know?
Your input: I want a refund
For which order would you like a refund? The Laptop or the Mouse?
Your input: laptop
Calling check_refund_eligibility with arguments: {'customer_id': 1, 'order_id': 1}
The Laptop order is eligible for a refund. Would you like me to proceed with issuing the refund?
Your input: yes
Calling issue_refund with arguments: {'customer_id': 1, 'order_id': 1}
Refund issued for order 1 for customer 1
The refund for your Laptop has been successfully processed. Is there anything else I can help you with?
Your input:

```