## **Updated SQL Writing Exam Reviewer (HRDB Focused)**

This reviewer is now specifically designed for your exam on **October 15th**. It uses the official `HRDB` database for all examples and adheres to the formatting rules mentioned in your exam instructions.

### **Part 1: Summary of Key Topics**

Based on your exam's coverage (SQL Writing Levels 1-5) and your instructor's hints, these are the essential topics to master.

*   **[ ] Data Retrieval, Filtering, and Sorting**
    *   `SELECT`: Choosing specific columns.
    *   `FROM`: Specifying the source table(s).
    *   `AS`: Creating aliases for columns.
    *   `DISTINCT`: Getting unique values.
    *   `WHERE`: Filtering rows using operators (`=`, `>`, `<`, `>=`, `<=`, `!=`, `AND`, `OR`, `NOT`, `BETWEEN`, `IN`, `LIKE`).
    *   `ORDER BY`: Sorting results (`ASC`, `DESC`).

*   **[ ] Joining Multiple Tables**
    *   `JOIN...ON`: The standard `INNER JOIN` to combine rows from tables based on a matching key.
    *   `LEFT JOIN...ON`: To include all records from the left table, even if there are no matches in the right table.

*   **[ ] Aggregating and Grouping Data**
    *   Aggregate Functions: **`COUNT()`**, **`AVG()`**, `SUM()`, `MAX()`, `MIN()`.
    *   `GROUP BY`: Grouping rows to perform calculations on each group.
    *   `HAVING`: Filtering the results *after* grouping, based on an aggregate value.

*   **[ ] Important Functions (Based on Instructor Hints)**
    *   String Functions: **`CONCAT()`** (to combine strings, like first and last names).
    *   Date & Time Functions: **`YEAR()`** (to extract the year from a date), **`NOW()`** (to get the current date and time), **`TIMESTAMPDIFF()`** (to calculate the difference between two dates).
    *   Formatting Functions: **`FORMAT()`** (to format numbers with commas or decimal places).

*   **[ ] Subqueries**
    *   Using nested `SELECT` statements, often with `IN` or `NOT IN`, to perform complex, multi-step filtering. Your instructor specifically mentioned **"Joining subqueries,"** which means you might see a subquery in the `FROM` clause.

***

### **Part 2: In-Depth Lessons with HRDB Code Examples**

This is your hands-on practice section. All examples use the **`HRDB` database** and follow your exam's strict formatting rules: **CAPS for keywords**, **lowercase for names**, and **each clause on a new line**.

#### **Sample Database: HRDB Schema**

*   **`employees`**: Contains employee details like `employee_id`, `first_name`, `last_name`, `salary`, `hire_date`, `job_id`, and `department_id`.
*   **`departments`**: Lists department names and their `department_id`.
*   **`jobs`**: Contains `job_id` and `job_title`.
*   **`locations`**: Contains physical addresses for departments.
*   **`job_history`**: Records of past jobs held by employees.

---

#### **Lesson 1: Retrieval, Filtering, and Key Functions**

**Scenario: The HR department needs specific reports on employee data.**

*   **Example 1: Get the full name, job ID, and hire date of all employees, sorting by the most recently hired.**

```sql
SELECT
    first_name,
    last_name,
    job_id,
    hire_date
FROM
    employees
ORDER BY
    hire_date DESC;
```

*   **Example 2: Create a "directory" of employees, showing their full name as a single column, their email, and their annual salary formatted with two decimal places.**

```sql
-- Use CONCAT() to combine strings.
-- Use FORMAT() to format numbers.
SELECT
    CONCAT(first_name, ' ', last_name) AS full_name,
    email,
    FORMAT(salary, 2) AS formatted_salary
FROM
    employees;
```

*   **Example 3: Find all employees hired in the year 1987 who are in the 'SA_REP' job.**

```sql
-- Use the YEAR() function to extract the year from the hire_date.
-- Use AND to combine multiple conditions.
SELECT
    first_name,
    last_name,
    hire_date,
    job_id
FROM
    employees
WHERE
    YEAR(hire_date) = 1987
    AND job_id = 'SA_REP';
```

*   **Example 4: Calculate the number of years each employee has been with the company.**

```sql
-- Use TIMESTAMPDIFF() to find the difference between two dates.
-- The first argument is the unit (YEAR, MONTH, DAY).
SELECT
    first_name,
    last_name,
    hire_date,
    TIMESTAMPDIFF(YEAR, hire_date, NOW()) AS years_of_service
FROM
    employees;
```

---

#### **Lesson 2: Joining Tables (`JOIN...ON`)**

**Scenario: Management needs reports that combine information from different HR tables.**

*   **Example 1: `JOIN` - List all employees and the name of the department they work in.**

```sql
-- We join employees and departments on their common column, department_id.
SELECT
    e.first_name,
    e.last_name,
    d.department_name
FROM
    employees e
JOIN
    departments d ON e.department_id = d.department_id;
```

*   **Example 2: `LEFT JOIN` - List ALL departments and the full name of the manager for each department. Show departments even if they don't have a manager.**

```sql
-- A LEFT JOIN ensures all departments are listed.
-- If a department has no manager, the employee name will be NULL.
SELECT
    d.department_name,
    CONCAT(e.first_name, ' ', e.last_name) AS manager_name
FROM
    departments d
LEFT JOIN
    employees e ON d.manager_id = e.employee_id;
```

*   **Example 3: Multi-Table Join - Get the first name, last name, job title, and department name for every employee.**

```sql
-- Chain multiple JOINs to connect employees -> jobs and employees -> departments.
SELECT
    e.first_name,
    e.last_name,
    j.job_title,
    d.department_name
FROM
    employees e
JOIN
    jobs j ON e.job_id = j.job_id
JOIN
    departments d ON e.department_id = d.department_id;
```

---

#### **Lesson 3: Aggregating & Grouping (`GROUP BY`, `HAVING`)**

**Scenario: The finance department needs summary data about salaries and department sizes.**

*   **Example 1: `COUNT` and `GROUP BY` - Show the number of employees in each department.**

```sql
SELECT
    d.department_name,
    COUNT(e.employee_id) AS number_of_employees
FROM
    employees e
JOIN
    departments d ON e.department_id = d.department_id
GROUP BY
    d.department_name
ORDER BY
    number_of_employees DESC;
```

*   **Example 2: `AVG` and `GROUP BY` - Calculate the average salary for each job title.**

```sql
SELECT
    j.job_title,
    AVG(e.salary) AS average_salary
FROM
    employees e
JOIN
    jobs j ON e.job_id = j.job_id
GROUP BY
    j.job_title;
```

*   **Example 3: `HAVING` - Show only the departments that have more than 10 employees.**

```sql
-- HAVING is used to filter the results of an aggregate function like COUNT().
SELECT
    d.department_name,
    COUNT(e.employee_id) AS number_of_employees
FROM
    employees e
JOIN
    departments d ON e.department_id = d.department_id
GROUP BY
    d.department_name
HAVING
    number_of_employees > 10;
```

---

#### **Lesson 4: Subqueries**

**Scenario: Answering complex questions that require finding a list of items first.**

*   **Example 1: Subquery with `IN` - Find the names of all employees who work in the 'IT' department.**

```sql
-- The inner query first finds the department_id for 'IT'.
-- The outer query then finds all employees with that ID.
SELECT
    first_name,
    last_name
FROM
    employees
WHERE
    department_id IN (
        SELECT
            department_id
        FROM
            departments
        WHERE
            department_name = 'IT'
    );
```

*   **Example 2: Subquery with `NOT IN` - Find all departments that have no employees.**

```sql
-- The inner query gets a list of all department_ids that have employees.
-- The outer query then finds departments whose ID is NOT IN that list.
SELECT
    department_name
FROM
    departments
WHERE
    department_id NOT IN (
        SELECT DISTINCT
            department_id
        FROM
            employees
        WHERE
            department_id IS NOT NULL
    );
```

***

### **Part 3: Curated YouTube Video Resources**

These videos are excellent for reinforcing the specific functions and concepts for your exam.

#### **1. SQL `CONCAT()` Function**
*   **Title:** SQL CONCAT() Function
*   **Source:** Darshil Parmar
*   **Why it's good:** A very quick, to-the-point video showing exactly how to combine columns.
*   **Link:** `https://www.youtube.com/watch?v=u-s0b8-1-cQ`

#### **2. SQL Date Functions (`YEAR`, `TIMESTAMPDIFF`)**
*   **Title:** SQL Date Functions
*   **Source:** Joey Blue
*   **Why it's good:** This video covers the most important date and time functions, including `TIMESTAMPDIFF` and `YEAR`, with clear examples.
*   **Link:** `https://www.youtube.com/watch?v=CzS7sZl5A-Q`

#### **3. SQL Joins (A Deeper Dive)**
*   **Title:** SQL Joins Explained
*   **Source:** freeCodeCamp.org
*   **Why it's good:** A great visual explanation of how different joins work, which is crucial for your exam.
*   **Link:** `https://www.youtube.com/watch?v=9_bK1qI-t4A`

#### **4. SQL Subqueries**
*   **Title:** SQL Subqueries Tutorial for Beginners
*   **Source:** TechTFQ
*   **Why it's good:** This gives practical, real-world examples that make it easy to understand when and why you would use a subquery.
*   **Link:** `https://www.youtube.com/watch?v=2n8-hM9ih0s`

***

### **Final Exam Tips**

*   **Formatting is Key:** Remember the rules! **CAPS** for keywords (`SELECT`, `FROM`), **lowercase** for names (`employees`, `first_name`), and each clause on a **new line**.
*   **Read Carefully:** Identify exactly which columns the question asks for. Don't add extra columns.
*   **Plan Your Query:** Before writing, think about the steps:
    1.  Which tables do I need? (`FROM` and `JOIN`)
    2.  How do I filter the rows? (`WHERE`)
    3.  Do I need to group or summarize? (`GROUP BY`, `COUNT`, `AVG`)
    4.  Which columns should I display? (`SELECT`)
    5.  How should it be sorted? (`ORDER BY`)

You are well-prepared. Take your time, apply these concepts, and you will do great. Good luck
