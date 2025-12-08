# Building a Multi-Tool AI Customer Service Agent

## Overview

This project demonstrates building a customer service agent powered by AI with multi-tool capabilities. This is a demo implementation that showcases core functionality but is missing extensive testing and production-ready features.

**Source Code:** [GitHub Repository](https://github.com/vvduth/quicksight-01/tree/main/code/multitools-agent)

---

## About the Project

### Database Setup

This project uses a SQLite database with dummy data for testing purposes. The database contains two main tables:

**Customers Table:**
- `id` - Customer unique identifier
- `first_name` - Customer's first name
- `last_name` - Customer's last name  
- `pin` - Customer PIN for verification

**Orders Table:**
- `id` - Order unique identifier
- `customer_id` - Foreign key referencing customers
- `date` - Order date
- `product_name` - Product name
- `amount` - Order amount

**⚠️ Important Security Note:** PINs are stored as plaintext in this demo for simplicity. In production environments, you should **always hash sensitive credentials** before storing them in the database. This plaintext approach is **only acceptable for demonstration purposes**.

The database is cleared and repopulated with dummy data each time the program starts to ensure a consistent testing environment.

---

## Database Implementation

```python
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
```

---

## Agent Tools and Functions

The agent has access to five core functions that enable it to perform customer service operations. Each function is defined with proper documentation and error handling.

### 1. Database Initialization

**Function:** `create_db_and_tables()`

Imported from `database.py`. Creates the database schema and populates it with sample data.

### 2. Customer Verification

**Function:** `verify_customer(name: str, pin: str) -> int`

Verifies customer identity using their full name and PIN. Returns the customer ID if verification succeeds, or `-1` if authentication fails.

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

### 3. Order History Retrieval

**Function:** `get_orders(customer_id: int) -> List[dict]`

Retrieves the complete order history for a specific customer using their customer ID.

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

### 4. Refund Eligibility Check

**Function:** `check_refund_eligibility(customer_id: int, order_id: int) -> bool`

Determines whether an order qualifies for a refund. The eligibility rule is simple: orders placed within the last 30 days are eligible for refunds.

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

### 5. Refund Processing

**Function:** `issue_refund(customer_id: int, order_id: int) -> bool`

Processes a refund for a specific order. In a production environment, this would trigger payment gateway logic and update financial records. For this demo, it simply logs the refund to the console.

```python
def issue_refund(customer_id: int, order_id: int) -> bool:
    """
    Issues a refund for an order.
    """
    # in reality, this would be stored in some database
    print(f"Refund issued for order {order_id} for customer {customer_id}")
    return True
```

### 6. Feedback Collection

**Function:** `share_feedback(customer_id: int, feedback: str) -> str`

Allows customers to submit feedback about their experience. In production, this would be stored in a database for analysis. For this demo, it prints the feedback to the console.

```python
def share_feedback(customer_id: int, feedback: str) -> str:
    """
    Allows a customer to share feedback.
    """
    # in reality, this would be stored in some database
    print(f"Feedback received from customer {customer_id}: {feedback}")
    return "Thank you for your feedback!"
```

---

## Structuring the Agent

### Registering Available Functions

Before implementing the main agent logic, we need to declare all available functions in a dictionary. This allows the agent to dynamically call the appropriate function based on user requests.

**Why is this necessary?** When the agent decides to use a tool, it needs a mapping between function names and their actual implementations. This dictionary serves as that bridge.

```python
available_functions = {
    "verify_customer": verify_customer,
    "get_orders": get_orders,
    "check_refund_eligibility": check_refund_eligibility,
    "issue_refund": issue_refund,
    "share_feedback": share_feedback,
}
```

### Defining Tool Schemas

Next, we define detailed schemas for each tool. These schemas describe the function name, purpose, parameters, and requirements. The AI model uses these schemas to understand when and how to call each function.

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

### Tool Execution Handler

The `execute_tool_call()` function serves as the bridge between the AI agent and our Python functions. When the agent decides to use a tool, this function:

1. Extracts the function name and arguments from the tool call
2. Looks up the corresponding function in the `available_functions` dictionary
3. Executes the function with the provided arguments
4. Returns the result as a string for the AI to process
5. Handles errors gracefully by returning descriptive error messages

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

---

## Main Function Implementation

### System Prompt Configuration

The main function orchestrates the entire agent workflow. First, we define the system prompt that establishes the agent's personality and security guardrails:

**Key Security Rules:**
- ✅ Always verify customer identity before sharing sensitive information
- ❌ Never expose data to unverified users
- ❌ Don't guess or assume customer/order information
- ⚠️ Escalate complex issues to human agents
- ✔️ Request confirmation before performing critical actions

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
        """
    }
]
```

### Conversation Loop

The main interaction loop continuously processes user inputs and manages the conversation flow. Here's how it works:

1. **User Input:** Prompts the user for their request
2. **Message Processing:** Sends the conversation history to the OpenAI model
3. **Tool Execution:** If the agent calls a tool, executes it and appends results
4. **Response Generation:** Returns the agent's response to the user
5. **Loop Control:** Limits tool calls to prevent infinite loops

**Key Features:**
- 5-cycle limit prevents infinite tool call loops
- Automatic tool execution when the agent requests it
- Seamless conversation flow with context preservation
- Clean separation between regular messages and tool calls

```python
print("Welcome to the customer service chatbot! How can we help you today? Please type 'exit' to end the conversation.")

while True:
    user_input = input("Your input: ")
    if user_input == "exit":
        break

    messages.append({"role": "user", "content": user_input})

    for _ in range(5):  # limit tool call / assistant cycles to prevent infinite loops
        response = client.responses.create(
            model='gpt-4o',
            input=messages,
            tools=tools,  # Pass the tools defined earlier
        )

        output = response.output

        for reply in output:
            messages.append(reply)

            if reply.type != "function_call":
                # Only print the message content if it's a regular message
                print(reply.content[0].text)
            else:
                # Execute the tool call
                tool_output = execute_tool_call(reply)
                # Append the tool output to the conversation history
                messages.append({
                    "type": "function_call_output",
                    "call_id": reply.call_id,
                    "output": str(tool_output),
                })
        
        # Check if the last message is a regular message (not a tool call)
        # Break out of the tool call loop and ask user for next input
        if not isinstance(messages[-1], dict) and messages[-1].type == "message":
            break
```

---

## Demo: Agent in Action

Here's a complete conversation demonstrating the agent's capabilities:

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

Your input: exit
```

---

## Key Takeaways

### What This Project Demonstrates

✅ **Multi-Tool AI Agent Architecture** - Building agents that can call multiple functions dynamically  
✅ **Security-First Design** - Implementing identity verification before sensitive operations  
✅ **Conversational Context** - Maintaining conversation state across multiple interactions  
✅ **Error Handling** - Graceful handling of invalid inputs and function errors  
✅ **Tool Call Management** - Preventing infinite loops with cycle limits  

### Production Considerations

When deploying a similar system to production, consider:

1. **Security:** Hash sensitive credentials (never store plaintext PINs)
2. **Database:** Use production-grade databases (PostgreSQL, MySQL, etc.)
3. **Logging:** Implement comprehensive audit trails for all operations
4. **Testing:** Add extensive unit tests and integration tests
5. **Monitoring:** Track agent performance and error rates
6. **Escalation:** Build robust human handoff workflows
7. **Rate Limiting:** Prevent abuse with API rate limits
8. **Data Privacy:** Comply with GDPR, CCPA, and other regulations

### Future Enhancements

- 🔄 Add more sophisticated refund rules (product type, reason codes)
- 📧 Integrate email notifications for refunds and confirmations
- 📊 Implement analytics dashboard for agent performance
- 🔐 Add multi-factor authentication for high-value operations
- 🤖 Expand tool library (order tracking, product recommendations, etc.)
- 💬 Support multi-language conversations
- 🎯 Implement sentiment analysis for escalation triggers

---

**Source Code:** [View on GitHub](https://github.com/vvduth/quicksight-01/tree/main/code/multitools-agent)

```