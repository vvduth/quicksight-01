# Why RAG?

*   LLMs can be right, but they can also be dead wrong. Confidently wrong.
*   **Undesirable behaviors of LLMs:**
    *   Hallucination (making things up).
    *   Outdated knowledge (stuck in the past).
    *   Lack of domain-specific knowledge.

## What is RAG?

*   It is an AI framework that combines LLMs with external knowledge sources to improve the accuracy and relevance of generated responses.
*   **Benefit:** Better output without re-training the LLM. It's like giving the student an open-book test.
*   **2 Main Components:**
    *   **Retriever:** Fetches relevant documents from a knowledge base based on the input query.
    *   **Generator:** Uses the retrieved documents along with the input query to generate a more informed response.

### Step 1: Text Embedding
*   The question or prompt is embedded into a high-dimensional vector space using a **question encoder model**.
*   Knowledge base documents are also pre-embedded using a **document encoder model** and stored in a vector database.

### Step 2: Retrieval
*   The system performs a **similarity search** in the vector database to find the most relevant documents to the embedded query.
*   **Augmented Query Creation:** The system creates an augmented query by combining the original query with the retrieved documents.

### Step 3: Generation
*   The augmented query is passed to a language model to generate a response.

### Understanding Prompt Encoding
*   Encoding involves converting text into a high-dimensional vector using an encoder model.
*   The system typically takes the average to create a single vector representation for the whole prompt.

### Converting Contextual Data to Vectors
*   **Chunking:** The document is broken down into smaller chunks of text for better granularity.
*   **Embedding & Indexing:** Each text chunk is encoded into a high-dimensional vector using a document encoder model.
*   **Aggregation:** The system takes the average to create a single vector representation for the whole chunk.
*   **Storage:** Vectors are indexed in the knowledge base (vector database) with the chunk ID as the key. 

### Searching for Relevant Context
*   The system compares the vector representation of the prompt with the text chunks in the knowledge base using similarity metrics (like **cosine similarity**).
*   **Example:**
    *   **Question:** "What is your mobile policy?"
    *   **Question Vector:** `[0.35, 0.08, 0.16]`
    *   The system calculates the distance between the question vector and each text chunk vector.
    *   **Result:** It selects the text chunks with the highest similarity scores (e.g., top 3) as relevant context.