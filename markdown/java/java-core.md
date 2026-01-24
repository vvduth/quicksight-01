# Java Core Concepts

## How Java Works
*   **JVM (Java Virtual Machine):** Essential for running Java applications.
*   **Compilation Process:** Java code (`.java`) -> `javac` Compiler -> Bytecode (`.class`) -> JVM -> Machine Code.
*   **Main Method:** The JVM requires a specific method signature to start the program: `public static void main(String[] args)`.
*   **JRE (Java Runtime Environment):** Contains the JVM.
*   **JDK (Java Development Kit):** Includes the JRE plus development tools (compiler, debugger, etc.).

## Primitives in Java
*   **Byte:** 1 byte (8 bits).
*   **Integer Types:**
    *   `byte`: 1 byte
    *   `short`: 2 bytes
    *   `int`: 4 bytes
    *   `long`: 8 bytes
*   **Floating Point Types:**
    *   `float`: 4 bytes
    *   `double`: 8 bytes
*   **Char:** 2 bytes (Unicode characters).
*   **Boolean:** 1 bit (true or false represents the logic, though size is implementation dependent).

### Literals
*   **Integer Literals:**
    *   Decimal: `123`
    *   Octal: `0173`
    *   Hexadecimal: `0x7B`
    *   Binary: `0b1111011`

## Type Conversion and Casting
*   **Widening (Implicit):** `byte` -> `short` -> `int` -> `long` -> `float` -> `double`.
*   **Casting (Explicit):** `double` -> `float` -> `long` -> `int` -> `short` -> `byte`.
*   **Char Conversion:** Can be converted to `int` and vice versa.
*   **Type Promotion:** In expressions, smaller types are promoted to larger types.
*   **Sign Bit:** The high-order bit represents the sign (0 = positive, 1 = negative).

## Arithmetic Operators
*   `+`, `-`, `*`, `/`, `%` (modulus).
*   **Integer Division:** Truncates the decimal part.
*   **Increment/Decrement:** `++` and `--`.

## Relational Operators
*   `==`, `!=`, `>`, `<`, `>=`, `<=`.

## Logical Operators
*   `&&` (Logical AND), `||` (Logical OR), `!` (Logical NOT).

## Conditional Statements
*   `if`, `else if`, `else`.
*   `switch`: Works with `int`, `char`, `String`, `enum`.
*   **Ternary Operator:** `condition ? expr1 : expr2`.

### Pre-increment vs. Post-increment
*   `int i = 5; ++i;` // Increments `i`, returns 6.
*   `int i = 5; i++;` // Returns 5, then increments `i` to 6.

## Object-Oriented Programming (OOP)
*   **Class:** A blueprint for objects.
*   **Methods:** Functions defined in a class.

### Method Overloading
*   Same method name with different parameters.

### Stack and Heap Memory
*   **Stack:** Stores primitive types and references to objects; follows LIFO (Last In, First Out).
*   **Heap:** Stores objects; managed by the garbage collector.

### Jagged Arrays
*   Arrays of arrays with different lengths.

### For-Each Loop
*   Used for easier reading and writing, and is less error-prone when iterating over collections or arrays.

### String
*   **Immutable:** Cannot be changed after creation.
*   **String Constant Pool:** Special memory area in the heap for storing string literals.
*   **Type:** Object, not a primitive type.

### StringBuffer
*   **Mutable:** Can be changed after creation.
*   **Modification:** Offers methods to change the content without creating a new object.
*   **Usage:** Use `.toString()` to convert to a String.

### StringBuilder
*   Similar to `StringBuffer` but not synchronized (not thread-safe).

### Encapsulation
*   Uses private fields with public getters and setters.
*   Keeps data secure and prevents direct access.

### Setters and Getters
*   **Setters:** Set the value of a private field.
*   **Getters:** Retrieve the value of a private field.

### `this` Keyword
*   Refers to the current object instance.
*   Used to differentiate between instance variables and parameters with the same name.

### Constructor
*   Special method to initialize objects.
*   Same name as the class.
*   No return type.
*   **Default Constructor:** No-arg constructor provided by the compiler if no constructor is defined.
*   **Parameterized Constructor:** Constructor with arguments.

### Static Variables
*   Shared among all instances of a class.
*   Belong to the class, not to any specific object.
*   Accessed using the class name or object reference.

### Static Methods
*   Belong to the class, not to any specific object.
*   Can be called without creating an instance.
*   **Restrictions:** Cannot access instance variables or methods directly; can only access static variables and methods.

### Static Blocks
*   Used to initialize static variables.
*   Executed when the class is loaded.
*   Only one static block per class.
*   Executed before the `main` method.

### Anonymous Objects
*   Objects created without a reference variable (e.g., `new Human();`).
*   Example: `Human h = new Human();` creates a reference `h` in the stack pointing to the object in the heap.
*   Without a reference, the object cannot be accessed later. Useful for single-use objects.

### Inheritance
*   Mechanism to create a new class from an existing class using the `extends` keyword.

### Single and Multilevel Inheritance
*   **Single Inheritance:** A class inherits from one superclass.
*   **Multilevel Inheritance:** A class inherits from a superclass, which inherits from another superclass.

### Multiple Inheritance
*   Not supported in Java with classes to avoid ambiguity.

### `this()` and `super()`
*   `this()`: Calls the current class constructor (used for constructor chaining).
*   `super()`: Calls the superclass constructor.
*   **Default Behavior:** All constructors implicitly call `super()` first. Implicit calls invoke the no-arg constructor. Use `super(params)` explicitly for parameterized constructors.
*   All classes in Java extend the `Object` class by default.

### Method Overriding
*   A subclass provides a specific implementation of a method already defined in its superclass.

```java
class A {
    public void display() {
        System.out.println("Class A");
    }

    public void config() {
        System.out.println("Config A");
    }
}

class B extends A {
    @Override
    public void display() {
        System.out.println("Class B");
    }
}
```

### Packages
*   Used to group related classes and interfaces.
*   Helps avoid name conflicts and organize code.
*   **Creation:** Use the `package` keyword at the top of the file.
*   **Usage:** Use the `import` keyword.

### Access Modifiers
*   **Public:** Accessible from anywhere.
*   **Protected:** Accessible within the same package and subclasses.
*   **Default (no modifier):** Accessible within the same package.
*   **Private:** Accessible only within the same class.

### Polymorphism
*   Ability of an object to take on many forms.
*   **Compile-time:** Method overloading (e.g., `add(int a, int b)` vs. `add(double a, double b)`).
*   **Runtime:** Method overriding (dynamic method dispatch).

### Dynamic Method Dispatch
*   Resolving a method call at runtime rather than compile time.
*   Achieved through method overriding.
*   A base class reference can refer to a derived class object.

```java
A obj = new B(); // Base class reference, derived class object
obj.display();   // Calls B's display() method
```

### `final` Keyword
*   **Final Variable:** Constant; value cannot be changed once assigned.
*   **Final Method:** Cannot be overridden by subclasses.
*   **Final Class:** Cannot be subclassed.

```java
final class A {
    // class definition
}

// class B extends A { // Error: cannot subclass final class
// }

class C {
    final void display() {
        System.out.println("Final method");
    }
}

class D extends C {
    // void display() { // Error: cannot override final method
    //     System.out.println("Override final method");
    // }
}
```

### Upcasting and Downcasting
*   **Upcasting:** Converting a subclass reference to a superclass reference (always safe).
*   **Downcasting:** Converting a superclass reference to a subclass reference (may throw `ClassCastException`).

```java
class A {
    void show() {
        System.out.println("Class A");
    }
}

class B extends A {
    void show() {
        System.out.println("Class B");
    }
}

A a = new B(); // Upcasting
B b = (B) a;   // Downcasting
```

### Wrapper Classes
*   Provide a way to use primitive data types as objects.
*   Mapping:
    *   `byte` -> `Byte`
    *   `short` -> `Short`
    *   `int` -> `Integer`
    *   `long` -> `Long`
    *   `float` -> `Float`
    *   `double` -> `Double`
    *   `char` -> `Character`
    *   `boolean` -> `Boolean`
