### JSP:
* JavaServer Pages (JSP) is a technology that helps software developers create dynamically generated web pages based on HTML, XML, or other document types.
* servlet accept request , send data to jsp, jsp render data to html
* howvere java is OOP, so we have MVC pattern to sepresentation and business logic
* client request -> controller(servlet) 
* depends on what type of req (i.e get db from data) => model (java class) => view 

## what is MVC pattern
* Model-View-Controller (MVC) is a design pattern that separates an application into three main components: Model, View, and Controller.
* Model: Represents the data and business logic of the application. It encapsulates the application's state and provides methods to manipulate that state.
* View: Represents the presentation layer of the application. It is responsible for displaying the data provided by the Model to the user. In web applications, views are often implemented using technologies like JSP, Thymeleaf, or HTML/CSS.
* Controller: Acts as an intermediary between the Model and the View. It handles user input, processes requests, and determines which view to render based on the application's state.