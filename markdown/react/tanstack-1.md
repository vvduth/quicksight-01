# Data fetching with tanstack query

**Author:** Duc Thai

**Email:** ducthai060501@gmail.com

## Overview

Here’s what we’re here to do today:

**Goal:**  
Learn how to use the `tanstack-query` library to send HTTP requests from a React application and manage server state efficiently.

**What we’ll cover:**
- **Fetching:** How to retrieve data from an API using tanstack-query.
- **Mutation:** How to send data or perform updates via HTTP requests (like POST, PUT, DELETE).
- **Configuration:** Setting up tanstack-query in your React project, including basic options and advanced features.
- **Cache Validation:** How tanstack-query automatically caches responses and keeps data up-to-date.
- **Optimistic Updating:** How to update the UI immediately before getting a server response, making your app feel faster and more responsive.
- **And more:** Best practices for error handling, refetching, and using dev tools.

By the end, you’ll know how to harness tanstack-query for efficient, user-friendly HTTP interactions in your React apps!

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qrjHR0cG6gjiQSLxEToX4su7hyD5dKwYVn9RP" alt="tanstck.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

## Steps

### Getting starter project

In this step, I am getting a starter project for our training. The starter project is a full-stack events management app found at [https://github.com/vvduth/tanstack-project](https://github.com/vvduth/tanstack-project).

**Project Setup Highlights:**

- **Frontend:**  
  - Built with React 19, Vite, and React Router v6  
  - Runs on Vite’s default dev server (port 5173)  
  - Uses nested routing for its single-page app structure

- **Backend:**  
  - Express.js REST API (runs on port 3000)  
  - Stores data in file-based JSON files (no real database)  
  - Also serves static images from a public folder

- **Communication:**  
  - The React frontend uses fetch requests to communicate with the backend (calls to `http://localhost:3000`)  
  - CORS is enabled to allow communication between the frontend and backend servers

**Why?**  
The backend acts as a dummy API, making it perfect for practicing frontend data fetching, mutations, and working with Tanstack Query. This setup lets me experiment with how a real React app fetches, mutates, and displays data—while keeping everything simple and local.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4q4KTfiMapNo6aJFDVPzky8RUCQXY0ZilSTLM9" alt="snip tool.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Start development

In this step, I am setting up both the backend and frontend servers for the starter project:

**1. Backend Setup**  
- Navigate to the `backend` folder.  
- Run `npm i` to install backend dependencies.  
- Run `npm start` to launch the Express server.  
- You should see:  
  ```
  Server running on port 3000
  ```

**2. Frontend Setup**  
- Open a new terminal window/tab.  
- Go to the project's root folder.  
- Run `npm i` to install frontend dependencies.  
- Start the development server with `npm run dev`.  
- You should see something like:  
  ```
  VITE v4.5.14  ready in 335 ms
  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
  ➜  press h to show help
  ```

**Result:**  
Both servers are now running—the backend API on port 3000 and the React frontend on port 5173. This sets up your development environment so you can test connections, fetch data, and start building features!

### React/tanstack Query? What and why

Here’s a clear, beginner-friendly explanation:

---

## What is React TanStack Query? Why would you use it?

**React TanStack Query** is a library that helps you send HTTP requests and keeps your frontend UI in sync with server-side data.

### Why use TanStack Query?
- You _can_ fetch data yourself using tools like `useEffect` and `fetch`. For example, in your starter project’s `NewEventsSections.jsx`, you might see something like:

  ```javascript
  const [data, setData] = useState();
  const [error, setError] = useState();
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    async function fetchEvents() {
      setIsLoading(true);
      const response = await fetch('http://localhost:3000/events');

      if (!response.ok) {
        const error = new Error('An error occurred while fetching the events');
        error.code = response.status;
        error.info = await response.json();
        throw error;
      }

      const { events } = await response.json();
      return events;
    }

    fetchEvents()
      .then((events) => setData(events))
      .catch((error) => setError(error))
      .finally(() => setIsLoading(false));
  }, []);
  ```

- **But TanStack Query simplifies your code and your life as a developer!**
    - Handles loading and error states for you, so you write less boilerplate
    - Provides built-in caching, so data is available instantly and doesn’t refetch unnecessarily
    - Keeps your UI updated in real time when your data changes
    - Makes it much easier to implement advanced features, like pagination, background updates, optimistic UI, and refetching on interval or focus

**Summary:**  
While you don’t _need_ TanStack Query to fetch and display data, it gives you many powerful features for “free” and shrinks the amount of code you need to write. This helps keep your app fast, reliable, and easy to maintain.

### what feature I want to achieve with tanstack in this project?


Here’s a clear statement of the feature you want to achieve with TanStack Query in this project:

---

**Features I want to achieve with TanStack Query:**

1. **Automatic data refetching on page revisit:**  
   - When I navigate away from a page (like the events list), and then come back, I want the app to automatically refetch the data for that view. This keeps the UI up-to-date with the latest information—without manual refresh or extra code.

2. **Caching fetched data in memory:**  
   - After the app fetches data for a page, I want that data to be cached and stored in memory.  
   - If I visit another page (e.g. event details), and then come back to the events list, the app should first show the cached data instantly for a fast experience, and then silently fetch the latest data in the background.

**Why TanStack Query?**  
TanStack Query makes both these behaviors easy and automatic. The current approach (with useEffect and fetch) doesn’t provide refetching when navigating back, and doesn’t handle caching out-of-the-box. TanStack Query does both—making your UI snappy, always up-to-date, and much easier to maintain!

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qbx5NnaYzC0UbepwGZEqnOvQ5go6BkMrPTW1t" alt="localhost.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Let's get going

Here’s a summary of what we are doing in this step:

---

**Goal:**  
Refactor the `NewEventsSection` component to use TanStack Query (`@tanstack/react-query`) for fetching event data, removing manual state management and useEffect/fetch logic.

**Step-by-step:**
1. **Install Dependency:**  
   Run `npm i @tanstack/react-query` to add TanStack Query.

2. **Move Fetch Logic:**  
   - Cut out the existing `async function fetchEvents` from `NewEventsSection.jsx`.
   - Place it in `src/utils/http.js` and export it, so you can reuse it.

3. **Remove Manual State and useEffect:**  
   - Delete all manual state management (`useState`) and `useEffect` from `NewEventsSection.jsx`.

4. **Use TanStack’s useQuery Hook:**  
   - Import `{ useQuery }` from `@tanstack/react-query`.
   - Set up the hook in your component:
     ```js
     const { data, isPending, isError, error } = useQuery({
       queryKey: ['events'],         // Unique key to identify/caches this query
       queryFn: fetchEvents,         // The function to actually fetch data (from utils/http.js)
     });
     ```
   - `data` holds your event data if the request is successful.
   - `isPending` (or `isLoading` in some versions) is `true` while the request is ongoing.
   - `isError` is `true` if an error is thrown from your fetch function.
   - `error` gives direct access to the error object.

5. **Adjust the error block:**  
   Update your error rendering to:
   ```js
   if (error) {
     content = (
       <ErrorBlock title="An error occurred" message={
         error.info?.message || "Failed to fetch events."
       }/>
     );
   }
   ```

---

**Result:**  
After these steps, I expected my data fetching to be much simpler and caching to be handled automatically. However, I ran into an unexpected application error: "No QueryClient set, use QueryClientProvider to set one." This error occurs because TanStack Query requires a QueryClient instance to be provided at the top level of the app using QueryClientProvider. It's a common setup step that I missed, but that's okay—I can easily fix it by wrapping my app with QueryClientProvider and passing in a QueryClient instance.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qv3JEG6KzsfPtlpKmiZSO0rUQ5ChuLkN6XW3R" alt="error.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Wrapp the app in the stanstack provider

In this step, I’m updating the `App.jsx` file in the `src` folder to properly set up React Query. I add:

```js
import { QueryClientProvider, QueryClient } from '@tanstack/react-query';

// General config for React Query
const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  );
}
```

With this setup, my React Query hooks now have access to the QueryClient, and everything works as expected. If I open the dev tools and watch the network tab, I can see that when I navigate away from a page and come back, React Query automatically refetches the data for me. This confirms that my caching and refetching features are working!

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qSJGKM4eJUaAolstIYiwR4jLvrEQ0XqWpO95C" alt="network.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Explore caching and stale data

Currently, in my app, when I navigate to a different page and then come back, all the data is fetched again. Since everything is running locally, the requests feel almost instant. However, if I throttle the network using dev tools and repeat the same flow, the data still appears instantly when I return to a page. This demonstrates TanStack Query's caching abilities: even though it sends a network request in the background, it always reuses the cached data immediately whenever the same useQuery hook with the same queryKey is encountered. The UI stays fast by showing cached data right away, while TanStack Query updates it silently in the background—offering the best of both worlds: instant display with automatic freshness.

I also learned that you can control this query behavior through configuration options:

- `staleTime`: Controls how long fetched data is considered “fresh.” For example, setting `staleTime: 1000 * 60` (one minute) tells React Query to wait for one minute before sending another refetch request.
- `gcTime`: Controls how long cached data should be kept in memory after it becomes stale. For example, `gcTime: 1000 * 60 * 5` (five minutes) will keep the data for five minutes before garbage collecting it.

These options give you fine-grained control over your app’s network usage, caching, and how responsive the UI feels to users.

### Dynamic query functions and Query keys

In this step, I’m working on the `FindEventSections.jsx` component, which acts as a search bar for users to find events by entering and submitting a search term. Previously, the form didn’t do anything and lacked support for searching.

First, I update the `fetchEvents` function in `src/utils/http.js` so it can now accept a search term as a parameter. If a search term is provided, it appends it as a query parameter to the backend URL:

```js
export async function fetchEvents(searchTerm) {
  let url = 'http://localhost:3000/events';
  if (searchTerm) {
    url += `?search=${encodeURIComponent(searchTerm)}`;
  }
  const response = await fetch(url);
  if (!response.ok) {
    const error = new Error('An error occurred while fetching the events');
    error.code = response.status;
    error.info = await response.json();
    throw error;
  }
  const { events } = await response.json();
  return events;
}
```

Next, I update `FindEventSections.jsx` to use a state variable `searchTerm` to track the current value input by the user. Whenever the search term changes (by submitting the form), I call `setSearchTerm`, which triggers a new query via TanStack Query. The query key is now `['events', {search: searchTerm}]`, so each search gets its own cache entry:

```js
const [searchTerm, setSearchTerm] = useState('');
const { data, isError, isPending, error } = useQuery({
  queryKey: ['events', { search: searchTerm }],
  queryFn: () => fetchEvents(searchTerm),
});
```

I add a `handleSubmit` function for the form, updating the search term from the input:

```js
function handleSubmit(event) {
  event.preventDefault();
  setSearchTerm(searchElement.current.value);
}
```

The UI now displays different content based on the query state: a prompt if no search term, a loading indicator during fetch, an error block if something fails, or a list of events if the data comes back.

Under the section header, I add the form and render the content appropriately:

```js
<section className="content-section" id="all-events-section">
  <header>
    <h2>Find your next event!</h2>
    <form onSubmit={handleSubmit} id="search-form">
      <input
        type="search"
        placeholder="Search events"
        ref={searchElement}
      />
      <button>Search</button>
    </form>
  </header>
  {content}
  <p>Please enter a search term and to find events.</p>
</section>
```

Now, when I return to the app, the FindEventSection component loads events based on the search term just fine. But in the NewEventsSection—where my query key is simply `["events"]` and I don’t pass a search term—the code still tries to build a URL with an invalid value (`search=%5Bobject%20Object%5D`). This is because an undefined or incorrect search term value gets used in the URL construction logic. I'll address and fix this issue in the next step.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qa2W4eEmdpoa0Rj23UWQDuHKPwhigkJ7OfnNC" alt="find event.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qUYuFziWTIjT5B2OK3mQsvL90t1laFeDicUku" alt="newfetchenvent.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Qurey config  Object and Aborting Request

In this step, I’m addressing an issue with how TanStack Query passes default arguments to the query function. Previously, in NewEventsSection, the query function was receiving an object instead of just a search term, which caused problems with building the search URL—resulting in invalid requests. TanStack passes a default parameter object containing helpful properties like the query key and an abort signal (which lets you cancel the request if the component unmounts before completion).

To fix this, I updated the `fetchEvents` function in `src/utils/http.js` to expect an object, and then destructure the `signal` and `searchTerm` from it. I also added support for passing the signal to the fetch options, which allows requests to be aborted cleanly.

```js
export async function fetchEvents({ signal, searchTerm }) {
  // ...construct URL as before
  const response = await fetch(url, { signal });
  // ...rest of the function
}
```

In `FindEventSection`, I adjusted the query function to pass both the abort signal and the search term in an object, so the request remains compatible:

```js
const { data, isPending, isError, error } = useQuery({
  queryKey: ["events", { search: searchTerm }],
  queryFn: ({ signal }) => fetchEvents({ signal, searchTerm }),
});
```

Now, the app works as expected: the events are fetched correctly, search terms are handled properly, and requests are efficiently aborted if needed. It’s a small but important adjustment for robustness and prevents those "stupid" search URLs!

### Enabled and disabled queries

Right now, when I visit the `/event` route or reload the page, both **NewEventsSection** and **FindEventSection** end up showing the same set of events, especially when the search term is empty (""). This leads to duplicated data on the page—not ideal!

To fix this, I want the search query in **FindEventSection** to only run when the user actually enters a search term. First, I set the `enabled` property on the useQuery config like this:

```js
enabled: !!searchTerm, // only run query if there's a search term
```

However, when the search term is not set, this still shows a loading indicator, which isn’t the experience I want. Ideally, on first visit, **FindEventSection** displays *no events and no loading spinner*. Only after the user enters a search term should the events show up. And if the user later clears the search input, all events should be displayed—there should be a clear difference between no search and a cleared search.

To achieve this, I made these changes:
- Set the initial `searchTerm` state to `undefined`:
  ```js
  const [searchTerm, setSearchTerm] = useState();
  ```
- Use `isLoading` instead of `isPending`, because `isLoading` is only true when the query is actually enabled. `isPending` can be true even for disabled queries that are being refetched.

  Example:
  ```js
  const { data, isPending, isLoading, isError, error } = useQuery({
    queryKey: ["events", { search: searchTerm }],
    queryFn: ({ signal }) => fetchEvents({ signal, searchTerm }),
    enabled: searchTerm !== undefined, // only run if searchTerm is defined (not on initial render)
  });
  ```

Result:  
Now, when a user lands on the page for the first time, no events and no loading indicator are shown in **FindEventSection**. After searching, the appropriate events appear. And if the search input is cleared, *all events* are displayed, giving a distinct and intuitive experience.

### Changing data with mutation

In this step, I’m switching over to the backend for a bit—specifically `backend/app.js`, which defines a route for handling POST requests to create new events:

```js
app.post('/events', async (req, res) => { ... }
```

Back on the React side, I’m focusing on the `NewEvent.jsx` component, which is designed to let users create new events. Until now, this component only displayed a modal with a form, but submitting the form didn’t actually do anything.

So, I added a function in `src/utils/http.js` to handle creating new events via a POST request:

```js
export async function createNewEvent(eventData) {
  const response = await fetch(`http://localhost:3000/events`, {
    method: 'POST',
    body: JSON.stringify(eventData),
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    const error = new Error('An error occurred while creating the event');
    error.code = response.status;
    error.info = await response.json();
    throw error;
  }
  const { event } = await response.json();
  return event;
}
```

In `NewEvent.jsx`, I then used TanStack Query’s `useMutation` hook to handle submitting the form data. Unlike `useQuery`, `useMutation` is triggered only when you call its `mutate` function. This is perfect for form submissions:

```js
import { useMutation } from '@tanstack/react-query';

const { mutate, isPending, isError, error } = useMutation({
  mutationFn: createNewEvent,
});

function handleSubmit(formData) {
  mutate(formData);
}
```

For now, after clicking “Create,” nothing happened because I hadn’t written any code to refresh data or navigate away after the creation request. Also, I ran into a “400 Bad Request” error—because the form didn’t include an image upload (which I haven’t implemented yet). The backend rejects events without all required fields.

To make error handling better, I updated the UI so users get feedback while submitting and if there’s a problem:

```js
return (
  <Modal onClose={() => navigate('../')}>
    <EventForm onSubmit={handleSubmit}>
      {isPending && "Submitting..."}
      {!isPending && (
        <>
          <Link to="../" className="button-text">
            Cancel
          </Link>
          <button type="submit" className="button">
            Create
          </button>
        </>
      )}
    </EventForm>
    {isError && (
      <ErrorBlock
        title={"An error occurred while creating the event"}
        message={error.info?.message || "Failed to create event, please check your input."}
      />
    )}
  </Modal>
);
```

Now, the user sees proper error messages and feedback if event creation fails, which helps make the UI more user-friendly while I continue building features!

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qgMZ2UFa3KiXd4fvRTClOF932c8JhDIkLQoYE" alt="create new events.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qpC5y7BHLVnitpMKCDrXcu5h7F14Tg2bje6Sl" alt="error handling.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### That's it for noew

I’ve reached a good stopping point for now, since this post is getting long and I need to head out for a workout. I’ll pick up with the next steps and continue exploring React in my upcoming blog entry. Looking forward to diving deeper into new features and sharing what I learn!

## Summary

### Key Learnings

Key services and concepts I learned in this project include:
- **React and TanStack Query:** Managing HTTP requests and client-side state, enabling data caching, refetching, and optimistic updates for a responsive UI.
- **Express.js Backend:** Designing REST API endpoints for CRUD operations and handling JSON data storage.
- **Form Handling and Validation:** Using React for dynamic forms, including error handling and mutation management.
- **Network Behavior:** Understanding the impact of caching, background refetching, and request throttling on user experience.
- **State and Query Management:** Leveraging query keys, abort signals, and control flags (like `enabled`, `staleTime`, `gcTime`) for precise query behavior.
- **Error Feedback:** Improving user experience with informative loading and error indicators.

Overall, these concepts helped me create an app with smooth client-server interactions and optimized user experience.

### Unexpected Insights

One thing I didn’t expect in this project was running into subtle issues with data caching and refetching behavior in the frontend. While I thought TanStack Query would handle everything automatically, I quickly realized that it required careful configuration to avoid unnecessary duplicate fetches and ensure efficient UI updates. Troubleshooting these edge cases was unexpected, but it taught me a lot about how advanced React query tools work behind the scenes!

### Time Investment

Few hors

### Motivation

I did this project today because I wanted hands-on experience with modern React data fetching patterns and backend integration. I set out to learn how TanStack Query simplifies client-server communication, data caching, and UI synchronization, and to strengthen my skills in building full-stack apps with React and Express. This project also gave me a practical opportunity to experiment with query configuration, error handling, and backend-based features—all of which I’m eager to use in future projects.
