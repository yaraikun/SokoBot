# The Ultimate CSADPRG Reviewer 📚✨

---

# **Module 1: Evolution and Overview of Programming Languages**

## **🧠 1.1 Why Study Programming Languages?**

*   **Increased Capacity to Express Ideas:** Exposure to different language constructs expands a programmer's thinking process, even if their primary language lacks those features. Constructs can often be *simulated*.
*   **Improved Background for Choosing Appropriate Languages:** Understanding the strengths and weaknesses of various languages leads to better-informed decisions, avoiding the trap of using a familiar but ill-suited language.
*   **Increased Ability to Learn New Languages:** A solid grasp of the fundamental concepts (e.g., data types, control structures, scoping) makes learning new languages significantly easier.
*   **Better Understanding of Implementation:** Knowing the design considerations behind a language allows for more intelligent and efficient use of its features.
*   **Overall Advancement of Computing:** Moving beyond familiar languages and embracing new concepts helps drive innovation in the field.

## **💡 1.2 Programming Language Evaluation Criteria**

### **Readability**

> 📌 **Definition:** The ease with which programs can be read and understood. It is arguably the most important criterion, as it heavily impacts maintainability.

*   **Overall Simplicity:**
    *   **Too many features:** A language with a large number of constructs can be difficult to learn. Programmers often learn different subsets, leading to difficulties in understanding each other's code.
    *   **Feature Multiplicity:** Having multiple ways to perform the same operation (e.g., `count++`, `count += 1`, `count = count + 1` in Java).
    *   **Operator Overloading:** A single operator symbol having multiple meanings can cause confusion if not used carefully.
*   **Orthogonality:**
    *   > 📌 **Definition:** A small set of primitive constructs that can be combined in a small number of ways to build control and data structures. A lack of orthogonality leads to exceptions to the language's rules.
    *   **⚠️ Example (Lack of Orthogonality):** In C, `structs` can be returned from functions, but `arrays` cannot, even though both are data structures.
    *   **⚠️ Too much orthogonality** can also be a problem, leading to unnecessary complexity from too many possible combinations.
*   **Data Types:** The ability to define adequate data types (e.g., having a `boolean` type instead of using `1` for true) enhances readability.
*   **Syntax Design:**
    *   **Special Words:** The form and meaning of keywords and symbols (e.g., `if`, `for`, `while`) impact readability. Using them as variable names can cause confusion.
    *   **Form and Meaning:** The syntax should clearly suggest the purpose of a statement.

### **Writability**

> 📌 **Definition:** Measures how easily a programming language allows programmers to express their solutions.

*   **Simplicity and Orthogonality:** A good balance is key. Too much simplicity can make complex tasks difficult, while too much orthogonality can lead to overly complex code.
*   **Support for Abstraction:** The ability to define and use complex structures or operations while hiding the implementation details.
*   **Expressivity:** The availability of convenient ways to specify computations (e.g., `count++` is more expressive and shorter than `count = count + 1`).

### **Reliability**

> 📌 **Definition:** A program is reliable if it performs to its specifications under all conditions.

*   **Type Checking:** Testing for *type errors* (applying an operator to an inappropriate operand) at compile time is crucial for reliability.
*   **Exception Handling:** The ability to intercept and handle run-time errors gracefully.
*   **Aliasing:** Allowing a memory cell to be accessed through more than one name. This can be dangerous as it's not always obvious that modifying a value through one name will affect another.
*   **Readability and Writability:** If a program is hard to read, it's hard to verify its correctness. If it's hard to write, mistakes are more likely.

### **Summary Table**

| Characteristic | Readability | Writability | Reliability |
| :--- | :---: | :---: | :---: |
| **Simplicity** | ● | ● | ● |
| **Orthogonality** | ● | ● | ● |
| **Data types** | ● | ● | ● |
| **Syntax design** | ● | | ● |
| **Support for abstraction** | | ● | ● |
| **Expressivity** | | ● | ● |
| **Type checking** | | | ● |
| **Exception handling** | | | ● |
| **Restricted aliasing** | | | ● |

---

# **Module 2: Names, Bindings, and Type Checking**

## **🧠 2.1 Names and Variables**

*   **Name (Identifier):** A string of characters used to identify an entity in a program. The name of a variable is also referred to as an identifier.
*   **Variable:** An abstraction of a computer memory cell or a collection of cells.

### **Special Words: Keywords vs. Reserved Words**

> 📌 **Definition:** Special words are used to make programs more readable by naming actions and separating syntactic parts.

*   **Keyword:** A special word that has meaning only in certain contexts.
*   **Reserved Word:** A special word that cannot be used as a user-defined name (e.g., as a variable name).
*   **❓ Key Distinction:** The two are **not** the same. A word can be one without being the other.
    *   **Example 1:** In Java, `goto` is a **reserved word** (you cannot name a variable `goto`), but it is **not a keyword** (it has no function in the language).
    *   **Example 2:** In Fortran, keywords are not reserved words, meaning you can use them as names (e.g., `IF = 10`).

### **Attributes of a Variable**

1.  **Name:** The identifier used to refer to the variable. (Note: Not all variables have names, e.g., explicit heap-dynamic variables).
2.  **Address (L-value):** The memory address associated with the variable.
3.  **Type:** Determines the range of values the variable can store and the set of operations that can be performed on it.
4.  **Value (R-value):** The contents of the memory cell(s) associated with the variable.
5.  **Lifetime:** The time during which the variable is bound to a specific memory location.
6.  **Scope:** The range of statements in which the variable is *visible* (can be referenced).

## **💡 2.2 Binding**

> 📌 **Definition:** An association between an attribute and an entity, or between an operation and a symbol.

### **Binding Times**

*   **Language Design Time:** When the language is being designed (e.g., binding `*` to multiplication).
*   **Language Implementation Time:** When the compiler/interpreter is written.
    *   **Example 1:** Defining the range of an `int` in C.
    *   **Example 2:** Defining the possible values and internal representation of a `boolean` in Java.
*   **Compile Time:** When the source code is compiled (e.g., binding a variable to a data type in C or Java).
*   **Link Time:** When the compiler links to external libraries (e.g., resolving the address of the `printf` function).
*   **Load Time:** When the program is loaded into memory by the OS (e.g., binding a C `static` variable or the `main` function to a memory cell).
*   **Run Time:** When the program is executing (e.g., binding a non-static local variable to a memory cell or binding a variable to its value).

### **Type Binding**

*   **Static Type Binding:** Occurs before run-time and remains fixed throughout execution.
    *   **Explicit Declaration:** The type is explicitly stated (e.g., `int count = 10;`).
    *   **Implicit Declaration:** The type is determined by context or naming conventions (e.g., `var num = 10;` in C#).
*   **Dynamic Type Binding:** Occurs during run-time and can change during execution. The variable is bound to a type when it is assigned a value.
    *   **✅ Advantage:** High flexibility.
    *   **⚠️ Disadvantage:** Less reliable (errors are harder to detect) and higher run-time cost due to type checking.

### **Storage Binding and Lifetime**

*   **Static Variables:** Bound to memory *before* execution begins and remain bound to the same cell until termination. (e.g., global variables, C `static` variables).
*   **Stack-Dynamic Variables:** Storage is allocated from the run-time stack when their declaration is reached (*elaboration*). (e.g., local variables in subprograms). Allows for recursion.
*   **Explicit Heap-Dynamic Variables:** Nameless memory cells allocated and deallocated by explicit run-time instructions (e.g., `new` and `delete` in C++, `malloc` in C). Accessed via pointers.
*   **Implicit Heap-Dynamic Variables:** Bound to heap storage only when they are assigned values. (e.g., all variables in Python and JavaScript).

## **⭐ 2.3 Scope**

> 📌 **Definition:** The range of statements in which a variable is visible and can be referenced.

*   **Static Scoping (Lexical Scoping):**
    *   The scope of a variable can be determined by examining the source code at **compile time**.
    *   To find the declaration for a referenced variable, the search begins in the local scope and, if not found, proceeds to the enclosing (parent) static scopes.
    *   **Variable Hiding:** A variable in an inner scope *hides* a variable with the same name in an outer scope.
*   **Dynamic Scoping:**
    *   Based on the *calling sequence* of subprograms, not their spatial relationship in the code. Binding depends on the flow of control at **run time**.
    *   To find the declaration for a referenced variable, the search begins in the local scope and, if not found, proceeds to the scope of the subprogram that *called* the current one.
    *   **✅ Advantage:** Convenient for passing parameters implicitly.
    *   **⚠️ Disadvantage:** Difficult to read and trace, less reliable, and slower.

---

# **Module 3: Data Types**

> 📌 **Definition:** A data type is a collection of data objects and a set of predefined operations on those objects. It determines the range of values that can be stored and the set of operations applicable to those values.

## **🧠 3.1 Primitive Data Types**

> 📌 **Definition:** Data types that are not defined in terms of other data types. `Arrays` are not primitive.

| Type | Definition | Example |
| :--- | :--- | :--- |
| **Integer** | Most common numeric type, representing whole numbers. | `10`, `-5` |
| **Floating-point** | Models real numbers as approximations (e.g., IEEE 754 standard). | `3.14`, `-0.01` |
| **Decimal** | For business applications (money), stores a fixed number of decimal digits. | `19.99` |
| **Boolean** | Simplest type, with values of `true` or `false`. | `true` |
| **Character** | Stored as numeric codings (e.g., ASCII, Unicode). | `'A'`, `'b'` |

## **💡 3.2 Character String Types**

*   A sequence of characters.
*   **Operations:** Assignment, comparison, concatenation, substring reference, pattern matching.
*   **String Lengths:**
    *   **Static:** Length is fixed at creation (Python, Java).
    *   **Limited Dynamic:** Can vary up to a declared maximum (C, C++).
    *   **Dynamic:** Can vary with no maximum, providing flexibility at the cost of overhead (JavaScript, Perl).

## **⭐ 3.3 User-Defined Ordinal Types**

*   **Enumeration Types:**
    *   A collection of named constants (enumeration constants).
    *   Improves readability and reliability by using meaningful names instead of coded values.
    *   Example in C#: `enum Days {Mon, Tue, Wed, Thu, Fri, Sat, Sun};`

## **🔧 3.4 Structured Data Types**

> 📌 **Definition:** A data type where each data item is a collection of other data items.

### **Arrays**

*   An aggregate of *homogeneous* data elements where an individual element is identified by its position (index).
*   **Indexing (Subscripting):** The mapping of indices to elements.
*   **Array Categories by Binding:**
    *   **Static:** Subscript ranges and storage are bound before run-time.
    *   **Fixed Stack-Dynamic:** Subscript ranges are static, but allocation is done during run-time on the stack.
    *   **Fixed Heap-Dynamic:** Storage binding is dynamic (from the heap), but the size is fixed after allocation.
    *   **Heap-Dynamic:** Subscript ranges and storage allocation are dynamic and can change any number of times.

### **Records (Structs)**

*   A possibly *heterogeneous* aggregate of data elements where elements are identified by name, not index.
*   Example: A `student` record could contain a `string` for the name and an `int` for the student number.

### **Tuples**

*   Similar to a record, but the elements are not named. They are accessed by position. Python has immutable tuples.

### **Lists**

*   First supported by Lisp. In Python, lists are the implementation of arrays and are very flexible.

### **Unions**

*   A type where a variable is allowed to store different type values at different times, using the *same memory space*.
*   **Free Union (C/C++):** No type checking is performed, which is generally unsafe.
*   **Discriminated Union:** Includes a tag (discriminant) to indicate the type of data currently stored, allowing for type checking.

## **🔗 3.5 Pointer and Reference Types**

*   **Pointer:** A variable whose value is a memory address.
*   **Reference:** Similar to a pointer but often with restricted operations.
*   **Operations:**
    *   **Assignment:** Sets the pointer to an address.
    *   **Dereferencing:** Accesses the value stored at the memory location the pointer points to.
*   **⚠️ Pointer Problems:**
    *   **Dangling Pointers:** A pointer that points to a memory location that has been deallocated.
    *   **Memory Leakage:** A heap-dynamic variable that is no longer accessible to the program (garbage).
*   **Heap Management (Garbage Collection):**
    *   **Reference Counting:** Maintains a count of references to each object. When the count reaches zero, the object is deallocated.
    *   **Mark-Sweep:** A lazy approach where the system periodically marks all reachable objects and then "sweeps" away the unmarked (garbage) objects.

---

# **Module 4: Expressions and Assignment Statements**

## **🧠 4.1 Arithmetic Expressions**

*   Consist of **operators**, **operands**, **parentheses**, and **function calls**.
*   **Operators:**
    *   **Unary:** One operand (e.g., `-x`).
    *   **Binary:** Two operands (e.g., `x + y`).
    *   **Ternary:** Three operands (e.g., the conditional operator `? :` in C).

## **💡 4.2 Operator Evaluation Order**

### **Operator Precedence**

> 📌 **Definition:** Defines the order in which adjacent operators of *different* precedence levels are evaluated.

*   **Typical Order:**
    1.  Parentheses `()`
    2.  Postfix operators (`++`, `--`)
    3.  Unary operators (`+`, `-`)
    4.  Multiplication/Division/Modulus (`*`, `/`, `%`)
    5.  Addition/Subtraction (`+`, `-`)

### **Operator Associativity**

> 📌 **Definition:** Defines the order in which adjacent operators of the *same* precedence level are evaluated.

*   **Left Associativity:** Operators are evaluated from left to right (e.g., `a - b + c` is treated as `(a - b) + c`). Most binary operators are left-associative.
*   **Right Associativity:** Operators are evaluated from right to left (e.g., exponentiation `**` in Ruby, `a ** b ** c` is treated as `a ** (b ** c)`).

## **⭐ 4.3 Operand Evaluation Order & Side Effects**

*   **Operand Evaluation:** The process of fetching values of operands from memory.
*   **Functional Side Effects:**
    *   > 📌 **Definition:** Occurs when a function changes one of its parameters or a global variable.
    *   **⚠️ Problem:** When an expression involves a function with side effects, the result of the expression can depend on the operand evaluation order. For `a + fun(a)`, if `fun(a)` modifies `a`, the result will be different depending on whether `a` is fetched before or after `fun(a)` is called.
*   **Solutions to Side Effects:**
    1.  **Disallow Side Effects:** Prohibit functions from modifying non-local variables or two-way parameters. This is the approach in functional languages but is restrictive for imperative languages.
    2.  **State the Order of Evaluation:** The language definition can fix the operand evaluation order (e.g., Java evaluates operands left-to-right). This prevents ambiguity but can limit certain compiler optimizations.

## **🔧 4.4 Overloaded Operators**

> 📌 **Definition:** The use of a single operator symbol for more than one purpose.

*   **Example:** In Java, `+` is used for both integer/float addition and string concatenation.
*   **Dangers:** Can harm readability if used improperly. For example, in C++, the `&` operator can be a bitwise AND or the address-of operator, which can lead to subtle bugs.

## **📝 4.5 Assignment Statements**

> 📌 **Definition:** Allows the user to dynamically change the binding of a value to a variable.

### **Assignment Operators**

| Language Family | Operator |
| :--- | :---: |
| FORTRAN, BASIC, C-based, Java | `=` |
| ALGOLs, Pascal, Ada | `:=` |

### **Special Assignment Forms**

*   **Conditional Targets:** Some languages, like Perl, allow the target of an assignment to be chosen conditionally.
    *   `($flag ? $count1 : $count2) = 0;` is equivalent to an `if-else` statement that assigns `0` to either `$count1` or `$count2` based on `$flag`.
*   **Compound Assignment Operators:** A shorthand for modifying a variable's value and assigning the new value back to it.
    *   `sum += value;` is equivalent to `sum = sum + value;`.
*   **Unary Assignment Operators:** In C-based languages, `++` and `--` are unary operators that also perform assignment.
    *   `sum = ++count;` (prefix) increments `count` first, then assigns the new value to `sum`.
    *   `sum = count++;` (postfix) assigns the current value of `count` to `sum`, then increments `count`.
*   **Multiple-Target, Multiple-Source:** Allows for assigning multiple values to multiple variables in a single statement.
    *   `($first, $second) = (20, 40);` is supported in languages like Perl, Ruby, and Python.

### **Assignment as an Expression**

*   In C-based languages, assignment statements produce a result (the value being assigned) and can be used as operands in other expressions.
*   **Example:** `while ((ch = getchar()) != EOF) { ... }`
*   **⚠️ Disadvantages:**
    *   **Readability:** Can lead to complex and hard-to-read expressions with side effects, like `a = b + (c = d / b) - 1;`.
    *   **Error Prone:** A common mistake is using `=` (assignment) instead of `==` (equality) in a conditional, like `if (x = y)`. Java and C# mitigate this by requiring boolean expressions in `if` statements.

### **Mixed-Mode Assignment**

> 📌 **Definition:** An assignment where the type of the expression on the right side is different from the type of the variable on the left side.

*   **Coercion:** Many languages allow for automatic type conversion (coercion) in these cases.
*   **Rules vary by language:**
    *   **C, C++, Perl:** Type mixes are legal with coercion freely applied.
    *   **Java, C#:** Only allowed if the coercion is a *widening* conversion (e.g., `int` to `float`). Some *narrowing* conversions are allowed (e.g., `int` to `char`) if the value is in range.
    *   **Functional PLs:** Mixed-mode assignments generally don't exist, as "assignment" is just naming a value, and types must match.

---

# **Module 5: PL Implementation Methods**

## **💡 5.1 The Phases of Compilation**

> 📌 **Definition:** Compilation is the process of translating a program from a high-level source language into an equivalent machine language program.

1.  **Lexical Analyzer:** Gathers characters from the source program into lexical units (lexemes) like identifiers, keywords, operators, and punctuation. It ignores comments and whitespace.
2.  **Syntax Analyzer (Parser):** Takes the lexical units from the lexical analyzer and uses them to construct a parse tree, which represents the syntactic structure of the program according to the language's grammar rules.
3.  **Semantic Analyzer:** Checks the parse tree for semantic errors (errors of meaning). ⭐ **This is the phase where type checking is performed**, ensuring that operands are compatible with their operators, checking for undeclared variables, and matching parameter types.
4.  **Intermediate Code Generator:** Creates a program in an intermediate language, which is between the source language and the machine language. This makes the compilation process more portable.
5.  **Optimizer (Optional):** Improves the intermediate code to make the final program run faster or use less memory.
6.  **Code Generator:** ✅ Translates the (optimized) intermediate code into the final, equivalent machine language program.
