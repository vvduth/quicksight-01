## Q: Can you explain what Hibernate is and why we use it instead of plain JDBC?


**Hibernate** is an **Object-Relational Mapping (ORM) framework** for Java that simplifies database interactions by mapping Java objects to database tables automatically.

## Why Use Hibernate Over Plain JDBC?

**Key advantages:**

1. **Eliminates boilerplate code** - No need to write repetitive SQL queries, manage connections, or handle ResultSet mappings manually

2. **Object-oriented approach** - Work with Java objects instead of SQL queries. Hibernate handles the conversion between objects and database records

3. **Database independence** - Change databases with minimal code changes, as Hibernate generates database-specific SQL automatically

4. **Automatic table generation** - Can create/update database schema based on your entity classes

5. **Built-in features** - Provides caching, lazy loading, transaction management, and connection pooling out of the box

6. **HQL (Hibernate Query Language)** - Write database-independent queries using object-oriented syntax instead of SQL

**In plain JDBC**, you manually write SQL, open/close connections, handle exceptions, and map ResultSet data to objects - which is time-consuming and error-prone. Hibernate automates all of this, letting developers focus on business logic rather than database plumbing.

## Q: Can you explain how you'd save an object in the database using Hibernate?
To save an object in Hibernate, you use the **Session** object. Here's the typical process:

## Steps to Save an Object

1. **Get a Session** from the SessionFactory
2. **Begin a transaction**
3. **Call `session.save()` or `session.persist()`** with your entity object
4. **Commit the transaction**
5. **Close the session**

## Example Code

```java
// Assuming you have an Employee entity class
Employee emp = new Employee();
emp.setName("John Doe");
emp.setSalary(50000);

// Get session from SessionFactory
Session session = sessionFactory.openSession();

// Begin transaction
Transaction tx = session.beginTransaction();

try {
    // Save the object
    session.save(emp);  // or session.persist(emp)
    
    // Commit transaction
    tx.commit();
} catch (Exception e) {
    tx.rollback();
    e.printStackTrace();
} finally {
    session.close();
}
```

**Key points:**
- `save()` returns the generated ID immediately
- `persist()` doesn't return anything but is JPA-standard
- The transaction ensures data consistency
- Hibernate converts the Java object to an SQL INSERT statement automatically

Would you like me to explain how to fetch data back from the database?

## Q: What is the difference between `get()` and `load()` methods in Hibernate?


Yes! Both `get()` and `load()` retrieve objects from the database, but they behave differently:

## Key Differences

| Feature | `get()` | `load()` |
|---------|---------|----------|
| **Database hit** | Hits database immediately | Returns a proxy, hits database only when accessed (lazy loading) |
| **Returns** | Actual object or `null` if not found | Proxy object |
| **Not found** | Returns `null` | Throws `ObjectNotFoundException` when accessed |
| **Use case** | When you're unsure if object exists | When you're confident object exists |

## Example

```java
// get() - immediate database hit
Employee emp1 = session.get(Employee.class, 101);
if (emp1 == null) {
    System.out.println("Employee not found");
}

// load() - returns proxy, lazy loads
Employee emp2 = session.load(Employee.class, 102);
// No database hit yet!
System.out.println(emp2.getName()); 
// Database hit happens HERE when you access a property
```

**In practice:** Use `get()` when you need to check if the record exists. Use `load()` when you're certain it exists and want better performance through lazy loading (like setting foreign key relationships).


## Q: How does Hibernate handle transactions?
Hibernate manages transactions through its **Transaction API**, which provides a way to group multiple operations into a single unit of work. This ensures data integrity and consistency.


## Q: Could you describe how you would define a OneToMany relationship using Hibernate annotations?
Great question! A **OneToMany relationship** means one entity has multiple related entities. For example, one **Department** has many **Employees**.

## Example: Department (One) → Employees (Many)

### Parent Entity (Department)

```java
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String name;
    
    // OneToMany relationship
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();
    
    // Getters and setters
    public List<Employee> getEmployees() {
        return employees;
    }
    
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
```

### Child Entity (Employee)

```java
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String name;
    private double salary;
    
    // ManyToOne relationship (inverse side)
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    // Getters and setters
    public Department getDepartment() {
        return department;
    }
    
    public void setDepartment(Department department) {
        this.department = department;
    }
}
```

## Key Annotations Explained

- **`@OneToMany`** - Defines the one-to-many side (Department)
- **`mappedBy`** - Points to the field in the child entity that owns the relationship
- **`@ManyToOne`** - Defines the many-to-one side (Employee)
- **`@JoinColumn`** - Specifies the foreign key column name in the child table
- **`cascade = CascadeType.ALL`** - Operations on parent cascade to children (optional but useful)

This setup creates a `department_id` foreign key in the employees table automatically!