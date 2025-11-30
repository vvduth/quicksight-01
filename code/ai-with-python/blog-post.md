# Building a Smart Content Pipeline: From Web Scraping to Social Media Posts

I recently built a Python tool that takes any website URL and automatically generates an optimized social media post. But here's the interesting part: instead of throwing everything at one AI model and hoping for the best, I created a multi-step workflow that uses different AI models for different tasks.

Let me walk you through how it works and the techniques I used to get better results.

## The Big Picture

The workflow has four stages:

```
Website URL → Fetch HTML → Extract Content → Summarize → Generate Post
```

Each stage does one thing well, and the output of one stage feeds into the next. This approach is better than asking one AI model to "read this website and make a tweet" because:

1. We can use cheaper, smaller models for simple tasks
2. We can use powerful models only where we need them
3. Each step is easier to debug and improve
4. The final output is more consistent

## Stage 1: Fetching the Website

This is straightforward - just grab the HTML:

```python
def get_website_html(url: str) -> str:
    try:
        response = requests.get(url)
        response.raise_for_status()
        return response.text
    except requests.RequestException as e:
        print(f"Error fetching the URL {url}: {e}")
        return ""
```

Nothing fancy here. We use the `requests` library, handle errors gracefully, and return an empty string if something goes wrong.

## Stage 2: Extract the Core Content

Now we have HTML with navigation bars, footers, ads, and scripts. We only want the main content. This is where the first AI model comes in:

```python
def extract_core_website_content(html: str) -> str:
    response = client.responses.create(
        model="gpt-4o-mini",
        input=f"""
            You are an expert web content extractor. Your task is to extract the core content from a given HTML page.
            The core content should be the main text, excluding navigation, footers, and other non-essential elements like scripts etc.

            Here is the HTML content:
            <html>
            {html}
            </html>

            Please extract the core content and return it as plain text.
        """
    )
    return response.output_text
```

### Technique #1: Use Smaller Models for Simple Tasks

Notice I'm using `gpt-4o-mini` here, not the full `gpt-4o`. Why? Because extracting content is a straightforward task. The smaller model:
- Costs less (important if you're processing many URLs)
- Runs faster
- Still does the job perfectly well

This is about matching the model to the task. You don't need a sledgehammer to crack a nut.

### Technique #2: Structure Your Prompts with XML-like Tags

See those `<html>` tags? They're not required by the API, but they help the model understand structure:

```python
<html>
{html}
</html>
```

This creates clear boundaries. The model knows exactly where the input starts and ends. It's a simple trick that makes prompts more reliable.

## Stage 3: Summarize the Content

Now we have clean text, but it might be long. Let's condense it:

```python
def summarize_content(content: str) -> str:
    response = client.responses.create(
        model="gpt-4o-mini",
        input=f"""
            You are an expert summarizer. Your task is to summarize the provided content into a concise and clear summary.

            Here is the content to summarize:
            <content>
            {content}
            </content>

            Please provide a brief summary of the main points in the content. Prefer bullet points and avoid unncessary explanations.
        """
    )
    return response.output_text
```

Again, `gpt-4o-mini` is perfect here. Summarization is a well-defined task. I'm also giving specific formatting instructions: "Prefer bullet points and avoid unnecessary explanations."

This is better than hoping the model figures out what you want. Be explicit.

## Stage 4: Generate the Social Media Post

This is where it gets interesting. Creating engaging social media content is hard. You need the right tone, structure, and style. Here's where I use the bigger model and few-shot learning:

```python
def generate_x_post(summary: str) -> str:
    # Load examples from a JSON file
    with open("post-examples.json", "r", encoding="utf-8") as f:
        examples = json.load(f)

    # Build examples string dynamically
    examples_str = ""
    for i, example in enumerate(examples, 1):
        examples_str += f"""
        <example-{i}>
            <topic>
            {example['topic']}
            </topic>

            <generated-post>
            {example['post']}
            </generated-post>
        </example-{i}>
        """

    prompt = f"""
        You are an expert social media manager, and you excel at crafting viral and highly engaging posts for X (formerly Twitter).

        Your task is to generate a post based on a short text summary.
        Your post must be concise and impactful.
        Avoid using hashtags and lots of emojis (a few emojis are okay, but not too many).

        Keep the post short and focused, structure it in a clean, readable way, using line breaks and empty lines to enhance readability.

        Here's the text summary which you should use to generate the post:
        <summary>
        {summary}
        </summary>

        Here are some examples of topics and generated posts:
        <examples>
            {examples_str}
        </examples>

        Please use the tone, language, structure, and style of the examples provided above to generate a post that is engaging and relevant to the topic provided by the user.
        Don't use the content from the examples!
    """
    
    response = client.responses.create(
        model="gpt-4o",
        input=prompt
    )
    return response.output_text
```

### Technique #3: Use Larger Models for Complex Creative Tasks

Now I'm using `gpt-4o` (not mini) because:
- Creating engaging content requires understanding nuance
- The tone and style need to match examples
- This is the final output users see - quality matters most here

### Technique #4: Few-Shot Learning with Examples

This is the key technique. Instead of just saying "write a good post," I'm showing the model what "good" looks like:

```json
[
  {
    "topic": "Fighting through every round—training Muay Thai while undergoing chemo for Hodgkin lymphoma. 💪",
    "post": "Fighting through every round—training Muay Thai while undergoing chemo for Hodgkin lymphoma. 💪🏼✨\nYour spirit is stronger than any diagnosis. Keep pushing your limits and inspiring us all! 🥊\n#MuayThai #CancerWarrior #HodgkinLymphoma #StayStrong"
  }
]
```

The model sees:
- How long posts should be
- What tone to use (encouraging, authentic)
- How to structure them (line breaks, emojis)
- When hashtags are okay

This is called "few-shot learning" - you teach by example rather than by rules.

### Technique #5: Generate Prompts Dynamically

The examples aren't hardcoded in the prompt. They're loaded from a JSON file and inserted dynamically:

```python
with open("post-examples.json", "r", encoding="utf-8") as f:
    examples = json.load(f)
```

Why does this matter?

1. **Easy to update**: Add new examples without touching code
2. **Scalable**: Want 10 examples instead of 1? Just update the JSON
3. **A/B testing**: Try different example sets to see what works best

**Important note**: Always use `encoding="utf-8"` when reading JSON files with emojis or special characters. On Windows, Python defaults to `cp1252` which can't handle these characters.

## Putting It All Together

Here's the main flow:

```python
def main():
    website_url = input("Website URL: ")
    
    # Step 1: Fetch
    html_content = get_website_html(website_url)
    if not html_content:
        return
    
    # Step 2: Extract
    core_content = extract_core_website_content(html_content)
    print("Extracted core content:")
    print(core_content)
    
    # Step 3: Summarize
    summary = summarize_content(core_content)
    print("Generated summary:")
    print(summary)
    
    # Step 4: Generate post
    x_post = generate_x_post(summary)
    print("Generated X post:")
    print(x_post)
```

Each step prints its output so you can see what's happening. This makes debugging easy - if the final post is bad, you can see if the problem is in extraction, summarization, or generation.

## Key Takeaways

1. **Break complex workflows into stages** - Don't ask one AI call to do everything
2. **Match model size to task complexity** - Use smaller models for simple tasks, save the big ones for creative work
3. **Be specific in your prompts** - Tell the model exactly what you want (format, style, length)
4. **Use few-shot learning** - Show examples of what "good" looks like
5. **Make prompts dynamic** - Load examples and content from files, don't hardcode everything
6. **Structure with XML-like tags** - Help the model understand your input structure

## Running the Code

Setup is simple:

```bash
uv sync          # Install dependencies
python main.py   # Run the tool
```

You'll need an OpenAI API key in a `.env` file:

```
OPENAI_API_KEY=your-key-here
```

The dependencies are minimal:
- `openai` - For AI model access
- `requests` - For fetching websites
- `python-dotenv` - For loading environment variables

## What I Learned

Building this taught me that good AI applications aren't about using the most powerful model for everything. They're about:

- Understanding what each part of your workflow needs
- Using the right tool for each job
- Giving models clear, structured instructions
- Teaching by example rather than by rules

The result is a tool that's fast, cost-effective, and produces consistent results. And because each stage is independent, I can improve or replace any part without rewriting everything.

That's the power of thinking in pipelines instead of single AI calls.
