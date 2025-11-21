# Data fetching with tanstack - Mutation

**Author:** Duc Thai

**Email:** ducthai060501@gmail.com

## Overview

Today, I’m continuing the journey into React and TanStack Query, with a focus on handling mutations to provide the best possible user experience when creating events. The goal is to ensure that users can easily add new events through our site, and that any issues are handled gracefully for a smooth, reliable experience.

This project picks up right where the previous blog post left off ([read here](https://dukemblog.xyz/post/56)). Here’s a quick recap of what I’ve accomplished so far:
- Installed and set up TanStack Query for managing data fetching and mutations.
- Moved all fetch and post logic into `src/utils/http.js` for better separation and reusability.
- Updated both the `NewEventsSection` and `FindEventSection` to use React Query hooks for efficient data management.
- Improved error display across the app so users get clear and immediate feedback when something goes wrong.
- ...and more foundational improvements to set the stage for deeper mutation handling.

Now, I’m ready to dig deeper into mutation logic and explore strategies for making user event creation seamless and robust!

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qMDDVTBiXTWlJcoYOmRxLS2GzA6fwIkbrvdZV" alt="create new events.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qsk2IAH5qWjNxAMiKVC58X0lQ6zwvr9cuLkfo" alt="error handling.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

## Steps

### Fetching more data and test the mutation

In this step, the goal is to let users pick an image for their event—making sure we collect all necessary data to successfully create an event.

On the backend, there’s a route in `backend/app.js`:
```js
app.get('/events/images', async (req, res) => {
  const imagesFileContent = await fs.readFile('./data/images.json');
  const images = JSON.parse(imagesFileContent);
  res.json({ images });
});
```
This lets us send a GET request to retrieve a list of images for users to choose from.

In the frontend, I updated the `EventForm` component, which has an `<ImagePicker />` placeholder for image selection. Previously, the images array was hardcoded and empty—so nothing useful showed up!

To fix this:
- I created a new function in `src/utils/http.js` to fetch selectable images from the backend using TanStack Query:
  ```js
  export async function fetchSelectableImages({ signal }) {
    const response = await fetch(`http://localhost:3000/events/images`, { signal });
    if (!response.ok) {
      const error = new Error('An error occurred while fetching the images');
      error.code = response.status;
      error.info = await response.json();
      throw error;
    }
    const { images } = await response.json();
    return images;
  }
  ```

- Then, inside `EventForm.jsx`, I replaced the hardcoded images with the ones fetched from the server:
  ```js
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['events-images'],
    queryFn: fetchSelectableImages,
  });

  <div className="control">
    {isLoading && <p>Loading images...</p>}
    {isError && (
      <ErrorBlock
        title={"Failed to load selectable images"}
        message={"Could not load images, please try again later."}
      />
    )}
    {data && (
      <ImagePicker
        images={data}
        onSelect={handleSelectImage}
        selectedImage={selectedImage}
      />
    )}
  </div>
  ```

Now, when users open the form, they see the available images and can pick one as part of creating an event. Submission works if all required data is provided. While there’s no redirect or post-success action yet, I confirmed that the newly created event appears on the main page when I navigate back—making image selection smooth and fully functional!

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4q1bKw2rCAELGFWo3JV5NrglT0MDxijvqQHef1" alt="image show inform.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qnffov6YMGzFMuZljoEWma64h1URIiqtwkTxp" alt="new event here.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Acting on mutation Success and Invalidating Queries

In this step, I'm focused on improving what happens after a successful mutation in the `NewEvent` component—specifically, how the app behaves when a user creates a new event.

Previously, inside the `handleSubmit(formData)` function, I would call `mutate` to send the event data. I also considered adding a navigation action directly after mutating, but realized it's best practice to wait for mutation success before navigating away. That way, I’m sure the event is actually created before moving the user back to the `/events` page.

To do this, I added an `onSuccess` callback inside the `useMutation` hook:
```js
const { mutate, isPending, isError, error } = useMutation({
  mutationFn: createNewEvent,
  onSuccess: () => {
    navigate('/events');
  }
});
```

This works for navigation, but I noticed a UX issue: when redirected to the `/events` page, the newly created event doesn't appear right away—it only shows up if I leave and revisit the page. That’s because React Query’s cached data isn't being updated automatically.

To fix this, I need to mark the data for the events query as stale so it gets refetched. I moved the `queryClient` instance to `src/utils/http.js` and imported it wherever I need React Query utilities. Then, I updated my `onSuccess` logic:

```js
const { mutate, isPending, isError, error } = useMutation({
  mutationFn: createNewEvent,
  onSuccess: () => {
    // Invalidate the events query so the events list refetches
    queryClient.invalidateQueries({ queryKey: ['events'] });
    navigate('/events');
  }
});
```

Now, when a user successfully creates an event, the events list is automatically refreshed and the new event appears instantly—making the UI feel much more responsive and polished!

### View event details and delete events

In this step, I'm implementing the ability to view individual event details and delete events.

First, in `src/utils/http.js`, I added two helpers:
- `fetchEvent`: fetches a single event by its ID
- `deleteEvent`: deletes an event by its ID

```js
export async function fetchEvent({ id, signal }) {
  const response = await fetch(`http://localhost:3000/events/${id}`, { signal });
  if (!response.ok) {
    const error = new Error('An error occurred while fetching the event');
    error.code = response.status;
    error.info = await response.json();
    throw error;
  }
  const { event } = await response.json();
  return event;
}

export async function deleteEvent({ id }) {
  const response = await fetch(`http://localhost:3000/events/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) {
    const error = new Error('An error occurred while deleting the event');
    error.code = response.status;
    error.info = await response.json();
    throw error;
  }
  return response.json();
}
```

Inside `EventDetails`:
- I use `useParams()` to grab the event ID from the route.
- I fetch the event data using `useQuery`, so the event details are loaded and displayed.
- For deletion, I use `useMutation` with `deleteEvent`, and in `onSuccess`, I invalidate the events query and navigate back to `/events` to make sure the list refreshes right away.

```js
const { id } = useParams();
const navigate = useNavigate();
const { data, isLoading, isPending, isError, error } = useQuery({
  queryKey: ["events", id],
  queryFn: ({ signal }) => fetchEvent({ signal, id }),
});

const { mutate } = useMutation({
  mutationFn: deleteEvent,
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["events"] });
    navigate("/events");
  },
});

function handleDelete() {
  mutate({ id: id });
}
```

Depending on state, the UI will show loading, error, or the event content. When `data` is available, details like the title, image, location, date, time, and description are rendered. The user can also click 'Delete' to remove the event smoothly, or 'Edit' to make changes.

I then update the main JSX template to wrap everything and display the right view:

```jsx
return (
  <>
    <Outlet />
    <Header>
      <Link to="/events" className="nav-item">
        View all Events
      </Link>
    </Header>
    <article id="event-details">{content}</article>
  </>
);
```

Result:  
After these changes, users can view event details, delete events seamlessly, and see the updated events list instantly when returning to `/events`. It feels smooth and responsive!

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qg4zK973KiXd4fvRTClOF932c8JhDIkLQoYE6" alt="view event success.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Disable auto refecthing after sendign query

In this step, I'm improving how event deletion works to avoid unnecessary 404 errors.

Previously, when a user deleted an event from its details page (`events/:id`), React Query would invalidate both the general `["events"]` list and the individual event detail query. Since the app was still on the details page during invalidation, React Query triggered a refetch for the deleted event’s data, which resulted in a 404 error and an error message like `{"message":"For the id 2469, no event could be found."}` in the dev tools network tab.

To avoid this, I updated the mutation logic:
```js
const { mutate } = useMutation({
  mutationFn: deleteEvent,
  onSuccess: () => {
    queryClient.invalidateQueries({
      queryKey: ["events"],
      // prevent refetching the events detail query after deletion
      refetchType: "none"
    });
    navigate("/events");
  },
});
```

Now, after an event is deleted, React Query no longer refetches the specific event detail query (so no more 404 errors), and only the main event list page is refreshed after navigation. This results in a smoother user experience with no confusing network errors!

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qjstiAQL6ULWgum4ConQ1HqVwTy75DKYtkrha" alt="404.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Enhance the app lil more.

In this step, I’m adding a confirmation modal to the event deletion feature to improve the user experience—no TanStack Query needed here, just simple React state management.

First, I created a local state to manage the deletion process:
```js
const [isDeleting, setIsDeleting] = useState(false);
```

Then, I added two functions for starting and stopping the confirmation modal:
```js
function handleStartDelete() {
  setIsDeleting(true);
}
function handleStopDelete() {
  setIsDeleting(false);
}
```

For deleting the event, I also enhanced the mutation hook to track the loading and error states specific to deletion:
```js
const {
  mutate,
  isPending: isPendingDeletion,
  isError: isErrorDeleting,
  error: deleteError,
} = useMutation({
  mutationFn: deleteEvent,
  onSuccess: () => {
    queryClient.invalidateQueries({
      queryKey: ["events"],
      refetchType: "none",
    });
    navigate("/events");
  },
});
```

Finally, I updated the JSX to show a confirmation modal when `isDeleting` is true. The modal asks the user to confirm, shows a loading state when the deletion is in progress, and displays any errors if the deletion fails. Users can either cancel or proceed:

```jsx
{isDeleting && (
  <Modal onClose={handleStopDelete}>
    <h2>Are you sure?</h2>
    <p>
      Do you really want to delete this event? This process cannot be
      undone.
    </p>
    <div className="form-actions">
      {isPendingDeletion && <p>Deleting event...</p>}
      {!isPendingDeletion && (
        <>
          <button className="button-text" onClick={handleStopDelete}>
            Cancel
          </button>
          <button className="button" onClick={handleDelete}>
            Delete
          </button>
        </>
      )}
    </div>
    {isErrorDeleting && (
      <ErrorBlock
        title={"Failed to delete event"}
        message={deleteError.info?.message || "Something went wrong"}
      />
    )}
  </Modal>
)}
```

This creates a clear and user-friendly deletion process, ensuring users must confirm before removing an event and get immediate feedback if something goes wrong.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qCzdf5ru1VEYGObctBN5qTLu8msjQay0zwd93" alt="delete confirmation modal.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Edit event by prepopulate event data

In this step, I’m refining the **EditEvent** component so it can prepopulate the event form with the existing event’s data—making it easier for users to edit an event.

Originally, `EventForm` received `inputData={null}`, which meant the form fields were empty and users had to re-enter all their information from scratch. Now, I’m updating **EditEvent** to fetch the current event data and pass it into the form as `inputData`.

Here’s what I did:

- Used `useQuery` with the event `id` from route params to fetch the event:
  ```js
  const { data, isPending, isError, error } = useQuery({
    queryKey: ['events', params.id],
    queryFn: ({ signal }) => fetchEvent({ signal, id: params.id }),
  });
  ```

- Rendered dynamic content:
  - Show a loading spinner if the data is still loading.
  - Show an error block if the fetch failed.
  - Display the form, prepopulated with the event’s data, when fetch is successful.

- Cleaned up the returned JSX so it only renders what makes sense for each state:
  ```js
  return (
    <Modal onClose={handleClose}>
      {content}
    </Modal>
  );
  ```

Result:  
When editing an event, the form now displays all the original event details, ready for update—making the editing experience much more convenient and user-friendly!

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qfd01HqDxo48VaHKMDyTZCRwkeB6cpPb7jJ1z" alt="error prepoluated.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qWdEQA98TrlMgIHQdjUxZ0GXn9aKBwvqWyChb" alt="prepoluated data.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Edit event - user click update

In this step, we’re learning about **optimistic updating**, and I’m applying it to the event-updating feature.

First, I added a new function in `src/utils/http.js`:

```js
// function to update an event by id
export async function updateEvent({ id, event }) {
  const response = await fetch(`http://localhost:3000/events/${id}`, {
    method: 'PUT',
    body: JSON.stringify({ event }),
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    const error = new Error('An error occurred while updating the event');
    error.code = response.status;
    error.info = await response.json();
    throw error;
  }

  return response.json();
}
```

Then inside **EditEvent.jsx**, I created a mutation using `useMutation`.
The goal here is to update the UI immediately—*before* the backend responds—while still handling errors safely:

```js
const { mutate } = useMutation({
  mutationFn: updateEvent,

  // Called right before the mutation runs, meaning before we get a backend response
  onMutate: async (data) => {
    const newEvent = data.event;

    // Cancel active queries for this event to avoid overwriting our optimistic update
    await queryClient.cancelQueries({ queryKey: ['events', params.id] });

    // Store the previous event so we can roll back if something goes wrong
    const previousEvent = queryClient.getQueryData(['events', params.id]);

    // Apply the optimistic update immediately
    queryClient.setQueryData(['events', params.id], newEvent);

    // Pass previous data to onError for rollback
    return { previousEvent };
  },

  // If the mutation fails, restore the previous data
  onError: (error, data, context) => {
    queryClient.setQueryData(['events', params.id], context.previousEvent);
  },

  // After success or failure, revalidate the data with the backend
  onSettled: () => {
    queryClient.invalidateQueries({ queryKey: ['events', params.id] });
  },
});
```

The submit handler simply triggers the mutation with the required shape:

```js
function handleSubmit(formData) {
  mutate({ id: params.id, event: formData });
  navigate('../');
}
```

I didn’t use `onSuccess` here because the whole point of optimistic updating is to **update the UI instantly without waiting for the backend response**.
The backend response is only used later for validation—or rollback if something breaks.

Overall, everything works smoothly now.


<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qnegg6eMGzFMuZljoEWma64h1URIiqtwkTxpP" alt="update event success.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qGYgXj8F2wqzVp0OH4lYcgFELIxnCZmTArSB7" alt="updated form.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Using query key as query function input

Here is the polished, structured version of your learning note, keeping your tone while making everything clear and readable:

---

In this step, we’re basically done with the CRUD features, but there’s one more improvement I want to make: **using the query key as input to the query function**. Specifically, in the `/events` route (inside `NewEventsSection.jsx`), the “Recently Added Events” section currently renders *all* events. I want to tweak this so it only fetches a limited number of events.

Right now, the query looks like this:

```js
const { data, isPending, isError, error } = useQuery({
  queryKey: ["events"],
  queryFn: fetchEvents,
  staleTime: 1000 * 60,
  gcTime: 1000 * 60 * 0.5,
});
```

To support fetching only a subset of events, the backend already includes optional query parameters:

```js
app.get('/events', async (req, res) => {
  const { max, search } = req.query;
  ...

  if (max) {
    events = events.slice(events.length - max, events.length);
  }

  res.json({
    events: events.map((event) => ({
      id: event.id,
      title: event.title,
      image: event.image,
      date: event.date,
      location: event.location,
    })),
  });
});
```

The `max` parameter lets us return only the last *N* events, so the next step is updating the client to make use of it.

In `http.js`, I updated `fetchEvents` to accept `max` as an argument:

```js
export async function fetchEvents({ signal, searchTerm, max }) {
  let url = "http://localhost:3000/events";

  if (searchTerm && max) {
    url += `?max=${max}&search=${encodeURIComponent(searchTerm)}`;
  } else if (searchTerm) {
    url += `?search=${encodeURIComponent(searchTerm)}`;
  } else if (max) {
    url += `?max=${max}`;
  }

  const response = await fetch(url, { signal });
  ...
  const { events } = await response.json();

  return events;
}
```

Finally, in **NewEventsSection**, I adjusted the query to pass `max: 3` through the query key so React Query can use it inside the query function:

```js
const { data, isPending, isError, error } = useQuery({
  queryKey: ["events", { max: 3 }],

  // React Query passes the query key and a signal to the queryFn.
  // We extract `max` from the query key and forward it to fetchEvents.
  queryFn: ({ signal, queryKey }) =>
    fetchEvents({ signal, ...queryKey[1] }),

  staleTime: 1000 * 60,
  gcTime: 1000 * 60 * 0.5,
});
```

By passing `max` through the query key, we avoid redundant state and keep everything driven by React Query’s caching model. Now, the “Recently Added Events” section displays only the **three most recent events**, which is exactly what we want.

This setup works cleanly and feels smooth.


### React Router with tanstack

## One Last thing: Integrating React Router with TanStack Query

Let see if we can leverages **React Router's data loading features** alongside **TanStack Query's caching and optimistic updates**. Here's a detailed breakdown:

---

## Let's start

**Before**: The app used TanStack Query for data fetching but couldn't prefetch data before route transitions, causing loading spinners on every navigation.

**After**: React Router loaders prefetch data into TanStack Query's cache, while mutations use optimistic updates for instant UI feedback.

---

## 🔧 Key Changes Made

### 1. **Added React Router Loader Function**

```javascript
// EditEvent.jsx - NEW loader export
export function loader({ params }) {
  return queryClient.fetchQuery({
    queryKey: ["events", params.id],
    queryFn: ({ signal }) => fetchEvent({ signal, id: params.id }),
  });
}
```

**What it does**:
- Runs **before** the `EditEvent` component renders
- Uses `queryClient.fetchQuery()` to fetch data and cache it in TanStack Query
- React Router waits for this promise to resolve before showing the route

**Impact**: 
- Eliminates the loading state flash when navigating to `/events/:id/edit`
- Data is already in cache when `useQuery` runs in the component

---

### 2. **Added React Router Action Function**

```javascript
// EditEvent.jsx - NEW action export
export async function action({ request, params }) {
  const formData = await request.formData();
  const updatedEventData = Object.fromEntries(formData);
  
  await updateEvent({ id: params.id, event: updatedEventData });
  queryClient.invalidateQueries({ queryKey: ["events"] });
  
  return redirect("../");
}
```

**What it does**:
- Handles form submission on the **server side** of React Router (client-side action)
- Receives native `FormData` from the form submission
- Calls the mutation function directly (bypassing `useMutation`)
- Invalidates queries and redirects after success

**Why this matters**:
- Centralizes mutation logic outside the component
- Works even if JavaScript is disabled (progressive enhancement)
- Simplifies component code by moving business logic to route definition

---

### 3. **Refactored Form Submission Flow**

**Old approach (using `useMutation` directly)**:
```javascript
function handleSubmit(formData) {
  mutate({ id: params.id, event: formData });
  navigate('../');
}
```

**New approach (using React Router's `useSubmit`)**:
```javascript
import { useSubmit, useNavigation } from "react-router-dom";

const submit = useSubmit();
const { state } = useNavigation(); // Track submission state

function handleSubmit(formData) {
  submit(formData, { method: "PUT" }); // Triggers the action function
}
```

**Benefits**:
- `useNavigation().state` gives you `"submitting"` state automatically
- No need to manually track `isPending` from `useMutation`
- Form submission is now handled by React Router's navigation system
- use can remove  `useMutation` completely now!
---

### 4. Meation in step 3: we can remove **Optimistic Updates with Rollback**

```javascript
// delete this, or comment it
const { mutate } = useMutation({
  mutationFn: updateEvent,
  
  // 🎯 OPTIMISTIC UPDATE: Update cache immediately
  onMutate: async (data) => {
    const newEvent = data.event;
    
    // Cancel outgoing queries to avoid race conditions
    await queryClient.cancelQueries({ queryKey: ["events", params.id] });
    
    // Save previous data for rollback
    const previousEvent = queryClient.getQueryData(["events", params.id]);
    
    // Update cache optimistically
    queryClient.setQueryData(["events", params.id], newEvent);
    
    return { previousEvent }; // Return for rollback
  },
  
  // 🔄 ROLLBACK: Restore previous data if mutation fails
  onError: (error, data, context) => {
    queryClient.setQueryData(["events", params.id], context.previousEvent);
  },
  
  // ✅ SYNC: Refetch after success or error to ensure consistency
  onSettled: () => {
    queryClient.invalidateQueries({ queryKey: ["events", params.id] });
  },
});
```

**What's happening**:

1. **`onMutate`**: Fires **before** the backend call
   - Cancels any in-flight queries for this event
   - Saves current data as `previousEvent`
   - Immediately updates the cache with new data (optimistic)
   - User sees instant feedback

2. **`onError`**: If backend returns error
   - Restores `previousEvent` from context
   - User sees their change "revert"

3. **`onSettled`**: After success or error
   - Invalidates the query to refetch from backend
   - Ensures cache matches server state

---

### 5. **Configured Query Staleness**

```javascript
const { data, isPending, isError, error } = useQuery({
  queryKey: ["events", params.id],
  queryFn: ({ signal }) => fetchEvent({ signal, id: params.id }),
  staleTime: 5000, // ⬅️ NEW: Data is fresh for 5 seconds
});
```

**Why this matters**:
- Without `staleTime`, TanStack Query refetches on every focus/mount
- With `staleTime: 5000`, data loaded by the loader stays fresh for 5 seconds
- Reduces unnecessary network requests when navigating back and forth

---

### 6. **Added Global Fetching Indicator**

```javascript
// Header.jsx - NEW useIsFetching hook
import { useIsFetching } from "@tanstack/react-query";

export default function Header({ children }) {
  const fetching = useIsFetching(); // Returns count of active queries
  
  return (
    <>
      <div id="main-header-loading">
        {fetching > 0 ? <progress/> : null}
      </div>
      {/* ...rest of header */}
    </>
  );
}
```

**What it does**:
- Shows a global progress bar whenever **any** query is fetching
- Useful for background refetches that don't block navigation
- User always knows when data is being loaded

---

### 7. **Wired Up Loader and Action in Router**

```javascript
// App.jsx - Updated route configuration
{
  path: "/events/:id/edit",
  element: <EditEvent />,
  loader: editEventLoader,  // ⬅️ Prefetch data before render
  action: editEventAction,  // ⬅️ Handle form submissions
}
```

---

## 🏆 Benefits of This Integration Pattern

### **Best of Both Worlds**

| Feature | TanStack Query | React Router | Combined Benefit |
|---------|----------------|--------------|------------------|
| **Data Prefetching** | ❌ No built-in | ✅ Loaders | Data ready before component mounts |
| **Caching** | ✅ Automatic | ❌ No caching | Cached data reused across routes |
| **Optimistic Updates** | ✅ Built-in | ❌ Manual | Instant UI feedback with automatic rollback |
| **Form Handling** | ❌ Manual | ✅ Actions | Centralized mutation logic |
| **Loading States** | ✅ `isPending` | ✅ `useNavigation` | Multiple ways to track loading |
| **Background Sync** | ✅ Automatic | ❌ No sync | Cache stays fresh automatically |

---

## 🔄 Data Flow Example

**When user navigates to `/events/123/edit`**:

1. **React Router loader runs first**
   ```
   loader() → queryClient.fetchQuery() → Cache populated
   ```

2. **Component renders with cached data**
   ```
   useQuery() → Reads from cache (no loading state!)
   ```

3. **User edits form and submits**
   ```
   submit() → action() → updateEvent() → invalidateQueries()
   ```

4. **OR with optimistic updates (commented out in your code)**
   ```
   handleSubmit() → mutate() → onMutate (instant UI update)
   → Backend call → onError (rollback) OR onSettled (sync)
   ```

---

**The key insight**: Let React Router handle **when** to fetch (route transitions), and let TanStack Query handle **how** to fetch (caching, optimistic updates, background sync).

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qatMZPHdpoa0Rj23UWQDuHKPwhigkJ7OfnNCz" alt="loader slow net.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### What's a wrap

That's is it broski and sissy, see u next blog.

## Summary

### Key Learnings

One major takeaway was how to use TanStack Query effectively—not just for basic data fetching, but for more advanced patterns like optimistic updates, query invalidation, and fine-grained cache control. I also learned how query keys can act as dynamic inputs to the query function, allowing the UI to fetch only the data it actually needs.

On the backend side, I explored how to structure API endpoints with optional query parameters, such as max and search, and how to handle them cleanly in the Express server. This helped me understand how the frontend and backend work together to deliver filtered or partial data instead of always fetching full datasets.

### Unexpected Insights

One thing I didn’t expect was how powerful and flexible React Query becomes once you understand its patterns. At first, features like optimistic updates, canceling active queries, or passing parameters through queryKey felt a bit abstract. But after implementing them, I realized they actually simplify a lot of complex UI logic.

I also didn’t expect how easy it is to control things like caching behavior, staleTime, or aborting requests—React Query handles so much behind the scenes that the UI feels fast and stable, even with minimal code.

### Time Investment

2 hours.

### Motivation

Cuz I am cool just like that ==).
