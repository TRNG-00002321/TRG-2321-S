# Python Interview Questions & Answers

## Beginner Level

**Q1: What are Python's key features that make it popular?**

> **Answer:**
> - **Easy to read:** Clean syntax resembling English
> - **Dynamically typed:** No need to declare variable types
> - **Interpreted:** No compilation step; run code directly
> - **Extensive libraries:** Rich ecosystem (NumPy, Pandas, Flask, pytest)
> - **Cross-platform:** Runs on Windows, Mac, Linux
> - **Multi-paradigm:** Supports OOP, functional, and procedural programming
>
> **Example:**
> ```python
> # Python is concise
> numbers = [1, 2, 3, 4, 5]
> squares = [n**2 for n in numbers]  # List comprehension
> ```

---

**Q2: Explain the difference between list, tuple, set, and dictionary.**

> **Answer:**
>
> | Collection | Syntax | Mutable | Ordered | Duplicates | Use Case |
> |------------|--------|---------|---------|------------|----------|
> | List | `[1, 2, 3]` | Yes | Yes | Yes | General collection |
> | Tuple | `(1, 2, 3)` | No | Yes | Yes | Fixed data, dict keys |
> | Set | `{1, 2, 3}` | Yes | No | No | Unique items, fast lookup |
> | Dict | `{"a": 1}` | Yes | Yes* | Keys: No | Key-value mapping |
>
> **Examples:**
> ```python
> # List - mutable, ordered
> fruits = ["apple", "banana"]
> fruits.append("cherry")
> 
> # Tuple - immutable
> coordinates = (10.5, 20.3)
> # coordinates[0] = 15  # Error!
> 
> # Set - unique values
> unique = {1, 2, 2, 3}  # {1, 2, 3}
> 
> # Dict - key-value pairs
> user = {"name": "Alice", "age": 30}
> print(user["name"])  # Alice
> ```

---

**Q3: What is the difference between `==` and `is`?**

> **Answer:**
> - `==` compares **values** (equality)
> - `is` compares **identity** (same object in memory)
>
> ```python
> a = [1, 2, 3]
> b = [1, 2, 3]
> c = a
> 
> print(a == b)  # True (same values)
> print(a is b)  # False (different objects)
> print(a is c)  # True (same object)
> 
> # Common with None
> x = None
> if x is None:  # Preferred over x == None
>     print("x is None")
> ```
>
> **Best practice:** Use `is` for `None`, `True`, `False`; use `==` for value comparison.

---

**Q4: How do you handle exceptions in Python?**

> **Answer:**
> ```python
> try:
>     result = 10 / 0
> except ZeroDivisionError as e:
>     print(f"Cannot divide by zero: {e}")
> except Exception as e:
>     print(f"Unexpected error: {e}")
> else:
>     print("Success!")  # Runs if no exception
> finally:
>     print("Cleanup")   # Always runs
> 
> # Raising exceptions
> def validate_age(age):
>     if age < 0:
>         raise ValueError("Age cannot be negative")
>     return age
> 
> # Custom exception
> class ValidationError(Exception):
>     pass
> ```
>
> **Best practices:**
> - Catch specific exceptions, not bare `except:`
> - Don't silence exceptions without logging
> - Use `finally` for cleanup (or context managers)

---

**Q5: What are *args and **kwargs?**

> **Answer:** They allow functions to accept variable numbers of arguments.
>
> - `*args`: Variable positional arguments (tuple)
> - `**kwargs`: Variable keyword arguments (dict)
>
> ```python
> def example(*args, **kwargs):
>     print(f"args: {args}")      # Tuple
>     print(f"kwargs: {kwargs}")  # Dict
> 
> example(1, 2, 3, name="Alice", age=30)
> # args: (1, 2, 3)
> # kwargs: {'name': 'Alice', 'age': 30}
> 
> # Practical example
> def create_user(username, **details):
>     user = {"username": username}
>     user.update(details)
>     return user
> 
> user = create_user("alice", email="alice@test.com", age=25)
> ```

---

## Intermediate Level

**Q6: Explain list comprehensions and when to use them.**

> **Answer:** List comprehensions provide a concise way to create lists.
>
> ```python
> # Traditional loop
> squares = []
> for n in range(5):
>     squares.append(n ** 2)
> 
> # List comprehension (same result)
> squares = [n ** 2 for n in range(5)]
> 
> # With condition
> evens = [n for n in range(10) if n % 2 == 0]
> 
> # Nested
> matrix = [[i*j for j in range(3)] for i in range(3)]
> 
> # Dict comprehension
> squared = {n: n**2 for n in range(5)}
> 
> # Set comprehension
> unique_lengths = {len(word) for word in words}
> ```
>
> **When to use:**
> - Simple transformations and filtering
> - When readability is maintained
>
> **When NOT to use:**
> - Complex logic (use regular loops)
> - Side effects needed
> - Very long expressions

---

**Q7: What are decorators and how do they work?**

> **Answer:** Decorators wrap functions to extend their behavior without modifying them.
>
> ```python
> # Basic decorator
> def timer(func):
>     def wrapper(*args, **kwargs):
>         import time
>         start = time.time()
>         result = func(*args, **kwargs)
>         print(f"{func.__name__} took {time.time() - start:.2f}s")
>         return result
>     return wrapper
> 
> @timer
> def slow_function():
>     import time
>     time.sleep(1)
>     return "Done"
> 
> slow_function()  # Prints: slow_function took 1.00s
> 
> # Decorator with arguments
> def retry(max_attempts=3):
>     def decorator(func):
>         def wrapper(*args, **kwargs):
>             for attempt in range(max_attempts):
>                 try:
>                     return func(*args, **kwargs)
>                 except Exception as e:
>                     if attempt == max_attempts - 1:
>                         raise
>         return wrapper
>     return decorator
> 
> @retry(max_attempts=3)
> def unreliable_api_call():
>     # May fail
>     pass
> ```
>
> **Common uses:** Logging, timing, authentication, caching, validation.

---

**Q8: Explain the difference between shallow copy and deep copy.**

> **Answer:**
> - **Shallow copy:** Creates new object, but nested objects are still references
> - **Deep copy:** Creates completely independent copy of all objects
>
> ```python
> import copy
> 
> original = [[1, 2], [3, 4]]
> 
> # Shallow copy
> shallow = copy.copy(original)
> shallow[0][0] = 99
> print(original[0][0])  # 99 (affected!)
> 
> # Deep copy
> original = [[1, 2], [3, 4]]
> deep = copy.deepcopy(original)
> deep[0][0] = 99
> print(original[0][0])  # 1 (unchanged)
> ```
>
> **When to use each:**
> - **Shallow:** Simple objects, performance matters
> - **Deep:** Nested mutable objects that need independence

---

**Q9: What are generators and when would you use them?**

> **Answer:** Generators produce values lazily, one at a time, saving memory.
>
> ```python
> # Generator function (uses yield)
> def count_up_to(n):
>     i = 1
>     while i <= n:
>         yield i
>         i += 1
> 
> # Usage
> for num in count_up_to(5):
>     print(num)  # Prints 1, 2, 3, 4, 5
> 
> # Generator expression
> squares = (n**2 for n in range(1000000))  # No memory used yet
> print(next(squares))  # 0 - generates on demand
> 
> # Reading large file
> def read_large_file(filepath):
>     with open(filepath) as f:
>         for line in f:
>             yield line.strip()
> ```
>
> **Benefits:**
> - Memory efficient for large datasets
> - Lazy evaluation
> - Can represent infinite sequences
>
> **Use when:** Large data, streaming, infinite sequences.

---

**Q10: Explain Python's OOP concepts (classes, inheritance, encapsulation).**

> **Answer:**
> ```python
> # Class definition
> class Animal:
>     species = "Unknown"  # Class attribute
>     
>     def __init__(self, name):
>         self.name = name  # Instance attribute
>         self._age = 0     # Protected (convention)
>         self.__id = 123   # Private (name mangling)
>     
>     def speak(self):
>         return "..."
>     
>     @property
>     def age(self):
>         return self._age
>     
>     @age.setter
>     def age(self, value):
>         if value < 0:
>             raise ValueError("Age cannot be negative")
>         self._age = value
> 
> # Inheritance
> class Dog(Animal):
>     def __init__(self, name, breed):
>         super().__init__(name)
>         self.breed = breed
>     
>     def speak(self):  # Override
>         return f"{self.name} says Woof!"
> 
> # Usage
> dog = Dog("Buddy", "Labrador")
> print(dog.speak())  # Buddy says Woof!
> dog.age = 3
> print(dog.age)  # 3
> ```
>
> **Four Pillars:**
> - **Encapsulation:** Bundling data and methods, hiding internals
> - **Inheritance:** Creating classes from existing ones
> - **Polymorphism:** Same interface, different implementations
> - **Abstraction:** Hiding complex implementation details

---

## Advanced Level

**Q11: What is the Global Interpreter Lock (GIL)?**

> **Answer:** The GIL is a mutex that protects access to Python objects, preventing multiple threads from executing Python bytecode simultaneously.
>
> **Implications:**
> - Only one thread executes Python code at a time
> - CPU-bound tasks don't benefit from threading
> - I/O-bound tasks still benefit from threading (GIL released during I/O)
>
> **Workarounds:**
> ```python
> # For CPU-bound: Use multiprocessing
> from multiprocessing import Pool
> 
> def cpu_intensive(n):
>     return sum(i**2 for i in range(n))
> 
> with Pool(4) as p:
>     results = p.map(cpu_intensive, [1000000]*4)
> 
> # For I/O-bound: Use asyncio or threading
> import asyncio
> 
> async def fetch_data(url):
>     # async HTTP call
>     pass
> ```
>
> **Note:** GIL is a CPython implementation detail, not a Python language feature.

---

**Q12: Explain context managers and the `with` statement.**

> **Answer:** Context managers handle setup and cleanup automatically, commonly for resource management.
>
> ```python
> # Using with statement
> with open("file.txt", "r") as f:
>     content = f.read()
> # File automatically closed
> 
> # Creating context manager (class-based)
> class DatabaseConnection:
>     def __enter__(self):
>         self.conn = create_connection()
>         return self.conn
>     
>     def __exit__(self, exc_type, exc_val, exc_tb):
>         self.conn.close()
>         return False  # Don't suppress exceptions
> 
> with DatabaseConnection() as conn:
>     conn.execute("SELECT * FROM users")
> 
> # Using contextlib decorator
> from contextlib import contextmanager
> 
> @contextmanager
> def timer():
>     import time
>     start = time.time()
>     yield
>     print(f"Elapsed: {time.time() - start:.2f}s")
> 
> with timer():
>     # Timed code block
>     time.sleep(1)
> ```
>
> **Benefits:** Guaranteed cleanup, cleaner code, exception safety.
