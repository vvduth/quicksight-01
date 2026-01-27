### python refresher
Here are some key concepts and examples to refresh your Python knowledge:
#### Variables and Data Types
```python
# Variables
x = 10          # Integer
y = 3.14        # Float
name = "Alice"  # String
is_active = True # Boolean
```

### the "in" keyword
* the in keyword is used to check if a value exists within an iterable (like a list, tuple, set, or string).
```python
# Using "in" with strings
sentence = "Hello, welcome to the world of Python."
if "Python" in sentence:
    print("Found Python in the sentence!")
else:
    print("Python not found in the sentence.")
```
#### Control Structures
```python
# If-Else Statement
if x > 5:
    print("x is greater than 5")
else:
    print("x is 5 or less")

## if statement with the "in" keyword
fruits = ['apple', 'banana', 'cherry']
if 'banana' in fruits:
    print("Banana is in the list!")
else:
    print("Banana is not in the list.")
```
```python
# For Loop
for i in range(5):
    print(i)
```
```python   
# While Loop
count = 0
while count < 5:
    print(count)
    count += 1
```
### tuple, list, set, dictionary
```python
# Tuple (immutable)
my_tuple = (1, 2, 3)
print(my_tuple[0])  # Output: 1
```
```python
# List (mutable)
my_list = [1, 2, 3]
my_list.append(4)
print(my_list)  # Output: [1, 2, 3, 4]
```
```python
# Set (unordered, unique elements)
my_set = {1, 2, 3}
my_set.add(4)
print(my_set)  # Output: {1, 2, 3, 4}
```
```python
# Dictionary (key-value pairs)
my_dict = {'a': 1, 'b': 2}
my_dict['c'] = 3
print(my_dict)  # Output: {'a': 1, 'b': 2, 'c': 3}
```

### some advanced set operations
```python
# Advanced Set Operations
set_a = {1, 2, 3, 4}
set_b = {3, 4, 5, 6}
# Union
union_set = set_a | set_b
print("Union:", union_set)  # Output: {1, 2, 3, 4, 5, 6}
# Intersection
intersection_set = set_a & set_b
print("Intersection:", intersection_set)  # Output: {3, 4}
# Difference
difference_set = set_a - set_b
print("Difference:", difference_set)  # Output: {1, 2}
```

### advan concept
```python
# List Comprehension
squares = [x**2 for x in range(10)]
print(squares)  # Output: [0, 1, 4, 9, 16, 25, 36, 49, 64, 81]
```
```python
# Lambda Function
add =  lambda a, b: a + b
print(add(3, 5))  # Output: 8
```


### Getting User Input
```python   
# Getting user input
name = input("Enter your name: ")
print(f"Hello, {name}!")
## get user input and do some calculation
age = int(input("Enter your age: "))
next_year_age = age + 1
print(f"Next year, you will be {next_year_age} years old.")
```

### detructuring variables
* Destructuring (or unpacking) allows you to assign values from a tuple or list to multiple variables in a single statement.
```python
# Destructuring Variables
point = (3, 4)
x, y = point
print(f"x: {x}, y: {y}")  # Output: x: 3, y: 4

head, *tail = [1, 2, 3, 4, 5]
print(f"head: {head}, tail: {tail}")  # Output: head: 1, tail: [2, 3, 4, 5]
```

### dictionary comprehension
```python
# Dictionary Comprehension
squares_dict = {x: x**2 for x in range(5)}
print(squares_dict)  # Output: {0: 0, 1: 1, 2: 4, 3: 9, 4: 16}

users =  [
    (0, "Alice", "password1"),
    (1, "Bob", "password2"),
    (2, "Charlie", "password3"),
]
username_mapping = {user[1]: user for user in users}
print(username_mapping)
# Output: {'Alice': (0, 'Alice', 'password1'), 'Bob': (1, 'Bob', 'password2'), 'Charlie': (2, 'Charlie', 'password3')}
```

## unpacking function arguments
* *args: allows a function to accept any number of positional arguments as a tuple.
```python
# Unpacking Function Arguments
def greet(first_name, last_name):
    print(f"Hello, {first_name} {last_name}!")

person = ("John", "Doe")
greet(*person)  # Unpacking the tuple into function arguments
# Output: Hello, John Doe!

def printArgs(*args):
    print(args)
def add(x , y):
    return x + y

def printMoreArgs(*args, extra):
    print("Args:", args)
    print("Extra:", extra)

printMoreArgs(1, 2, 3, extra="This is extra")
# Output:
# Args: (1, 2, 3)
# Extra: This is extra

printArgs(1, 2, 3, "four", "five")
# Output: (1, 2, 3, 'four', 'five')

nums = [10, 20]
result = add(*nums)  # Unpacking the list into function arguments
print(result)  # Output: 30

nums2 = {'x': 5, 'y': 15}
result2 = add(**nums2)  # Unpacking the dictionary into function arguments
print(result2)  # Output: 20
```
* explain **num2: The double asterisks (**) before nums2 in the function call add(**nums2) unpack the dictionary into keyword arguments. This means that the keys of the dictionary (in this case, 'x' and 'y') are used as the parameter names in the function, and their corresponding values (5 and 15) are passed as the argument values. So, it's equivalent to calling add(x=5, y=15). 
* if u want to pass list or tuple as positional arguments use * before the list or tuple name while calling the function.

### keyword arguments
* Keyword arguments allow you to specify arguments by name when calling a function, making the code more readable and allowing you to skip optional parameters. kwargs will be save as a dictionary inside the function.
* if u want to pass dictionary as keyword arguments use ** before the dictionary name while calling the function.
* 
```python
# Keyword Arguments
def named(**kwargs):
    for key, value in kwargs.items():
        print(f"{key}: {value}")

detals =  {"name": "Alice", "age": 30, "city": "New York"}

named(**detals)
# Output:
# name: Alice
# age: 30
# city: New York
```

### Magic methods __str__ and __repr__
* `__str__` and `__repr__` are special methods in Python that define how objects of a class are represented as strings.
* why we need the underscore? The double underscores (also known as "dunder") before and after the method names indicate that these are special or "magic" methods in Python. They are part of Python's data model and are used to implement certain behaviors for objects.
* `__str__` is intended to provide a "pretty" or user-friendly string representation of the object, while `__repr__` is meant to provide an "official" string representation that can be used to recreate the object.
```python
class Person:
    def __init__(self, name, age):
        self.name = name
        self.age = age

    def __str__(self):
        return f"{self.name}, {self.age} years old"

    def __repr__(self):
        return f"Person(name='{self.name}', age={self.age})"
```

## @classmethod and @staticmethod
* `@classmethod` and `@staticmethod` are decorators in Python that define methods within a class that are not tied to a specific instance of the class.
* A `@classmethod` takes `cls` as the first parameter, which refers to the class itself. It can access and modify class state that applies across all instances of the class.
* A `@staticmethod` does not take `self` or `cls` as the first parameter. It behaves like a regular function but belongs to the class's namespace. It cannot access or modify class or instance state.
```python
class MyClass:
    class_variable = 0

    def __init__(self, value):
        self.instance_variable = value

    @classmethod
    def increment_class_variable(cls):
        cls.class_variable += 1
    @staticmethod
    def static_method_example():
        return "This is a static method."
# Usage
MyClass.increment_class_variable()
print(MyClass.class_variable)  # Output: 1
print(MyClass.static_method_example())  # Output: This is a static method.
```

## first class functions
* In Python, functions are first-class citizens, meaning they can be treated like any other object. This allows you to pass functions as arguments to other functions, return them from functions, and assign them to variables.
```python
# First-Class Functions
def greet(name):
    return f"Hello, {name}!"
def call_function(func, arg):
    return func(arg)
result = call_function(greet, "Alice")
print(result)  # Output: Hello, Alice!
# Assigning a function to a variable
say_hello = greet
print(say_hello("Bob"))  # Output: Hello, Bob!
```