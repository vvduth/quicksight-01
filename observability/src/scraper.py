import os
import requests
import re
from markdownify import markdownify as md
from urllib.parse import urlparse

BASE_URL = "https://support.optisigns.com/api/v2/help_center/en-us/articles.json"
OUTPUT_DIR = "data"

def clean_filename(title):
    # Create a slug from the title
    slug = re.sub(r'[^a-z0-9]+', '-', title.lower()).strip('-')
    return slug

def fetch_articles(limit=30):
    articles = []
    url = BASE_URL
    while url and len(articles) < limit:
        print(f"Fetching {url}...")
        response = requests.get(url)
        if response.status_code != 200:
            print(f"Failed to fetch articles: {response.status_code}")
            break
        
        data = response.json()
        items = data.get('articles', [])
        articles.extend(items)
        
        url = data.get('next_page')
        
    return articles[:limit] # Return at least limit, or all if less

def save_article(article):
    title = article.get('title', 'Untitled')
    body = article.get('body', '')
    
    if not body:
        return
        
    # Convert HTML to Markdown
    # strip=['a'] would strip links, we want to keep them.
    # We want to remove potential script tags or styles if any, though Zendesk body is usually clean.
    markdown_content = md(body, heading_style="ATX")
    
    # Add title header
    full_content = f"# {title}\n\nURL: {article.get('html_url')}\n\n{markdown_content}"
    
    filename = clean_filename(title)
    filepath = os.path.join(OUTPUT_DIR, f"{filename}.md")
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(full_content)
    print(f"Saved: {filepath}")

def run_scraper():
    if not os.path.exists(OUTPUT_DIR):
        os.makedirs(OUTPUT_DIR)
        
    articles = fetch_articles(limit=50) # Fetch a bit more to ensure we have enough good ones
    print(f"Found {len(articles)} articles.")
    
    count = 0
    for article in articles:
        # Skip drafts or internal if any (API usually returns only published for public endpoint)
        if article.get('draft', False):
            continue
            
        save_article(article)
        count += 1
        
    print(f"Successfully processed {count} articles.")

if __name__ == "__main__":
    run_scraper()
