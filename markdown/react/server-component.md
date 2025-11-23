# React server components

**Author:** Duc Thai

**Email:** ducthai060501@gmail.com



## Overview

Today, we’re diving into some advanced React features that aren’t available in every project, but are becoming increasingly important in the React ecosystem. 

Specifically, we’ll explore the differences between React server components and client components—what they are, when you’d use each, and how they impact my app architecture. We’ll also take a closer look at the concept of server actions and how they compare to form actions, which you may have already encountered in previous course sections.

Additionally, we’ll revisit React’s Suspense component and its connection to the new `use` hook introduced in React 19. While `use` is commonly used to access context, in this section we’ll see how it can also help retrieve data resolved by promises, at least in projects that support the latest React features. 

**Final code:** [here](https://github.com/vvduth/server-component/tree/server)
**Start code:** https://github.com/vvduth/server-component/tree/main

## Steps

### React feature you might not be able to use

In this step, we’re looking at some React features that, while part of React itself, aren’t usable in every React project out-of-the-box. Specifically, I’m talking about React Server Components, Server Actions, and using the `use()` hook with Promises. These features are popular and powerful, but can’t be integrated just anywhere—they require special project setup or dependencies.

So, why are these features harder to use? The main reason is that they involve code that’s meant to run on the server side, not just the browser. In a standard React app, everything executes in the browser, but for server components and server actions, the project needs to split code into two parts: one that runs only on the server, and one for the client. Plus, the setup has to make sure there’s an environment available for running the server-side code, which isn’t the case in a typical client-only React app.

Because of these requirements, we’re starting with a simple Next.js project for this exploration. Next.js is “React-ish” and provides out-of-the-box support for server-side rendering and the kind of project structure needed to work with these advanced React features.

### Client vs server component : therory

In this step, we’re covering the theory behind Client vs Server Components in React. I know it can feel a bit dry, but understanding the differences is really important for building modern React apps, especially with frameworks like Next.js.

**React Server Components**  
- As the name suggests, server components never execute on the client side.
- Their code doesn’t even get sent to the browser—these component functions are only run on the server, or at build time if you’re pre-rendering pages.
- Because they never run in the browser, you can’t use client-side-only features like `useState` or browser APIs in server components.

**React Client Components**  
- These are the components most React devs are familiar with—they run in the browser.
- But in server-rendered apps (like Next.js), client components are also pre-rendered on the server: the server generates the initial HTML output, sends it to the browser, and then React takes over from there.
- After this initial render, client components "hydrate" on the client, turning into a fully interactive single page app.
- That means you can use client-side features like hooks (`useState`, `useEffect`) and handle all traditional SPA-style interactivity.

**TL;DR:**  
- Server components: Render only on the server, never in the browser, and code doesn’t reach the client.
- Client components: Rendered on both the server (for SSR/initial HTML) and the client (for interactivity/hydration).

This division is powerful, but also means you have to be intentional about where you put certain logic!

### Project overview

In this step, we’re starting with a really basic Next.js app, built with **Next.js 15 + React 19**, to explore the fundamentals of file-based routing (App Router) and the difference between Client and Server Components.

### File-based Routing (App Router) Recap:
- `app/layout.js`: Sets the root layout for all routes and exports metadata for better SEO.
- `app/page.js`: The main (home) route at `/`.
- Creating new routes is easy: just add a folder inside `app/` with a `page.js` (for example, `app/about/page.js` becomes `/about`).
- Layouts cascade, so any child route will inherit its parent’s layout automatically.

---

Inside the `/app` folder, we’re also creating a `components` folder with two simple demonstration files:

#### `clientDemo.js`

```js
'use client';

import { useState } from 'react';

export default function ClientDemo({ children }) {
  const [count, setCount] = useState(0); // <- this is why it's a client component

  console.log('ClientDemo rendered');
  return (
    <div className="client-cmp">
      <h2>A React Client Component</h2>
      <p>
        Will be rendered on the client <strong>AND</strong> the server.
      </p>
      {children}
    </div>
  );
}
```
- This is a **Client Component**, marked with `'use client'` at the top.
- It uses `useState` (a client-only React feature).
- Will be rendered on both the server (for initial SSR) and the client (for hydration/interactivity).

---

#### `RSCDemo.js`

```js
// import ClientDemo from './ClientDemo';

export default async function RSCDemo() {
  console.log('RSCDemo rendered');
  return (
    <div className="rsc">
      <h2>A React Server Component</h2>
      <p>
        Will <strong>ONLY</strong> be rendered on the server or at build time.
      </p>
      <p>
        <strong>NEVER</strong> on the client-side!
      </p>
      {/* <ClientDemo /> */}
    </div>
  );
}
```
- This is a **Server Component** (no `'use client'` directive).
- It never runs in the browser—just on the server or at build time.
- The commented-out `ClientDemo` import hints that you can nest client components inside a server component, but not the other way around.

---

**Summary:** We’re setting up the project structure, reviewing file-based routing, and building simple demos of both client and server components to see, in practice, how Next.js separates and manages them in a modern React environment.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qv33A1NvzsfPtlpKmiZSO0rUQ5ChuLkN6XW3R" alt="app structure.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qUpzCKdTIjT5B2OK3mQsvL90t1laFeDicUku8" alt="render rsc.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qbg89mUYzC0UbepwGZEqnOvQ5go6BkMrPTW1t" alt="projetc first run.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Lil more about server component

In this step, I'm rendering the `RSCDemo` component in `app/page.js` with:

```js
import RSCDemo from "./components/RSCDemo";

export default function Home() {
  return (
    <main>
      <RSCDemo />
    </main>
  );
}
```

When doing this, you’ll notice something interesting:  
- **You don’t see the console log from `RSCDemo` in the browser devtools. Instead, it appears in my local terminal (the server console).**  
- With recent Next.js versions, you might actually see it in the browser devtools console, but it shows up with a `Server` label, like: `Server  RSCDemo rendered` — which still proves it’s running on the server.

It looks and works like any other React component you’ve seen in this course, but what makes it a **React Server Component**?  
- The answer is **the special project setup that Next.js provides.**
- In a Next.js app, all React components are **server components by default**.  
- That means they're rendered on the server side—not in the browser—unless you opt into a client component with `'use client'`.
- **This is very different from a standard React project (like with Vite), where everything runs on the client.**

So in summary:  
Rendering `RSCDemo` looks "normal," but because of Next.js's setup, it's actually a server component. This is why you see the logs server-side instead of in my browser's console, proving it runs on the server—not in the browser!

### So why client component

In this step, we're thinking about why you would convert a component to a client component instead of leaving it as a server component.

First, let's discuss the advantages of server components:
- **Less code shipped to the client:** Since server components are rendered on the server, the client doesn't need to download all the code for these components. This can improve the performance and load times of my web application.
- **Server-side data fetching:** Data can be fetched on the server as part of the rendering process. This allows you to send a completed HTML page (with data included) to the client on the initial request, rather than having to fetch data separately in the browser after page load. This reduces the delay between initial load and data being displayed.

However, why would you convert a component to a client component?
- The main reason is when you need to use features that are only available in client components. For example, if you need to use React's `useState()` hook to manage state (like a counter), you must make that component a client component. Most React hooks (such as `useEffect`, `useRef`, and even `useState`) can **only** be used in client components.

**Summary:**  
Use server components to optimize performance and handle server-side data fetching. But if you need interactivity, state, or most React hooks, you must make a component a client component by adding the `'use client'` directive at the top.

### Combining server and client

In this step, we’re digging into how to **combine server and client components** in a Next.js (or React Server Components) setup.

Here’s what you need to know:

- **A React server component can directly include client components in its JSX.**
  - For example:
    ```jsx
    <div className="rsc">
      <h2>A React Server Component</h2>
      <ClientDemo />
    </div>
    ```
    This works just fine.

- **But a client component can’t directly include a server component in its JSX.**
  - For example, this is NOT allowed:
    ```jsx
    "use client"
    ...
    <h2>
      I am client
      <SomeRSC />
    </h2>
    ```
    If you try this, `SomeRSC` will actually be treated as a client component (even if it was previously a server component), losing any server-only optimizations.

- **There’s one exception:**  
  - **A client component can receive a server component as a child (via the `children` prop).**
    - For example, in `ClientComponent.js`:
      ```jsx
      export default function ClientComponent({children}) {
        return (
          <div>
            I am client
            {children}
          </div>
        );
      }
      ```
      And you use it like:
      ```jsx
      <ClientComponent>
        <ServerComponent />
      </ClientComponent>
      ```
    - This is allowed! The server component gets rendered on the server, passed as a child, and the client component remains interactive.

- **If you try to force a server-only feature inside a client component (for example, making it `async` or using server-only APIs), you’ll get an error.**

**Summary:**  
You can nest client components inside server components, but not the other way around—unless you’re passing server components as children. This is an important rule for architecting my React apps with RSCs!

### Fetching data with server component

In this step, we're adding backend-style data fetching directly into our React Server Components—something that's not possible in regular client components.

Here's what's happening:

1. **Add `dummy-db.json` at the project root:**  
   This file simulates a database using simple JSON:
   ```json
   [
     { "id": "u1", "name": "Dukem", "title": "Instructor" },
     { "id": "u2", "name": "Evum", "title": "Instructor" }
   ]
   ```

2. **Create `DataFetchingDemo.js` in `app/components`:**  
   This server component fetches and displays data from the JSON file using Node.js code (`fs.readFile`).  
   - Because server components run only on the server (not sent to the browser!), you can use Node.js APIs and `async`/`await` directly in my component.
   - This would NOT work in a browser or client component.

   Example code:
   ```js
   import fs from "fs/promises";

   export default async function DataFetchingDemo() {
     const data = await fs.readFile('dummy-db.json', 'utf-8');
     const users = JSON.parse(data);

     return (
       <div className="rsc">
         <h2>RSC with Data Fetching</h2>
         <p>
           Uses <strong>async / await</strong> for data fetching.
         </p>
         <ul>
           {users.map((user) => (
             <li key={user.id}>
               {user.name} ({user.title})
             </li>
           ))}
         </ul>
       </div>
     );
   }
   ```

3. **Render `DataFetchingDemo` on the home page (`app/page.js`):**
   ```js
   return (
     <main>
       <DataFetchingDemo />
     </main>
   );
   ```

**Result:**  
The user data from the JSON "database" appears right on the page—no need for useEffect or client-side fetching! The code for reading the file stays on the server and never gets shipped to the client.

**Why is this cool?**  
Server components make data fetching super simple and secure:  
- You can use async/await and Node.js code directly in my components.  
- The fetch and render happen on the server, and the final HTML is sent to the browser already loaded with data.  
- No extra client requests or `useEffect` needed—just clean, direct data loading in the server environment!

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4q74ObtWVQV1X9BEfvjYTeFJu05cbKyiIHSU4g" alt="data fetching demo.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qiq7GnVUNcRni36IQv5gJb12eYLxdC0rB4GAj" alt="data-fetching.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Submiting data with server component

In this step, we’re learning how to use **server actions** to submit data to the server directly from a React Server Component in Next.js.

Here's what's happening:

1. **Server Actions inside a Server Component:**
   - In `app/components/ServerActionsDemo.js`, you create a component with an `async` function called `saveUserAction`.
   - Inside this function, you add `"use server"` at the top, making it a server action. This means the function runs on the server whenever the form is submitted.
   - The form uses the `action={saveUserAction}` prop, directly linking the form submission to the server-side code.
   - Example:
     ```js
     import fs from 'node:fs';

     export default function ServerActionsDemo() {
       async function saveUserAction(formData) {
         'use server';
         // Read and update dummy-db.json
         // ...
       }
       // JSX including the form
     }
     ```
   - The special part here is the `"use server"` directive: this tells React/Next.js that this function should run only on the server when the form submits.

2. **Important rules for server actions:**
   - You can only **define** server actions inside server components (i.e., files without `'use client'` at the top).  
   - If you try to define a server action in a client component, you’ll get an error, because it’s a contradiction: the file is meant for the browser, but you’re declaring code that must only run on the server.

3. **Re-using server actions from client components:**
   - You **can** use server actions in client components, you just **cannot define** them there.
   - The best practice is to put the server action function (with `"use server"`) in a separate module (e.g., `actions/users.js`), and then import and reference it in my client or server components as needed.
   - Example:
     ```js
     // actions/users.js
     "use server";
     import fs from 'node:fs';
     export async function saveUserAction(formData) {
       // Server-side logic
     }
     // In a component: use action={saveUserAction} on my form
     ```

**Summary:**  
In this step, you made a server action by adding `"use server"` to an async function used as a form action inside a server component. This allows you to securely handle form submissions directly on the server—no client code needed for the data handling or database logic. If you want to use the server action in a client component, you must define it in a server-only module and import it, rather than defining it in the client component itself.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qCaMlhXu1VEYGObctBN5qTLu8msjQay0zwd93" alt="server actiob demo.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### the use() hook.

In this step, we’re focusing on understanding the `use()` hook and how it integrates with React’s Suspense for handling asynchronous data, especially in Next.js and React Server Components.

Here’s what’s happening:

- **The `use()` Hook:**  
  - You've probably seen `use()` used to access context, but you can also use it to “await” special Promises in client components without using `async/await`.  
  - The key: `use()` only works with Promises that are built for React Suspense—not just any regular Promise. These special Promises communicate their loading state to React, allowing for seamless fallback UIs.
  - Next.js helps you generate and use these special Promises out of the box.

- **Demo File – `UsePromiseDemo.js`:**  
  A simple component that receives `users` as a prop and displays them in a list.
```jsx
   export default async function UsePromiseDemo() {

    // add this to simulate a slow network
  await new Promise((resolve) => setTimeout(resolve, 2000));
  const data = await fs.readFile("dummy-db.json", "utf-8");
  const users = JSON.parse(data);
  return (
    <div className="rsc">
      <h2>RSC with Data Fetching</h2>
      <p>
        Uses <strong>async / await</strong> for data fetching.
      </p>
      <ul>
        {users.map((user) => (
          <li key={user.id}>
            {user.name} ({user.title})
          </li>
        ))}
      </ul>
    </div>
  );
}
```


- **Simulating Slow Data Fetching:**  
  - In my old setup in `page.js`, you used `await new Promise(resolve => setTimeout(resolve, 2000));` before reading the user data file, making the whole page hang for two seconds before anything rendered.
  - This leads to a bad UX, where the entire page is blank while the data loads.

- **Improving UX with Suspense and Data Fetching in Component:**  
  - Move the data fetching logic (with the artificial delay) from `page.js` into the server component, `UsePromiseDemo.js`.
  - In `page.js`, return:
    ```jsx
    <main>
      <Suspense fallback={<p>Loading Server Actions Demo...</p>}>
        <UsePromiseDemo />
      </Suspense>
    </main>
    ```
  - Now, React’s `Suspense` component kicks in, showing a loading state for just the data-dependent part of my UI, while the rest of the page can render immediately.
  - `Suspense` works here because server components and data fetching in Next.js are designed to work with React Suspense—they tell React when to show/load fallback UI.

- **Summary:**  
  - `use()` enables you to “await” certain Promises that are integrated with Suspense.
  - Suspense lets you wrap slow-to-load components or data in a fallback UI, rather than blocking the whole page.
  - Server components and data-fetching in Next.js fully support this pattern, leading to better user experience and incremental rendering.

**Bottom line:** This step is about learning to use the powerful `use()` hook and React Suspense together, allowing you to provide immediate feedback to users even when parts of my content take time to load.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qXeY5FSfmWjKXpyJBA6udU0R4aFSbl9wnQqO5" alt="use promis demo.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### use() in action

In this step, we are exploring how to combine Suspense, Promises, and the new React `use()` hook to enable client components to "await" data and still show a proper loading fallback—specifically in situations where you need client-side state (like a counter) and asynchronous data fetching.

Here’s what you’re doing:

1. **Why would you need the `use()` hook in a client component?**
   - Normally, only server components can be `async`.
   - But if you need to use client-only features (like `useState` for a counter), you must convert my component to a client component (`'use client'`).
   - When you do this and move the data fetching (like reading a file and waiting 2 seconds) back up to the page, Suspense won’t help—because the slow part is now outside the part wrapped by Suspense.

2. **What’s the solution? Pass a Promise as a prop and “await” it on the client using `use()`:**
   - In my `page.js`, create a Promise that reads the data after a delay.
   - Pass this Promise as a prop (`userPromise`) to my client component.
   - In my client component (`UsePromiseDemo`), use the `use()` hook to resolve the Promise and get the data:
     ```js
     "use client";
     import { use } from "react";
     import { useState } from "react";

     export default function UsePromiseDemo({ userPromise }) {
       const users = use(userPromise);
       const [counter, setCounter] = useState(0);

       return (
         <div>
           <p>
             Counter: {counter}
             <button onClick={() => setCounter(counter + 1)}>Increment</button>
           </p>
           {/* render users... */}
         </div>
       );
     }```
   - On the server, in `page.js`, do:
     ```js
     const fetchUsersPromise = new Promise((resolve) =>
       setTimeout(async () => {
         const data = await fs.readFile("dummy-db.json", "utf-8");
         const users = JSON.parse(data);
         resolve(users);
       }, 2000)
     );
     <UsePromiseDemo userPromise={fetchUsersPromise} />
     ```
   - Now, when the client component renders and calls `use(userPromise)`, React sees a Promise and coordinates the loading behavior with Suspense (so my fallback UI is displayed until the promise resolves).

3. **The result:**
   - The page loads immediately and shows my Suspense fallback while user data loads on the client.
   - When the data is ready, the client component renders with full interactivity (including my local state).

**Key concept:**  
The `use()` hook in client components lets you “await” React-compatible Promises and integrate with Suspense, unlocking incremental (non-blocking) client-side rendering—even for client components that need both data and interactive state. This is a modern pattern, but relies on React’s latest features and only works in supported setups (like Next.js 14/15+ and React 19+).

**Summary:**  
We’re demonstrating how the React `use()` hook smooths out loading flows for client components that fetch data, making Suspense work client-side too—so you can keep the UX snappy even with client-side interactivity and async data.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qbc1SQWYzC0UbepwGZEqnOvQ5go6BkMrPTW1t" alt="use hook in action.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />


## Summary

### Key Learnings

Here are the key services and concepts I learned in this project:

---

##### **React Server Components (RSC)**
- Components that run only on the server, never in the browser.
- Allow using Node.js APIs and server-side data fetching directly in components.
- Reduce bundle size and improve performance since code doesn’t ship to the client.

##### **Client Components**
- Render both on the server (for pre-rendering/SSR) and the client (for interactivity/hydration).
- Required for interactivity, state management (`useState`), and most React hooks.


##### **Combining Server & Client Components**
- Server components can include client components, but not vice versa (except as children).
- Understanding rules for where to define client/server logic and how to pass data down.

### **Data Fetching in Server Components**
- Using async/await and Node.js APIs directly from the component.
- No need for useEffect; data is fetched at render-time on the server.

### **Suspense & Asynchronous Data/UI**
- Using `<Suspense>` to provide loading fallbacks while waiting on async operations/data to resolve.
- Server components and suspense integrate for smooth loading experiences.

### **The `use()` Hook in React 19**
- Lets you "await" a promise directly in a component (especially in client components), pausing rendering until data arrives.
- Integrates with Suspense to show fallback UI while waiting.
- Only works with special "React-compatible" promises, often generated by frameworks or libraries with Suspense support.

### Unexpected Insights

One thing I didn’t expect in this project was how seamless (yet subtly complex) server actions and the use() hook integration would feel—until I ran into unexpected boundaries! For example, it surprised me that you can't define server actions inside client components even though you can pass and call them there, and that the use() hook only works with certain "suspense-aware" promises, not just any async function.

### Time Investment

2 hours.

### Motivation

Cuz I am cool.
