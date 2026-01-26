### IoC and DI: The "Who am I?" of Spring
- **Inversion of Control (IoC)** and **Dependency Injection (DI)** are the bread and butter of the Spring Framework. They help you manage object creation and dependencies without losing your mind.
- IoC is a principle. In a typical Java program, you're the boss of your objects. But in a Spring app, we should focus on the business logic instead of micromanaging object lifecycles. IoC flips the script: it inverts control from your code to an external entity—the IoC container. Think of it as a personal assistant who handles all your baggage.
- **Dependency Injection (DI)** is the design pattern that makes IoC actually happen. It allows the IoC container to "inject" required dependencies into an object at runtime. Instead of the object frantically looking for its friends, the container just introduces them. This promotes loose coupling and makes your code way easier to test (and brag about).

### The IoC Container: The Wizard Behind the Curtain
- The IoC container is the mastermind responsible for instantiating, configuring, and managing the lifecycle of application objects (known as **beans**). It reads configuration metadata (XML, annotations, or Java code) and wires everything together.
- To recap: DI is the pattern where the container does the heavy lifting of dependency management, and IoC is the concept of giving up that control in the first place.

### AOP: Cutting Through the Noise
- **Aspect-Oriented Programming (AOP)** lets you separate cross-cutting concerns—like logging, security, or transaction management—from your actual business logic. In Spring, we modularize these into reusable components called **aspects**. It's like having a dedicated cleaning crew so you can just focus on the party.

### Spring vs. Spring Boot: The Evolution
- In the "old days" of pure Spring, you had to write mountains of configuration. We’re talking endless XML files or annotations just to get a single bean to say "hello." It was time-consuming, error-prone, and slightly soul-crushing.
- **Spring Boot** saved the day with a "convention-over-configuration" approach. It comes with sensible defaults and auto-configuration magic that sets up your app based on the dependencies in your classpath. 
- In short: Spring creates the objects (**beans**), and the **IoC container** manages them. Spring Boot just makes sure you don't have to explain every single detail to the container.

### How to Implement DI in Spring?
- You use the **ApplicationContext** to get your beans.
- Even though Spring is responsible for bean creation, we don’t want it creating *every* object in existence (that would be chaos).
- We tell Spring which classes it should handle by using the `@Component` annotation.
- So, say goodbye to:
  ```java
  Service service = new Service();
  ```
- And say hello to:
  ```java
  ApplicationContext context = SpringApplication.run(MySpringBootApplication.class, args);
  Service service = context.getBean(Service.class);
  ```

### Benefits of DI
- **Loose Coupling**: DI helps components stay chill by relying on abstractions (interfaces) rather than concrete implementations. This makes it a breeze to swap components without breaking the whole world.
- **Easier Testing**: DI makes unit testing much simpler because you can easily mock dependencies instead of dealing with heavy real-world objects.

### Autowiring: The Automated Matchmaker
- Beans often depend on other beans. **Autowiring** is a Spring feature that lets the container automatically resolve and inject those dependencies without you having to lift a finger.
- There are several modes of autowiring in Spring:
  - **No Autowiring (default)**: You do it manually. Boring!
  - **By Name**: The container searches for a bean with the same name as the property.
    ```java
    @Autowired
    private UserService userService; // looks for a bean named "userService"
    ```

### Loose Coupling: Staying Independent
- Loose coupling is all about reducing dependencies between components. In Spring, we achieve this by pointing our code at interfaces rather than hardcoding specific classes. It’s the coding equivalent of "it's not you, it's my interface."

### Spring Bean XML Configuration (The Vintage Way)
- While annotations are the cool new thing, Spring still supports XML for those who appreciate the classics (or updated legacy apps).
- **alien.xml**
  ```xml
  <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd">
      <bean id="alien" class="org.example.Alien">
      </bean>
  </beans>
  ```
- **MainApp.java**
  ```java
  // bean xml configuration
  ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  Alien alien = (Alien) context.getBean("alien");
  alien.code();
  ```
- When you initialize the container with `ClassPathXmlApplicationContext`, Spring creates the objects defined in the XML. You just fetch them via `getBean()`.

### Singleton vs. Prototype: One or Many?
- If you run this:
  ```java
  // bean xml configuration
  ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  Alien alien = (Alien) context.getBean("alien");
  alien.code();

  Alien alien2 = (Alien) context.getBean("alien");
  System.out.println(alien == alien2); // true
  ```
- Even if you call `getBean("alien")` multiple times, you get the same instance. By default, Spring beans are **Singleton**. alien and alien2 both refer to the same object in memory. 
- If you want a fresh object every single time, set the scope to **Prototype** in the XML:
  ```xml
  <bean id="alien" class="org.example.Alien" scope="prototype">
  </bean>
  ```
- Prototype beans are only created when you call `getBean()`, not at container startup.

### Setter Injection (XML)
- Injecting primitive types:
  ```xml
  <bean id="alien" class="org.example.Alien">
      <property name="age" value="450"></property>
  </bean>
  ```

### Setter Injection (XML - Continued)
- Injecting object references (the `ref` attribute):
  ```xml
  <bean id="alien" class="org.example.Alien">
      <property name="age" value="450"></property>
      <property name="laptop" ref="laptop"></property>
  </bean>
  <bean id="laptop" class="org.example.Laptop"></bean>
  ```

### Constructor Injection (XML)
- For when you want to set things up right from the start:
  ```xml
  <bean id="alien" class="org.example.Alien">
      <constructor-arg ref="laptop" index="1"/>
      <constructor-arg value="129" index="0"/>
  </bean>
  ```

### Autowiring (XML Style)
- You can automate the wiring in XML too:
  ```xml
  <bean id="alien" class="org.example.Alien" autowire="byName">
      <property name="age" value="450"></property>
  </bean>
  <bean id="laptop" class="org.example.Laptop"></bean>
  ```

### DI Summary
1.  **Constructor Injection**: Dependencies go through the constructor.
2.  **Setter Injection**: Dependencies go through public setters after instantiation.
3.  **Field Injection**: Direct injection into fields via `@Autowired` (easy, but controversial).
4.  **Result**: Cleaner code, Single Responsibility Principle compliance, and a codebase that doesn't care how its dependencies were born.

### The Primary Bean: The Favorite Child
- If you have multiple beans of the same type, Spring gets confused. Use `@Primary` to tell it which one to pick by default.
- In XML:
  ```xml
  <bean id="alien1" class="org.example.Alien" primary="true">
      <property name="age" value="450"></property>
  </bean>
  <bean id="alien2" class="org.example.Alien">
      <property name="age" value="300"></property>
  </bean>
  ```

### Lazy Init: The Procrastinator
- By default, Spring creates all singleton beans at startup. If you want to delay this until the bean is actually needed (procrastination at its finest!), use `lazy-init="true"`.
  ```xml
  <bean id="desktop" class="org.example.Desktop" lazy-init="true"></bean>
  ```
- Note: If a non-lazy bean depends on a lazy bean, the lazy one will be forced to wake up early.

### Inner Beans: Keeping it Private
- An inner bean is defined inside another bean. It’s perfect for dependencies that don't need to be shared with anyone else—they're private property!
  ```xml
  <bean id="alien" class="org.example.Alien" autowire="byName">
      <property name="age" value="450"/>
      <property name="device">
          <bean id="laptop" class="org.example.Laptop"></bean>
      </property>
  </bean>
  ```

### Component Stereotype Annotations
- `@Component`: The generic "Hey Spring, manage this!" annotation.
- `@Service`: For your business logic (the brains of the operation).
- `@Repository`: For data access (the memory bank).

### Primary vs. Qualifier
- `@Qualifier` beats `@Primary`. If you use both, the qualifier wins every time. It’s more specific!

### Scope and Value Annotations
- `@Scope`: Defines if a bean is a Singleton or Prototype.
- `@Value`: Injects values into fields from properties files or environment variables. No more hardcoding constants!

### Extras
- The `@Configuration` annotation marks a class as a source of bean definitions. It tells Spring, "I've got some `@Bean` methods here you might want to look at."
 a 30-minute Google Meet call (in English) with Hang (Kaylin) Nguyen
Senior Talent Acquisition Partner to learn more about your experience and share what they are building at Nimble

a 30-minute Google Meet call (in English) with Hang (Kaylin) Nguyen - Senior Talent Acquisition Partner to learn more about your experience and share what they are building at Nimble. we aim to create a comfortable, conversational atmosphere while asking a mix of skills-based and personality questions. Our goal is to assess both your fit with the team and your readiness for the role. Just come ready to discuss my experience and share how I approach real-world challenges. focus on practical, intermediate-level questions that allow you to showcase your expertise and familiarity with current technologies and best practices.