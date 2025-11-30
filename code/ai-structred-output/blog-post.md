# Building Smart AI Workflows: From PDFs to Structured Data

If you've been playing with ChatGPT or other AI tools, you know they're great at generating text. But what if you need more than just text? What if you need data in a specific format, ready to drop into a database? That's where structured output comes in.

Let me walk you through a real project that processes invoices from PDF files and extracts structured data using AI. Along the way, I'll show you some practical techniques that make AI workflows actually useful in production.

## The Problem: PDFs Are Messy, Databases Need Structure

Say you run a business and receive dozens of PDF invoices every week. Each invoice has vendor info, customer details, amounts, dates, and tax information. You want all this data in a database for analysis, but manually typing it in? That's a waste of time.

The challenge: PDFs are just pixels and text. Databases need structured data with specific fields and types. We need AI to bridge that gap.

## The Two-Step Approach: Python First, AI Second

Here's a key insight from this project: **don't use AI for everything**. Use it where it adds value.

```python
def get_pdf_content(pdf_path: str) -> str:
    with open(pdf_path, "rb") as f:
        reader = PdfReader(f)
        text = ""
        for page in reader.pages:
            text += page.extract_text()
    return text
```

This code uses a Python library called `pypdf` to extract text from PDFs. No AI needed. It's faster, cheaper, and more reliable than asking AI to read a PDF.

**The workflow:**
1. Python extracts text from PDF (deterministic, cheap)
2. AI extracts structured data from text (intelligent, necessary)

This is what I call a **hybrid approach**: use traditional code for mechanical tasks, use AI for tasks that need understanding.

## Structured Output: Teaching AI to Speak JSON

Once we have the text, we need to tell the AI exactly what format we want. This is where OpenAI's structured output API shines.

Here's the magic part:

```python
response = requests.post(
    "https://api.openai.com/v1/responses",
    json={
        "model": "gpt-4o-mini",
        "input": prompt,
        "text": {
            "format": {
                "name": "invoice",
                "type": "json_schema",
                "schema": {
                    "type": "object",
                    "properties": {
                        "vendor": {
                            "type": "object",
                            "properties": {
                                "name": {"type": "string"},
                                "address": {"type": "string"},
                                "taxId": {"type": "string"}
                            },
                            "required": ["name", "address", "taxId"]
                        },
                        "customer": { /* similar structure */ },
                        "invoiceNumber": {"type": "string"},
                        "date": {"type": "string"},
                        "totalAmount": {"type": "number"},
                        "tax": {"type": "number"}
                    },
                    "required": ["vendor", "customer", "invoiceNumber", "date", "totalAmount", "tax"]
                },
                "strict": True
            }
        }
    }
)
```

That `"strict": True` parameter is crucial. It tells OpenAI: "Don't give me anything that doesn't match this schema." No extra commentary, no "Here's what I found..." Just pure, validated JSON.

The schema acts like a contract. You define the structure once, and every response follows it. This means your database insertion code never breaks:

```python
def insert_invoice_data(conn, invoice_data):
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO invoices (
            vendor_name, vendor_address, vendor_tax_id,
            customer_name, customer_address, customer_tax_id,
            invoice_number, "date", total_amount, tax
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ''', (
        invoice_data.get("vendor", {}).get("name"),
        invoice_data.get("vendor", {}).get("address"),
        invoice_data.get("vendor", {}).get("taxId"),
        # ... rest of the fields
    ))
    conn.commit()
```

## Multi-Step AI Workflows: The Web Content Pipeline

The second part of this project shows a more complex pattern: chaining multiple AI calls together, each building on the previous one.

**The pipeline:**
```
URL → Fetch HTML → Extract Core Content → Summarize → Generate Social Post
```

Each step refines the output:

### Step 1: Extract Core Content

```python
def extract_core_website_content(html: str) -> str:
    response = get_ai_response(
        model="gemma3:4b-it-qat",
        prompt=f"""
            You are an expert web content extractor.
            Extract the core content from this HTML, excluding navigation, footers, scripts, etc.
            
            <html>
            {html}
            </html>
        """,
        ctx=20000  # Large context window for HTML
    )
    return response
```

Notice the `ctx=20000` parameter? HTML pages can be huge. This tells the local Ollama model to allocate enough memory to handle large inputs.

### Step 2: Summarize

```python
def summarize_content(content: str) -> str:
    response = get_ai_response(
        model="gemma3:1b-it-qat",  # Smaller model
        prompt=f"""
            Summarize this content into concise bullet points.
            Avoid unnecessary explanations.
            
            <content>
            {content}
            </content>
        """
    )
    return response
```

**Key technique:** Use a smaller model (1b) for straightforward tasks like summarization. It's faster and uses less memory. Save the big models for complex work.

### Step 3: Generate Social Media Post

```python
def generate_x_post(summary: str) -> str:
    with open("post-examples.json", "r") as f:
        examples = json.load(f)
    
    examples_str = ""
    for i, example in enumerate(examples, 1):
        examples_str += f"""
        <example-{i}>
            <topic>{example['topic']}</topic>
            <generated-post>{example['post']}</generated-post>
        </example-{i}>
        """
    
    prompt = f"""
        You are an expert social media manager.
        Generate an engaging X post based on this summary.
        
        <summary>
        {summary}
        </summary>
        
        Here are examples of your style:
        <examples>
        {examples_str}
        </examples>
        
        Use the same tone and structure, but don't reuse the content.
    """
    
    response = get_ai_response(
        model="gemma3:27b-it-qat",  # Largest model for creative work
        prompt=prompt
    )
    return response
```

This demonstrates several advanced techniques:

## Technique 1: Dynamic Prompt Engineering

The prompt isn't static. It dynamically includes:
- The summary (generated in the previous step)
- Examples loaded from a JSON file
- Specific instructions about tone and style

This is **prompt composition**: building prompts programmatically based on context.

## Technique 2: Few-Shot Learning

By including examples from `post-examples.json`, we're showing the AI what "good" looks like. This is called few-shot prompting. Instead of explaining what you want, you show examples.

The AI learns:
- Your writing style
- Your formatting preferences
- Your tone

## Technique 3: Model Selection Strategy

Look at the progression:
- **4b model** for extraction (mechanical task)
- **1b model** for summarization (simple task)
- **27b model** for creative writing (complex task)

This is cost-optimization in action. The 27b model would work for summarization, but it's overkill. The 1b model is perfect and much faster.

## Technique 4: Progressive Refinement

Each step narrows the focus:

1. **Raw HTML** (10,000+ words, lots of noise)
2. **Core content** (2,000 words, relevant info)
3. **Summary** (200 words, key points)
4. **Social post** (50 words, punchy and engaging)

This is better than asking AI to go directly from HTML to a social post. Each step is simpler and more reliable.

## The Architecture That Ties It Together

The project uses two different AI approaches:

**OpenAI API** (cloud-based):
```python
requests.post(
    "https://api.openai.com/v1/responses",
    json={
        "model": "gpt-4o-mini",
        "text": {"format": {"type": "json_schema", ...}}
    }
)
```

**Ollama API** (local):
```python
requests.post(
    "http://localhost:11434/api/generate",
    json={
        "model": "gemma3:1b-it-qat",
        "prompt": prompt,
        "stream": False
    }
)
```

OpenAI is great for structured output and complex extraction. Ollama runs locally, giving you privacy and no per-request costs. The developer chose the right tool for each job.

## The Database Layer: Completing the Loop

After all the AI magic, we end up with structured data in SQLite:

```python
def setup_database():
    conn = sqlite3.connect("invoices.db")
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS invoices (
            id INTEGER PRIMARY KEY,
            vendor_name TEXT,
            vendor_address TEXT,
            vendor_tax_id TEXT,
            customer_name TEXT,
            customer_address TEXT,
            customer_tax_id TEXT,
            invoice_number TEXT,
            date TEXT,
            total_amount REAL,
            tax REAL
        )
    ''')
    conn.commit()
    return conn
```

Now you can run SQL queries:
```sql
SELECT vendor_name, SUM(total_amount) 
FROM invoices 
GROUP BY vendor_name;
```

The AI workflow ends with actionable data, not just text sitting in a chat window.

## Key Takeaways

If you're building AI workflows, here's what this project teaches us:

1. **Hybrid approach wins**: Use traditional code where it's faster/cheaper, use AI where understanding is needed

2. **Structured output is essential**: For production systems, you need predictable data formats, not free-form text

3. **Chain AI calls thoughtfully**: Break complex tasks into steps, each with a clear input and output

4. **Match model size to task complexity**: Small models for simple tasks, big models for creative/complex work

5. **Compose prompts dynamically**: Build prompts from examples, context, and structured data

6. **Few-shot learning is powerful**: Show the AI examples of what you want instead of just describing it

7. **Progressive refinement beats one-shot**: Going from "messy input → refined output 1 → refined output 2 → final output" is more reliable than trying to do it all at once

## The Bottom Line

AI workflows aren't about having a conversation with ChatGPT. They're about building systems that:
- Take real inputs (PDFs, websites, files)
- Process them through multiple steps
- Produce structured, usable outputs (databases, APIs, reports)

This invoice processor isn't just a demo. It's a production-ready pattern you can adapt for:
- Processing receipts
- Extracting data from contracts
- Parsing resumes
- Analyzing customer feedback
- Converting documents to structured data

The code is straightforward. The techniques are practical. And the results are immediately useful.

That's how you build AI workflows that actually work.

---

**Tech Stack Used:**
- Python 3.11
- OpenAI API (gpt-4o-mini)
- Ollama (gemma3 models: 1b, 4b, 27b)
- pypdf for PDF text extraction
- SQLite for data storage
- requests for HTTP calls

**Setup:**
```bash
# Install dependencies
uv sync

# Set API key
echo "OPENAI_API_KEY=your-key-here" > .env

# Run invoice processor
python main.py /path/to/invoices/

# Run web content processor
python open.py
```

The full code is available here: https://github.com/vvduth/ai-basic/tree/main/02-project
