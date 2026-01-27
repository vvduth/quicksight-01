### decorators
* A decorator is a special type of function that modifies the behavior of another function. Decorators are often used for logging, access control, instrumentation, and caching.
* In Python, decorators are defined using the `@` symbol followed by the decorator function name
```python
user = {"username": "admin", "access_level": "guest"}


def get_admin_data():
    return "Sensitive admin data"

def secure_get_admin():
    if user["access_level"] == "admin":
        return get_admin_data()
    else:
        return "Access denied"

print(secure_get_admin())  # Output: Access denied
```
* what if we do not want to add if statement every time we call the function? We can use decorators to wrap the function and add the access control logic.
* functools.wraps is a decorator that is used to preserve the metadata of the original function when it is wrapped by another function. This includes attributes like the function's name, docstring, and module.
```python
def secure_access(func):
    @functools.wraps(func)
    def wrapper():
        if user["access_level"] == "admin":
            return func()
        else:
            return "Access denied"
    return wrapper

@secure_access
def get_admin_data():
    return "Sensitive admin data"

print(get_admin_data())  # Output: Access denied
```

## decorationg dunc with parameters
* You can also create decorators that accept parameters. This allows you to customize the behavior of the decorator based on the arguments provided.
* pass (*args, **kwargs) to the wrapper function to handle any number of positional and keyword arguments.
```python
# new get_admin_data with parameters
def get_admin_data(panel):
    if panel == "admin":
        return "Settings data"
    else if panel == "billing":
        return "User data"

def secure_access(func):
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        if user["access_level"] == "admin":
            return func(*args, **kwargs)
        else:
            return "Access denied"
    return wrapper
```

### decorator with parameters
* To create a decorator that accepts parameters, you need to define a function that returns a decorator. This outer function will take the parameters for the decorator, and the inner function will be the actual decorator that wraps the target function.
```python

def make_secure(access_level):
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            if user["access_level"] == access_level:
                return func(*args, **kwargs)
            else:
                return "Access denied"
        return wrapper
    return decorator
@make_secure("admin")
def get_admin_data():
    return "Sensitive admin data"

@make_secure("user")
def get_user_data():
    return "User data"
```

### mutabilily in python
* In Python, mutability refers to whether an object can be changed after it is created. Mutable objects can be modified, while immutable objects cannot.
* Examples of mutable objects include lists, dictionaries, and sets. You can add, remove, or change elements in these objects.
* Examples of immutable objects include integers, floats, strings, and tuples. Once these objects are created, their values cannot be changed.
```python
# Mutable object example
my_list = [1, 2, 3]
my_list.append(4)  # Modifying the list
print(my_list)  # Output: [1, 2, 3, 4]
# Immutable object example
my_string = "Hello"
my_string += " World"  # Creating a new string
print(my_string)  # Output: Hello World
```

## class in python
* A class in Python is a blueprint for creating objects. It defines a set of attributes and methods that the created objects will have.
* no access modifier like public, private, protected in other languages. By convention, a single underscore (_) before an attribute or method name indicates that it is intended for internal use (protected), while a double underscore (__) indicates that it is intended to be private. 
```python
class Dog:
    def __init__(self, name, age):
        self.name = name
        self.age = age

    def bark(self):
        return "Woof!"
# Creating an instance of the Dog class
my_dog = Dog("Buddy", 3)
print(my_dog.name)  # Output: Buddy
print(my_dog.bark())  # Output: Woof!
# changing attributes
my_dog.age = 4
print(my_dog.age)  # Output: 4
```