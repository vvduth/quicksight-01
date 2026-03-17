## what is rest?
REST (Representational State Transfer) is an architectural style for designing networked applications. It relies on a stateless, client-server communication protocol, typically HTTP. RESTful services use standard HTTP methods (GET, POST, PUT, DELETE) to perform operations on resources identified by URIs (Uniform Resource Identifiers).

## Path variables
* @PathVariable is an annotation in Spring that indicates that a method parameter should be bound to a URI template variable. It is commonly used in RESTful web services to extract values from the URI and pass them as method parameters. For example, if you have a URI like `/users/{id}`, you can use `@PathVariable` to extract the `id` value and use it in your method.


## @responsebody
* @ResponseBody is an annotation in Spring that indicates that the return value of a method should be written directly to the HTTP response body, rather than being interpreted as a view name. It is commonly used in RESTful web services to return data in formats like JSON or XML.
## @RequestBody
* @RequestBody is an annotation in Spring that indicates that a method parameter should be bound to the body of the HTTP request. It is commonly used in RESTful web services to receive data in formats like JSON or XML, which can then be deserialized into Java objects for processing.