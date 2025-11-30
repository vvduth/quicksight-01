When building more complex AI workflows or

agents, it's quite common that you're not just dealing with

plain text, that you want the AI to not just

generate plain text. And thus far in all the previous

examples, we did only deal with plain text.

Our posts were text, the HTML content was

text. Everything was just text in the end.

But it is quite common that you actually wanna work with

structured output, that you wanna work with data that

must have a certain shape, and you want AI models

to generate data in that shape. That's why the

OpenAI API and many other APIs,

including Ollama, for running models locally,

offer you structured outputs as a special

output mode. Now, when using

OpenAI's API, it depends on whether you're

using Python and JavaScript, in which case you got

a simplified way of defining your target data structure,

or not. And I'll start with the more complex

approach, which you can, of course, also use when you are using Python or

JavaScript, and then thereafter, I'll show you the simpler approach.

But I'm starting with the more complex one so that you see what's happening under the

hood and so that you're also able to use this knowledge if you're

working with other programming languages which do not have the same

support. Here, I'm in a brand new AI

workflow and you find this updated main.py file

attached. And this workflow is all about accepting

some user input. So the idea here is that you run this

script and in the command where you execute it, you

provide a path to some file or folder.

Specifically, to a folder that contains PDF

documents, or to a single PDF document.

## eork flow expain

in main():
if len(sys.argv) < 2:
        print("Usage: python main.py /path/to/file_or_folder")
        return

    path = sys.argv[1]
    pdf_files = []

    if not os.path.exists(path):
        print(f"Error: The path '{path}' does not exist.")
        return

    if os.path.isfile(path):
        if path.lower().endswith(".pdf"):
            pdf_files.append(path)
        else:
            print(f"Error: The file '{path}' is not a PDF file.")
            return
    elif os.path.isdir(path):
        for filename in os.listdir(path):
            if filename.lower().endswith(".pdf"):
                pdf_files.append(os.path.join(path, filename))


* accetp user input (file/folder path)
* workfflow will chack if the path exist if we point at a file or folder
* if folder, we will load all pdf files in the folder
* if file  , we will load that single pdf file
=> workflow will get the etxt form the fail with plain old broing python code using pypdf:


def get_pdf_content(pdf_path: str) -> str:
    with open(pdf_path, "rb") as f:
        reader = PdfReader(f)
        text = ""
        for page in reader.pages:
            text += page.extract_text()
    return text

==> good idea to use python instead of using ai tools, save some cost and time

after the send the text to the ai model to get a summary of the content
invoice_details = extract_invoice_details(pdf_content)

I also instruct the model to return the result as a JSON without extra text or explanation
also , in the request body you can prives "text" parameter to desribe the shape the data you want the ai to generate

then we insert it to daabase ::

insert_invoice_data(conn, invoice_details)

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
        invoice_data.get("customer", {}).get("name"),
        invoice_data.get("customer", {}).get("address"),
        invoice_data.get("customer", {}).get("taxId"),
        invoice_data.get("invoiceNumber"),
        invoice_data.get("date"),
        invoice_data.get("totalAmount"),
        invoice_data.get("tax")
    ))
    conn.commit()
So overall, this workflow allows you to process PDF invoices from a specified file or folder, extract structured invoice details using an AI model, and store those details in a SQLite database for further use. This is a practical example of how to leverage AI for document processing and data extraction in a real-world scenario. 

## ollama
bsdide using mdeol from open ai and googlle, you can run 