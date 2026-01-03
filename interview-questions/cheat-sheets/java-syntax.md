# Java Syntax Cheat Sheet

## Data Types

### Primitives
| Type | Size | Range | Default |
|------|------|-------|---------|
| `byte` | 8-bit | -128 to 127 | 0 |
| `short` | 16-bit | -32,768 to 32,767 | 0 |
| `int` | 32-bit | -2³¹ to 2³¹-1 | 0 |
| `long` | 64-bit | -2⁶³ to 2⁶³-1 | 0L |
| `float` | 32-bit | ±3.4e38 | 0.0f |
| `double` | 64-bit | ±1.7e308 | 0.0d |
| `boolean` | 1-bit | true/false | false |
| `char` | 16-bit | 0 to 65,535 | '\u0000' |

### Reference Types
```java
String name = "Alice";
Integer num = 42;           // Wrapper class
int[] array = {1, 2, 3};
List<String> list = new ArrayList<>();
Map<String, Integer> map = new HashMap<>();
```

## Variables & Constants

```java
// Variable declaration
int count = 0;
String name = "Alice";
final double PI = 3.14159;  // Constant

// var (Java 10+)
var message = "Hello";  // Type inferred
```

## Operators

```java
// Arithmetic
+  -  *  /  %  ++  --

// Comparison
==  !=  >  <  >=  <=

// Logical
&&  ||  !

// Assignment
=  +=  -=  *=  /=  %=

// Ternary
result = condition ? valueIfTrue : valueIfFalse;
```

## Control Flow

### If-Else
```java
if (condition) {
    // code
} else if (anotherCondition) {
    // code
} else {
    // code
}
```

### Switch
```java
// Traditional
switch (value) {
    case 1:
        // code
        break;
    case 2:
        // code
        break;
    default:
        // code
}

// Enhanced (Java 14+)
String result = switch (day) {
    case 1, 7 -> "Weekend";
    case 2, 3, 4, 5, 6 -> "Weekday";
    default -> "Invalid";
};
```

### Loops
```java
// For loop
for (int i = 0; i < 10; i++) {
    System.out.println(i);
}

// Enhanced for
for (String item : list) {
    System.out.println(item);
}

// While
while (condition) {
    // code
}

// Do-while
do {
    // code
} while (condition);

// Loop control
break;     // Exit loop
continue;  // Skip iteration
```

## Methods

```java
// Method declaration
public static int add(int a, int b) {
    return a + b;
}

// Method overloading
public int add(int a, int b) { return a + b; }
public double add(double a, double b) { return a + b; }

// Varargs
public void printAll(String... items) {
    for (String item : items) {
        System.out.println(item);
    }
}
```

## Classes & Objects

```java
public class Person {
    // Instance variables
    private String name;
    private int age;
    
    // Static variable
    private static int count = 0;
    
    // Constructor
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        count++;
    }
    
    // Getter
    public String getName() {
        return name;
    }
    
    // Setter
    public void setName(String name) {
        this.name = name;
    }
    
    // Instance method
    public String greet() {
        return "Hello, " + name;
    }
    
    // Static method
    public static int getCount() {
        return count;
    }
    
    // toString override
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}

// Usage
Person p = new Person("Alice", 30);
String name = p.getName();
```

## Inheritance

```java
// Parent class
public class Animal {
    protected String name;
    
    public Animal(String name) {
        this.name = name;
    }
    
    public void speak() {
        System.out.println("...");
    }
}

// Child class
public class Dog extends Animal {
    public Dog(String name) {
        super(name);  // Call parent constructor
    }
    
    @Override
    public void speak() {
        System.out.println(name + " barks!");
    }
}
```

## Interfaces

```java
public interface Drawable {
    void draw();  // Abstract method
    
    default void print() {  // Default method
        System.out.println("Printing...");
    }
    
    static void info() {  // Static method
        System.out.println("Drawable interface");
    }
}

public class Circle implements Drawable {
    @Override
    public void draw() {
        System.out.println("Drawing circle");
    }
}
```

## Abstract Classes

```java
public abstract class Shape {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    // Abstract method (no body)
    public abstract double area();
    
    // Concrete method
    public String getColor() {
        return color;
    }
}

public class Rectangle extends Shape {
    private double width, height;
    
    @Override
    public double area() {
        return width * height;
    }
}
```

## Exception Handling

```java
try {
    // Risky code
    int result = 10 / 0;
} catch (ArithmeticException e) {
    // Handle specific exception
    System.err.println("Cannot divide by zero");
} catch (Exception e) {
    // Handle general exception
    e.printStackTrace();
} finally {
    // Always executes
    cleanup();
}

// Throw exception
if (age < 0) {
    throw new IllegalArgumentException("Age cannot be negative");
}

// Method that throws
public void readFile() throws IOException {
    // code that may throw IOException
}
```

## Collections

### List
```java
List<String> list = new ArrayList<>();
list.add("a");
list.add("b");
list.get(0);           // "a"
list.set(0, "x");      // Replace
list.remove(0);        // Remove by index
list.remove("b");      // Remove by value
list.size();           // Length
list.contains("a");    // Check existence
list.isEmpty();        // Check empty
list.clear();          // Remove all
```

### Set
```java
Set<String> set = new HashSet<>();
set.add("a");
set.add("a");          // Ignored (duplicate)
set.contains("a");     // true
set.remove("a");
```

### Map
```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.put("b", 2);
map.get("a");           // 1
map.getOrDefault("c", 0);  // 0 (default)
map.containsKey("a");   // true
map.containsValue(1);   // true
map.remove("a");
map.keySet();           // Set of keys
map.values();           // Collection of values
map.entrySet();         // Set of entries

// Iterate
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
```

## Streams (Java 8+)

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Filter
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// Map
List<Integer> doubled = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

// Reduce
int sum = numbers.stream()
    .reduce(0, Integer::sum);

// Other operations
.forEach(System.out::println)  // Print each
.count()                        // Count elements
.findFirst()                    // Optional<T>
.anyMatch(n -> n > 3)          // boolean
.sorted()                       // Sort
.distinct()                     // Remove duplicates
.limit(3)                       // First 3
.skip(2)                        // Skip first 2
```

## String Methods

```java
String s = "Hello World";

s.length()                 // 11
s.charAt(0)                // 'H'
s.substring(0, 5)          // "Hello"
s.toLowerCase()            // "hello world"
s.toUpperCase()            // "HELLO WORLD"
s.trim()                   // Remove whitespace
s.replace("l", "x")        // "Hexxo Worxd"
s.split(" ")               // ["Hello", "World"]
s.contains("World")        // true
s.startsWith("Hello")      // true
s.endsWith("World")        // true
s.equals("Hello World")    // true (value comparison)
s.equalsIgnoreCase("HELLO WORLD")  // true
String.join(", ", list)    // Join list elements
String.format("Hi %s", name)  // Formatted string
```

## Common Annotations

```java
@Override      // Override parent method
@Deprecated    // Marked for removal
@SuppressWarnings("unchecked")
@FunctionalInterface  // Single abstract method
```
