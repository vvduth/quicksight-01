Sure! Here's a concise version suitable for an interview:
### Can you explain the four core principles of Object-Oriented Programming—encapsulation, inheritance, polymorphism, and abstraction—and how they are implemented in Java? Feel free to give examples if it helps.
The four core OOP principles in Java are:

1. **Encapsulation** – Bundling data and methods, using private fields with public getters/setters to control access.  
2. **Inheritance** – Subclasses extend superclasses using `extends` to reuse and override behavior.  
3. **Polymorphism** – Objects can take multiple forms; achieved via method overriding and interface implementation.  
4. **Abstraction** – Hiding complexity using abstract classes and interfaces, exposing only essential features.

For example, encapsulation protects data, inheritance promotes code reuse, polymorphism allows flexible method calls, and abstraction simplifies complex systems. These principles make Java code modular, maintainable, and scalable.

### Q: Can you explain what happens in memory when you create an array of objects in Java?

Sure. When you create an array of objects in Java, here's what happens in memory:

1. **Array Object Creation**:  
   - The array itself is an object created on the **heap**.  
   - It stores **references** (memory addresses) to the actual object instances, not the objects themselves.  
   - Example: `Book[] books = new Book[5];` creates an array object with 5 slots, each initialized to `null`.

2. **Object Creation**:  
   - Each `Book` object is created separately on the heap when you instantiate them (e.g., `books[0] = new Book("Title", "Author");`).  
   - The array holds **references** to these objects, not the objects directly.

3. **Memory Layout**:  
   - The array object resides on the heap.  
   - Each element in the array is a **reference** (like a pointer) to a `Book` object also on the heap.  
   - If no object is assigned, the reference is `null`.

4. **Garbage Collection**:  
   - If you set `books[0] = null`, the reference is removed, but the `Book` object remains until garbage collection.

**Key Point**: The array holds **references**, not the objects. This means you can have multiple references to the same object, and changes to the object affect all references.

Example:
```java
Book[] books = new Book[3];        // Array on heap, all null
books[0] = new Book("Java", "Harsh");  // Object on heap, reference stored
```

This structure allows efficient memory use and flexible object handling.

### Q: If you have an array of objects in Java, and you modify one of the objects in the array, will it affect other references to that same object? Why or why not?
Yes, modifying an object in the array **will affect all references to that same object**, because arrays of objects store **references**, not copies.

Here’s what happens:

- If two or more array elements (or other variables) hold references to the **same object**, changes made through any reference will be visible through all references.
- This is because the array stores **pointers** to the actual object on the heap.

**Example**:
```java
Book[] books = new Book[2];
books[0] = new Book("Java", "Harsh");
books[1] = books[0]; // Both references point to the same object

books[0].setTitle("Advanced Java"); // Modifies the object
System.out.println(books[1].getTitle()); // Output: "Advanced Java"
```

**Why?**  
The `Book` object is stored on the heap. Both `books[0]` and `books[1]` hold the same memory address. So any change to the object (e.g., modifying title) affects all references.

**Key Takeaway**:  
Modifying an object via one reference affects all other references to that same object — a direct result of Java’s **pass-by-reference** behavior for objects.