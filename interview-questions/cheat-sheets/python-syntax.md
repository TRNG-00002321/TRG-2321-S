# Python Syntax Cheat Sheet

## Data Types

```python
# Primitives
integer = 42
floating = 3.14
string = "Hello"
boolean = True
none_value = None

# Collections
my_list = [1, 2, 3]           # Mutable, ordered
my_tuple = (1, 2, 3)          # Immutable, ordered
my_set = {1, 2, 3}            # Mutable, unique
my_dict = {"key": "value"}    # Key-value pairs
```

## String Operations

| Operation | Example | Result |
|-----------|---------|--------|
| Concatenate | `"Hello" + " World"` | `"Hello World"` |
| Repeat | `"Hi" * 3` | `"HiHiHi"` |
| Length | `len("Hello")` | `5` |
| Uppercase | `"hello".upper()` | `"HELLO"` |
| Lowercase | `"HELLO".lower()` | `"hello"` |
| Strip | `" hi ".strip()` | `"hi"` |
| Split | `"a,b,c".split(",")` | `["a","b","c"]` |
| Join | `",".join(["a","b"])` | `"a,b"` |
| Replace | `"hello".replace("l","x")` | `"hexxo"` |
| F-string | `f"Hello {name}"` | `"Hello Alice"` |

## List Operations

```python
lst = [1, 2, 3, 4, 5]

lst.append(6)         # Add to end: [1,2,3,4,5,6]
lst.insert(0, 0)      # Insert at index: [0,1,2,3,4,5,6]
lst.remove(3)         # Remove first occurrence
lst.pop()             # Remove and return last item
lst.pop(0)            # Remove and return at index
lst.extend([7, 8])    # Add multiple items
lst.reverse()         # Reverse in place
lst.sort()            # Sort in place
len(lst)              # Length
lst[0]                # First element
lst[-1]               # Last element
lst[1:3]              # Slice [1] to [2]
3 in lst              # Check membership
```

## Dictionary Operations

```python
d = {"name": "Alice", "age": 30}

d["name"]             # Get value: "Alice"
d.get("name")         # Safe get (returns None if missing)
d.get("x", "default") # Get with default
d["city"] = "NYC"     # Add/update key
del d["age"]          # Delete key
d.keys()              # All keys
d.values()            # All values
d.items()             # All key-value pairs
"name" in d           # Check key exists
d.update({"a": 1})    # Merge dictionaries
```

## Control Flow

```python
# If-elif-else
if x > 0:
    print("positive")
elif x < 0:
    print("negative")
else:
    print("zero")

# Ternary operator
result = "yes" if condition else "no"

# For loop
for item in collection:
    print(item)

for i in range(5):         # 0 to 4
    print(i)

for i, item in enumerate(lst):  # With index
    print(i, item)

# While loop
while condition:
    # do something
    if done:
        break
    if skip:
        continue
```

## Functions

```python
# Basic function
def greet(name):
    return f"Hello, {name}!"

# Default parameters
def greet(name="World"):
    return f"Hello, {name}!"

# *args and **kwargs
def func(*args, **kwargs):
    print(args)    # Tuple of positional args
    print(kwargs)  # Dict of keyword args

# Lambda
square = lambda x: x ** 2

# Map, Filter, Reduce
doubled = list(map(lambda x: x*2, [1,2,3]))
evens = list(filter(lambda x: x%2==0, [1,2,3,4]))
from functools import reduce
total = reduce(lambda a,b: a+b, [1,2,3,4])
```

## Comprehensions

```python
# List comprehension
squares = [x**2 for x in range(5)]
evens = [x for x in range(10) if x % 2 == 0]

# Dict comprehension
squared = {x: x**2 for x in range(5)}

# Set comprehension
unique = {x % 3 for x in range(10)}
```

## Classes

```python
class Dog:
    species = "Canis familiaris"  # Class attribute
    
    def __init__(self, name, age):
        self.name = name          # Instance attribute
        self.age = age
    
    def bark(self):
        return f"{self.name} says woof!"
    
    def __str__(self):
        return f"{self.name}, {self.age} years old"

# Inheritance
class Labrador(Dog):
    def __init__(self, name, age, color):
        super().__init__(name, age)
        self.color = color
```

## Exception Handling

```python
try:
    result = risky_operation()
except ValueError as e:
    print(f"Value error: {e}")
except Exception as e:
    print(f"Error: {e}")
else:
    print("Success!")
finally:
    cleanup()

# Raise exception
raise ValueError("Invalid input")

# Custom exception
class MyError(Exception):
    pass
```

## File Operations

```python
# Read file
with open("file.txt", "r") as f:
    content = f.read()       # Entire file
    lines = f.readlines()    # List of lines

# Write file
with open("file.txt", "w") as f:
    f.write("Hello\n")

# Append to file
with open("file.txt", "a") as f:
    f.write("More text\n")
```

## Common Modules

```python
import os
os.getcwd()              # Current directory
os.listdir(".")          # List directory
os.path.exists("file")   # Check if exists

import json
json.dumps(obj)          # Object to JSON string
json.loads(string)       # JSON string to object

import datetime
datetime.datetime.now()  # Current datetime
datetime.date.today()    # Current date
```
