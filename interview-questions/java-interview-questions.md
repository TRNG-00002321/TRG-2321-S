# Java Interview Questions & Answers

## Beginner Level

**Q1: What are the key features of Java?**

> **Answer:**
> - **Platform Independent:** "Write once, run anywhere" via JVM
> - **Object-Oriented:** Everything is an object (except primitives)
> - **Strongly Typed:** Variables must be declared with types
> - **Automatic Memory Management:** Garbage collection
> - **Multi-threaded:** Built-in concurrency support
> - **Robust:** Strong type checking, exception handling
>
> **JVM Process:**
> ```
> Java Code (.java) → Compiler → Bytecode (.class) → JVM → Machine Code
> ```

---

**Q2: Explain the difference between primitive types and wrapper classes.**

> **Answer:**
>
> | Primitive | Wrapper | Size |
> |-----------|---------|------|
> | `int` | `Integer` | 32-bit |
> | `double` | `Double` | 64-bit |
> | `boolean` | `Boolean` | 1-bit |
> | `char` | `Character` | 16-bit |
>
> **When to use wrappers:**
> - Collections (can't use primitives)
> - Null values needed
> - Object methods needed
>
> ```java
> // Autoboxing (primitive → wrapper)
> Integer num = 5;
> 
> // Unboxing (wrapper → primitive)
> int n = num;
> 
> // Collections require wrappers
> List<Integer> numbers = new ArrayList<>();
> numbers.add(1);  // Autoboxed
> ```

---

**Q3: What is the difference between `==` and `.equals()`?**

> **Answer:**
> - `==` compares **references** (memory addresses)
> - `.equals()` compares **values** (content)
>
> ```java
> String a = new String("hello");
> String b = new String("hello");
> String c = a;
> 
> System.out.println(a == b);       // false (different objects)
> System.out.println(a.equals(b));  // true (same content)
> System.out.println(a == c);       // true (same reference)
> 
> // Integer caching (-128 to 127)
> Integer x = 100;
> Integer y = 100;
> System.out.println(x == y);  // true (cached)
> 
> Integer p = 200;
> Integer q = 200;
> System.out.println(p == q);  // false (not cached)
> ```
>
> **Best practice:** Always use `.equals()` for object comparison.

---

**Q4: Explain access modifiers in Java.**

> **Answer:**
>
> | Modifier | Class | Package | Subclass | World |
> |----------|-------|---------|----------|-------|
> | `public` | ✅ | ✅ | ✅ | ✅ |
> | `protected` | ✅ | ✅ | ✅ | ❌ |
> | (default) | ✅ | ✅ | ❌ | ❌ |
> | `private` | ✅ | ❌ | ❌ | ❌ |
>
> ```java
> public class User {
>     public String name;      // Accessible everywhere
>     protected int age;       // Same package + subclasses
>     String email;            // Package-private (default)
>     private String password; // Only this class
> }
> ```
>
> **Encapsulation best practice:** Fields private, accessed via getters/setters.

---

**Q5: What is the difference between abstract class and interface?**

> **Answer:**
>
> | Feature | Abstract Class | Interface |
> |---------|---------------|-----------|
> | Methods | Abstract + concrete | Abstract (default since Java 8) |
> | Variables | Any type | public static final only |
> | Inheritance | Single | Multiple |
> | Constructor | Yes | No |
> | Use case | IS-A relationship | CAN-DO capability |
>
> ```java
> // Abstract class
> abstract class Animal {
>     protected String name;
>     
>     public Animal(String name) {
>         this.name = name;
>     }
>     
>     abstract void speak();  // Must be implemented
>     
>     void sleep() {
>         System.out.println("Sleeping...");
>     }
> }
> 
> // Interface
> interface Flyable {
>     void fly();  // Implicitly public abstract
>     
>     default void land() {  // Java 8+
>         System.out.println("Landing...");
>     }
> }
> 
> // Implementation
> class Bird extends Animal implements Flyable {
>     public Bird(String name) { super(name); }
>     void speak() { System.out.println("Chirp!"); }
>     public void fly() { System.out.println("Flying!"); }
> }
> ```
>
> **When to use:**
> - **Abstract class:** Shared code, common base, IS-A relationship
> - **Interface:** Defining capabilities, multiple inheritance, API contracts

---

## Intermediate Level

**Q6: Explain exception handling in Java.**

> **Answer:**
> ```java
> // Try-catch-finally
> try {
>     int result = 10 / 0;
> } catch (ArithmeticException e) {
>     System.out.println("Cannot divide by zero");
> } catch (Exception e) {
>     System.out.println("General error: " + e.getMessage());
> } finally {
>     System.out.println("Always executes");
> }
> 
> // Try-with-resources (auto-close)
> try (FileReader reader = new FileReader("file.txt")) {
>     // Use reader
> } catch (IOException e) {
>     e.printStackTrace();
> }
> // Reader automatically closed
> 
> // Throwing exceptions
> public void validateAge(int age) {
>     if (age < 0) {
>         throw new IllegalArgumentException("Age cannot be negative");
>     }
> }
> 
> // Checked vs Unchecked
> // Checked: Must be caught or declared (IOException, SQLException)
> // Unchecked: Runtime exceptions (NullPointerException, IndexOutOfBoundsException)
> ```
>
> **Hierarchy:**
> ```
> Throwable
> ├── Error (system errors, don't catch)
> └── Exception
>     ├── RuntimeException (unchecked)
>     └── Other exceptions (checked)
> ```

---

**Q7: What are generics and why are they useful?**

> **Answer:** Generics provide type safety at compile time and reduce casting.
>
> ```java
> // Without generics (unsafe)
> List list = new ArrayList();
> list.add("hello");
> list.add(123);  // No compile error
> String s = (String) list.get(1);  // Runtime error!
> 
> // With generics (safe)
> List<String> list = new ArrayList<>();
> list.add("hello");
> // list.add(123);  // Compile error!
> String s = list.get(0);  // No casting needed
> 
> // Generic class
> public class Box<T> {
>     private T content;
>     
>     public void set(T content) { this.content = content; }
>     public T get() { return content; }
> }
> 
> Box<String> stringBox = new Box<>();
> stringBox.set("Hello");
> 
> // Generic method
> public <T> T getFirst(List<T> list) {
>     return list.isEmpty() ? null : list.get(0);
> }
> 
> // Bounded generics
> public <T extends Comparable<T>> T max(T a, T b) {
>     return a.compareTo(b) > 0 ? a : b;
> }
> ```
>
> **Wildcards:**
> - `<?>` - Unknown type
> - `<? extends T>` - T or subtype (read only)
> - `<? super T>` - T or supertype (write only)

---

**Q8: Explain the Stream API and common operations.**

> **Answer:** Streams provide functional-style operations on collections.
>
> ```java
> List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
> 
> // Filter - keep matching elements
> List<Integer> evens = numbers.stream()
>     .filter(n -> n % 2 == 0)
>     .collect(Collectors.toList());
> // [2, 4, 6, 8, 10]
> 
> // Map - transform elements
> List<Integer> doubled = numbers.stream()
>     .map(n -> n * 2)
>     .collect(Collectors.toList());
> // [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]
> 
> // Reduce - combine to single value
> int sum = numbers.stream()
>     .reduce(0, Integer::sum);
> // 55
> 
> // Chaining operations
> double average = numbers.stream()
>     .filter(n -> n > 5)
>     .mapToInt(Integer::intValue)
>     .average()
>     .orElse(0.0);
> 
> // Collect to Map
> Map<Boolean, List<Integer>> partitioned = numbers.stream()
>     .collect(Collectors.partitioningBy(n -> n % 2 == 0));
> 
> // Sorting
> List<String> sorted = names.stream()
>     .sorted(Comparator.comparing(String::length))
>     .collect(Collectors.toList());
> ```
>
> **Key operations:**
> - **Intermediate:** filter, map, sorted, distinct, limit, skip
> - **Terminal:** collect, forEach, reduce, count, findFirst, anyMatch

---

**Q9: What is the difference between ArrayList and LinkedList?**

> **Answer:**
>
> | Operation | ArrayList | LinkedList |
> |-----------|-----------|------------|
> | Random access | O(1) ✅ | O(n) ❌ |
> | Add at end | O(1)* | O(1) |
> | Add at beginning | O(n) ❌ | O(1) ✅ |
> | Remove from middle | O(n) | O(1)** |
> | Memory | Less | More (node overhead) |
>
> \* Amortized
> \** Once iterator is positioned
>
> ```java
> // ArrayList - backed by array
> List<String> arrayList = new ArrayList<>();
> arrayList.get(5);  // Fast - O(1)
> 
> // LinkedList - doubly linked nodes
> List<String> linkedList = new LinkedList<>();
> ((LinkedList<String>) linkedList).addFirst("first");  // Fast - O(1)
> ```
>
> **When to use:**
> - **ArrayList:** Random access, lots of reads, infrequent inserts
> - **LinkedList:** Frequent add/remove at ends, queue/deque operations

---

**Q10: Explain the `final` keyword in different contexts.**

> **Answer:**
>
> ```java
> // Final variable - constant, cannot be reassigned
> final int MAX_SIZE = 100;
> // MAX_SIZE = 200;  // Compile error
> 
> // Final reference - reference can't change, content can
> final List<String> list = new ArrayList<>();
> list.add("item");  // OK - modifying content
> // list = new ArrayList<>();  // Error - changing reference
> 
> // Final method - cannot be overridden
> class Parent {
>     final void important() {
>         System.out.println("Cannot override");
>     }
> }
> 
> // Final class - cannot be extended
> final class Utility {
>     // No subclasses allowed
> }
> 
> // Final parameter - cannot be modified in method
> void process(final String input) {
>     // input = "new value";  // Error
> }
> ```
>
> **Benefits:**
> - Immutability and thread safety
> - Design clarity (constants, sealed classes)
> - Potential JVM optimizations

---

## Advanced Level

**Q11: Explain multithreading and synchronization in Java.**

> **Answer:**
> ```java
> // Creating threads
> class MyThread extends Thread {
>     public void run() {
>         System.out.println("Thread running");
>     }
> }
> 
> // Using Runnable (preferred)
> Runnable task = () -> System.out.println("Task running");
> Thread thread = new Thread(task);
> thread.start();
> 
> // Synchronization - prevent race conditions
> class Counter {
>     private int count = 0;
>     
>     public synchronized void increment() {
>         count++;
>     }
>     
>     // Or synchronized block
>     public void add(int value) {
>         synchronized(this) {
>             count += value;
>         }
>     }
> }
> 
> // ExecutorService (modern approach)
> ExecutorService executor = Executors.newFixedThreadPool(4);
> executor.submit(() -> {
>     System.out.println("Task executed");
> });
> executor.shutdown();
> 
> // CompletableFuture (async programming)
> CompletableFuture<String> future = CompletableFuture
>     .supplyAsync(() -> fetchData())
>     .thenApply(data -> process(data))
>     .thenAccept(result -> save(result));
> ```
>
> **Thread safety options:**
> - `synchronized` keyword
> - `volatile` for visibility
> - `java.util.concurrent` locks
> - Atomic classes (`AtomicInteger`)
> - Immutable objects

---

**Q12: What are design patterns? Explain Singleton and Factory.**

> **Answer:**
>
> **Singleton:** Ensures only one instance exists.
> ```java
> public class Database {
>     private static volatile Database instance;
>     
>     private Database() {}  // Private constructor
>     
>     public static Database getInstance() {
>         if (instance == null) {
>             synchronized(Database.class) {
>                 if (instance == null) {
>                     instance = new Database();
>                 }
>             }
>         }
>         return instance;
>     }
> }
> 
> // Enum singleton (preferred)
> public enum Logger {
>     INSTANCE;
>     public void log(String message) { }
> }
> ```
>
> **Factory:** Creates objects without exposing creation logic.
> ```java
> interface Animal { void speak(); }
> 
> class Dog implements Animal {
>     public void speak() { System.out.println("Woof"); }
> }
> 
> class Cat implements Animal {
>     public void speak() { System.out.println("Meow"); }
> }
> 
> class AnimalFactory {
>     public static Animal create(String type) {
>         return switch(type.toLowerCase()) {
>             case "dog" -> new Dog();
>             case "cat" -> new Cat();
>             default -> throw new IllegalArgumentException("Unknown: " + type);
>         };
>     }
> }
> 
> // Usage
> Animal animal = AnimalFactory.create("dog");
> animal.speak();  // Woof
> ```
>
> **Benefits:**
> - **Singleton:** Shared resources, configuration, logging
> - **Factory:** Decoupling, flexibility, testability
