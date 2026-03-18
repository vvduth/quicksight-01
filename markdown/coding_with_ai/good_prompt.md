## how to write a good prompt
* a good prompt should be clear, concise, and specific.


example of bad prompt>
* build me a to do app in react
   why it is bad?
   * too vague - what feature do you want?
   * no technical requirement - do you want to use typescript? do you want to use redux?
   * no ux/ui requirement - do you want to use material ui? do you want to use tailwind?
   * ai has to guess everything, which can lead to unexpected results and a lot of back and forth.
  
* better prompt: 
* build me a to do app in react with the following requirements:

feature:
* user can add, edit, delete, and mark tasks as complete.
* filter tasks by all, active, and completed.
* local storage to persist tasks.
* drag and drop to reorder tasks. 
  
technical requirement:
* use typescript.
* react with functional components and hooks.
* tailwind for styling.
* responsive design for mobile and desktop.

UI:
* clean and minimalist design.
* use checkboxes for marking tasks as complete.
* inline editing for task names.

## example 2: api integration
* bad: add a waether api to my app
   why it is bad?
   * too vague - which weather api do you want to use? openweathermap? weatherapi? accuweather?
   * what data should it show?
   * where in the app does it go?
   * no error handking method
* better: integrate the openweathermap api to my react app.
requirements:
- create a weather service.ts file for api calls.
- fetch current weather and 5 day forecast for a given city.
- include proper error handling for failed requests.
- add loading state while fetching data.
- display weather temperature, humidity, and weather conditions in a clean and user-friendly format.
api details:
- use openweathermap api , i have a key
- endpoint needed: current weather and forecast
- handle both sucessful and error responses gracefully, showing appropriate messages to the user.

show me the service file first then we will integrate it into the component.