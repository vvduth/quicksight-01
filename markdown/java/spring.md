### Ioc and DI
* Inversion of Control (IoC) and Dependency Injection (DI) are fundamental concepts in the Spring Framework that help manage object creation and dependencies in a flexible and maintainable way.
* IoC is a principle.In Java program it is all about objects, but when we making an app, we should focus on the business logic rather than on creating and managing objects. So, IoC inverts the control of object creation and management from the application code to an external entity (the IoC container). but to use those objects in our application, we need a way to get them from the IoC container. This is where DI comes in.
* Dependency Injection (DI) is a design pattern that implements IoC. It allows the IoC container to inject the required dependencies into an object at runtime, rather than the object creating them itself. This promotes loose coupling and makes the code more testable and maintainable.
### Ioc container
* The IoC container is responsible for instantiating, configuring, and managing the lifecycle of application objects (beans). It uses configuration metadata (XML, annotations, or Java code) to know how to create and wire beans together.
* DI is a design pattern where the control of creating and managing dependencies is inverted from the object itself to an external entity (the IoC container). Instead of an object creating its dependencies, they are injected into it by the container.

### AOP
* Aspect-Oriented Programming (AOP) is a programming paradigm that allows you to separate cross-cutting concerns (like logging, security, transaction management) from the main business logic of your application. In Spring, AOP is used to modularize these concerns into reusable components called aspects.

### Sping and spring boot:
* back then , when there are only spring framework, we need to do a lot of configurations to set up a spring application. We need to create XML files or use annotations to define beans, their dependencies, and other configurations. This can be time-consuming and error-prone.
* Spring Boot simplifies this process by providing a convention-over-configuration approach. It comes with sensible defaults and auto-configuration capabilities that automatically set up many aspects of a Spring application based on the dependencies present in the classpath.
* spring are responsible for creatign obj , these objs are called beans and the container that manages these beans is called IoC container.

### how to implement DI in spring?
* use application context to get the bean
* even tho spring is rsponsible for creating the bean aka object
* we dont want the sping to create all the objs for us
* taht why we need to tell spring which class we want it to create the object for
* by using annotation @Component on the class
* So no more 
  ```java
    Service service = new Service();
    ```
* instead we use
* ```java
    ApplicationContext context = SpringApplication.run(MySpringBootApplication.class, args);
    Service service = context.getBean(Service.class);
  ```
### benefits of DI
* **Loose Coupling**: DI promotes loose coupling between components by relying on abstractions (interfaces) rather than concrete implementations. This makes it easier to change or replace components without affecting the rest of the application.
* **Easier Testing**: DI makes it easier to write unit tests by allowing you to

### autowiring in spring
* one bean can depend on another bean so:
* Autowiring is a feature in Spring that allows the IoC container to automatically resolve and inject dependencies into beans without the need for explicit configuration.
* There are several modes of autowiring in Spring:
* **No Autowiring (default)**: Dependencies are not automatically injected. You need to explicitly define the dependencies in the configuration.
* **By Name**: The container looks for a bean with the same name as the property
  ```java
    @Autowired
    private UserService userService; // looks for a bean named "userService"
    ```

### loose coupling
* Loose coupling is a design principle that promotes reducing dependencies between components in a system. In the context of Spring and DI, loose coupling is achieved by relying on abstractions (interfaces) rather than concrete implementations.