# Advanced Java Concepts

### Abstract Keyword
*   Used to declare a class or method as abstract.
*   **Abstract Class:** Cannot be instantiated directly. Serves as a base for subclasses. Can contain both abstract and concrete methods.
*   **Abstract Method:** Has no body and must be implemented by subclasses. Must belong to an abstract class.

```java
abstract class Car {
    public abstract void drive();

    public void playMusic() {
        System.out.println("Playing music in the car");
    }
}

class GWagon extends Car {
    public void drive() {
        System.out.println("Driving the G-Wagon");
    }
}
```

### Inner Classes
*   A class defined within another class.
*   Can access members (including private) of the outer class.
*   Used for logical grouping, encapsulation, and code organization.
*   **Types:** Non-static inner class, static nested class.
*   **Usage:** Non-static inner classes require an instance of the outer class.

```java
class A {
    int age;
    public void show() {
        System.out.println("Outer class method");
    }

    class B {
        public void config() {
            System.out.println("Inner class method, accessing outer class age: " + age);
            show();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        A outer = new A();
        outer.age = 30;
        A.B inner = outer.new B();
        inner.config();
    }
}
```

### Anonymous Inner Class
*   An inner class without a name, instantiated at definition.
*   Useful for quick implementation of interfaces or abstract classes without creating a separate file or named class.

```java
class A {
    public void display() {
        System.out.println("Display method in class A");
    }
}

class B extends A {
    public void display() {
        System.out.println("Display method in class B");
    }
}

public class Main {
    public static void main(String[] args) {
        A obj = new B();
        obj.display(); // Output: Display method in class B

        A obj2 = new A() {
            // Anonymous inner class extending A, overriding display method
            public void display() {
                System.out.println("Display method in anonymous inner class");
            }
        };
        obj2.display(); // Output: Display method in anonymous inner class
    }
}
```

### Anonymous Inner Class with Abstract Class
*   Can extend an abstract class to provide implementations for abstract methods on the fly.

```java
abstract class A {
    public abstract void show();
}

class B extends A {
    public void show() {
        System.out.println("Show method in class B");
    }
}

public class Main {
    public static void main(String[] args) {
        A obj = new B();
        obj.show(); // Output: Show method in class B

        A obj2 = new A() {
            // Anonymous inner class extending abstract class A
            public void show() {
                System.out.println("Show method in anonymous inner class");
            }
        };
        obj2.show(); // Output: Show method in anonymous inner class
    }
}
```

### Interfaces
*   A reference type defining a contract for implementing classes.
*   Contains abstract methods, default methods, static methods, and constants (`static final`).
*   **Characteristics:**
    *   Methods are `public abstract` by default (except default/static).
    *   Fields are `public static final` by default.
*   **Keywords:**
    *   `implements`: Used by a class to implement an interface.
    *   `extends`: Used by an interface to inherit from another interface.

```java
interface A {
    int age = 44; // static and final by default
    void display(); // abstract method
    void config(); // abstract method
}

interface C {
    void run();
}

interface Y extends C {
}

class B implements A, C {
    public void display() {
        System.out.println("Display method in class B");
    }

    public void config() {
        System.out.println("Config method in class B");
    }

    public void run() {
        System.out.println("Run method in class B");
    }
}

public class Main {
    public static void main(String[] args) {
        // A obj0 = new A(); // Error: cannot instantiate interface
        A obj = new B();
        obj.display(); // Output: Display method in class B
        obj.config();  // Output: Config method in class B

        System.out.println("Age from interface A: " + A.age);
    }
}
```

### Enums
*   Represents a fixed set of constants.
*   Provides type safety.
*   Can have constructors, methods, and fields.

```java
enum Status {
    Running, Failed, Pending, Completed;
}

public class Main {
    public static void main(String[] args) {
        Status currentStatus = Status.Running;

        switch (currentStatus) {
            case Running:
                System.out.println("The process is running.");
                break;
            case Failed:
                System.out.println("The process has failed.");
                break;
            case Pending:
                System.out.println("The process is pending.");
                break;
            case Completed:
                System.out.println("The process is completed.");
                break;
        }
    }
}
```

### Enum Class with Fields
*   Enums can store additional data and behavior.

```java
enum Laptop {
    MacBookPro(2000), DellXPS(1500), HPEnvy(1200);

    private int price;

    private Laptop(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}

public class Main {
    public static void main(String[] args) {
        for (Laptop laptop : Laptop.values()) {
            System.out.println(laptop + " costs $" + laptop.getPrice());
        }
    }
}
```

### Annotations
*   Metadata providing information about the program.
*   Not part of the program execution logic itself but used by compiler/runtime.
*   **Examples:** `@Override`, `@Deprecated`, `@SuppressWarnings`.

### Types of Interfaces
*   **Normal Interface:** Multiple abstract methods.
*   **Functional Interface:** Exactly one abstract method (basis for Lambda Expressions).
*   **Marker Interface:** No methods or fields (e.g., `Serializable`, `Cloneable`).

### Lambda Expressions
*   Concise way to represent an anonymous function.
*   Primarily used to implement Functional Interfaces.

```java
@FunctionalInterface
interface A {
    void display(String message);
}

public class Main {
    public static void main(String[] args) {
        // Using a lambda expression
        A obj = (message) -> System.out.println("Message: " + message);

        obj.display("Hello, Lambda Expressions!");
    }
}
```

*   **Return Values:** If the abstract method has a return type, the lambda must return a value.

### Exceptions
*   Events disrupting the normal flow of execution.
*   **Types of Errors:**
    *   **Logical Errors:** Incorrect output.
    *   **Compile-time Errors:** Syntax errors.
    *   **Runtime Errors:** Exceptions occurred during execution.
*   Important to handle to prevent crashes.

### Exception Handling (Try-Catch)
*   `try`: Block containing code that may throw an exception.
*   `catch`: Block to handle specific exceptions.
*   Multiple catch blocks can be used for different exception types.

### Exception Hierarchy
*   **Throwable**
    *   **Exception**
        *   **Checked Exceptions:** Checked at compile-time (e.g., `IOException`).
        *   **Unchecked Exceptions:** Runtime exceptions (e.g., `NullPointerException`).
    *   **Error**
        *   Serious problems (e.g., `OutOfMemoryError`) that typically shouldn't be caught.

### Ducking Exceptions (throws)
*   The `throws` keyword in a method declaration indicates the method may throw specific exceptions, delegating handling to the caller.

### User Input/Output
*   **Scanner:** Commonly used for console input.
*   **BufferedReader:** Efficient for reading text from streams/files.

### Threads
*   **Thread:** A lightweight process representing a separate path of execution.
*   **Multithreading:** Running multiple tasks concurrently to utilize resources better.
*   **Implementation:** Extend `Thread` class or implement `Runnable` interface.
*   **Scheduler:** The OS scheduler allocates CPU time to threads.

### Thread Priority and Sleep
*   **Priority:** `MIN_PRIORITY` (1) to `MAX_PRIORITY` (10), default is `NORM_PRIORITY` (5).
*   **Sleep:** Pauses execution for a specified duration.

### Runnable vs Thread
*   **Thread Class:** Built-in class representing a thread. To use, extend `Thread` and override `run()`.
*   **Runnable Interface:** Functional interface with a `run()` method. To use, implement `Runnable`, then pass the instance to a `Thread` constructor.

### Race Condition
*   Occurs when multiple threads access shared data concurrently and the outcome depends on timing.
*   Can lead to unpredictable bugs.
*   **Solution:** Make methods thread-safe (synchronized).

### Thread States
1.  **New:** Created but not started.
2.  **Runnable:** Ready to run, waiting for CPU.
3.  **Running:** Currently executing.
4.  **Blocked:** Waiting for a monitor lock.
5.  **Waiting:** Waiting indefinitely for another thread.
6.  **Timed Waiting:** Waiting for a specified time.
7.  **Terminated:** Execution completed.
