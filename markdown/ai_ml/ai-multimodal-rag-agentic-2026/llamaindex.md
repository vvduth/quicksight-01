# LlamaIndex

*   It is a framework for building LLM-powered context augmentation applications.
*   **Context Augmentation:** The process of making data available to LLMs so they can generate more accurate and relevant responses. No more guessing games!
*   **Examples:** Question answering systems, chatbots (extending the basic RAG pipeline), document understanding, summarization, etc.

### RAG Review (Refresher)

1.  **Source** => Embedded Source => Vector Store => Retriever => LLM
2.  **Original Prompt** => Embedding Prompt => Retriever
3.  **Retriever** => Retrieved Text + Original Prompt => LLM => Response

## Loading Source Documents with LlamaIndex

*   You can load source documents from various formats such as `.txt`, `.pdf`, `.docx`, `.md`, `.json`, `.html`, etc.
*   **Example:** Loading a text file.

```python
from llama_index.core import Document
mydoc = Document(text="This is my document")
mydoc.dict() # Fixed typo: mydocs -> mydoc
```

The document object has several key components:
*   **id:** Unique identifier for the document.
*   **embeddings:** A placeholder for storing the document's embeddings.
*   **metadata:** A dictionary for storing additional information about the document.
*   **relationships:** A dictionary for storing relationships with other documents.
*   **text:** The actual content of the document.

## Loading Documents from Local Storage

Using `SimpleDirectoryReader`:

```python
from llama_index.core import SimpleDirectoryReader

# Load data from a directory
docs = SimpleDirectoryReader('path/to/directory').load_data()

# Load all files recursively
docs = SimpleDirectoryReader('path/to/directory', recursive=True).load_data()

# Load specific file types
docs = SimpleDirectoryReader('path/to/directory', file_types=['.txt', '.md']).load_data()
```

## Using LlamaIndex Nodes

*   **Nodes** are simple text chunks used to build the index.
*   Use `SentenceSplitter` to split documents into smaller chunks based on sentences.

```python
from llama_index.core.node_parser import SentenceSplitter

# Initialize the SentenceSplitter with desired chunk size and overlap
node_parser = SentenceSplitter(chunk_size=512, chunk_overlap=20)

# Use the get_nodes_from_documents method to split documents into nodes
nodes = node_parser.get_nodes_from_documents(docs, show_progress=False)
```

Besides `SentenceSplitter`, there are other node parsers available in LlamaIndex:
*   **SemanticTextSplitter:** Splits text based on semantic meaning.
*   **LangChain Wrapper:** A wrapper around any LangChain text splitter.
  
## Embedding Notes

*   LlamaIndex uses the `VectorStoreIndex` class to generate and store embeddings for documents.
*   Import libraries:

```python
import chromadb
from llama_index.core import VectorStoreIndex, StorageContext 
from llama_index.vector_stores.chroma import ChromaVectorStore
from llama_index.embeddings.huggingface import HuggingFaceEmbeddings

# Define the embedding model
embedding_model = HuggingFaceEmbeddings(model_name="sentence-transformers/all-MiniLM")

# Initialize the vector store and storage context
db = chromadb.PersistentClient(path="./chroma_db_test")
chroma_collection = db.get_or_create_collection(name="my_collection")
vector_store = ChromaVectorStore(collection=chroma_collection)

# We need storage context to manage the vector store
storage_context = StorageContext.from_defaults(vector_store=vector_store)

# Pass the nodes, embedding model, and storage context to create the VectorStoreIndex
index = VectorStoreIndex(
    nodes,
    embedding_model=embedding_model,
    storage_context=storage_context,
)
```

## Retrieval Steps

Create a retriever by calling the `as_retriever` method on the index object.

```python
retriever = index.as_retriever()
retrieved_nodes = retriever.retrieve("What is the main topic of the document?")
```

## LLM Response Synthesizer

LlamaIndex uses a response synthesizer to combine prompt augmentation, LLM querying, and response generation.

```python
from llama_index.core import get_response_synthesizer

response_synthesizer = get_response_synthesizer()

response = response_synthesizer.synthesize(
    "What is the main topic of the document?",
    nodes=retrieved_nodes,
)
```

## LlamaIndex Query Engine

*   A **Query Engine** is a high-level abstraction that combines the retriever and response synthesizer into a single interface for querying the index.
*   **Tips:** You can modify a query engine by:
    *   Changing the default LLM.
    *   Defining custom prompt templates.
    *   Specifying a custom retriever.