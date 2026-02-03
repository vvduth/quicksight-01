## @Model attribute Annotation in Spring Web
* what does it do? 
  - The `@ModelAttribute` annotation in Spring Web is used to bind a method parameter or method return value to a named model attribute, which is then exposed to a web view. It can be applied at the method level or on method parameters.
  
```java
@Controller
public class UserController {

    @ModelAttribute("user")
    public User populateUser() {
        return new User();
    }

    @GetMapping("/userForm")
    public String showForm(@ModelAttribute("user") User user) {
        return "userForm";
    }
}
```

## dispatch servlet in spring web
* what does it do?
  - The DispatcherServlet is the front controller in the Spring Web MVC framework. It handles all incoming HTTP requests and delegates them to appropriate handlers (controllers) for processing. It also manages the view resolution and rendering of responses.

```java
@WebServlet(name = "dispatcher", urlPatterns = "/")
public class MyDispatcherServlet extends DispatcherServlet {
    // Custom configurations can be added here
}
```
* to set up dispatcher servlet in web.xml
```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
```
* serverlet-mapping: 

```xml
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

* what does servlet-mapping do?
  - The `<servlet-mapping>` element in the `web.xml` file maps a specific URL pattern to a servlet. In this case, it maps all incoming requests (indicated by the `/` pattern) to the DispatcherServlet, allowing it to handle all requests for the application.
  
## internal resource view resolver in spring web
* what does it do?
* The InternalResourceViewResolver is a view resolver in Spring Web MVC that resolves logical view names to actual JSP files or other resources within the web application. It helps in rendering views by prefixing and suffixing the view names with specified paths.

```java
@Bean
public InternalResourceViewResolver viewResolver() {
    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setSuffix(".jsp");
    return resolver;
} 
```

### without sprng boot
* ina spring mcv app on tomcat, which component first get every http req and route it to controllers
  - The DispatcherServlet is the component that first receives every HTTP request and routes it to the appropriate controllers in a Spring MVC application running on Tomcat.
* where to map request to dispatcher servlet
  - You map requests to the DispatcherServlet in the `web.xml` file using the `<servlet-mapping>` element.
  - in web.xml, servlet name in both servlet and servlet-mapping must match to let dispatcher servlet handle all req
    - The `<url-pattern>` in the `<servlet-mapping>` element must match the desired URL patterns (e.g., `/`) to let the DispatcherServlet handle all requests.