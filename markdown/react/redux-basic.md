# Redux and why redux

**Author:** Duc Thai

**Email:** ducthai060501@gmail.com

## Overview

Today, we’re here to dive into **Redux**, a state management tool for managing cross-component or global state in React applications.

### Here’s what we’re focusing on:
- Understanding what Redux is and why developers use it, especially compared to React Context.
- Recognizing that React Context is useful for avoiding prop drilling and managing state across components—but it can become complex or unwieldy in larger applications, especially when you have deeply nested context providers or a single provider that handles too many unrelated concerns.
- Seeing how Redux offers an alternative that can help organize and manage app-wide state, making code more maintainable in complex or enterprise-scale applications.
- Acknowledging that Redux is **not always necessary**: for small or medium apps, Context is often enough. But understanding Context’s limitations helps you pick the right tool.
- Noting you can technically use Context and Redux together, but typically one is enough for app-wide state.

**In summary:**  
Today’s goal is to understand the limitations of React Context in large, complex apps and learn how Redux provides a powerful alternative for scalable state management in React.

## Steps

### How does redux work?

In this step, we’re covering the **core concepts of how Redux works**:

---

### What are we doing?

- **Understanding the Central Store:**  
  Redux uses a single, central store to hold all cross-component or app-wide state. You will *never* have more than one store – everything goes into this one place.

- **State Subscription:**  
  Components don’t just randomly grab data—they *subscribe* to the store. When data changes, Redux notifies subscribing components so they can update themselves (i.e., re-render with the latest state).

- **Updating State with Actions & Reducers:**  
  Components are **not** allowed to directly modify the store!  
  Instead, components *dispatch actions*. An action is a plain JS object that describes **what should happen** (“add a user”, “log out”, etc).  
  - These actions get sent to a **reducer** function.  
  - The reducer is a pure function (not related to React’s `useReducer` hook, though similar conceptually), which takes the current state and the action, and returns a *new* state.  
  - Redux then replaces the state in the central store with this new state.

- **Reactive UI Updates:**  
  When the store’s state updates, all subscribed components are notified so their UI remains in sync with the state.

---

### Summary

We’re building the mental model of Redux as:
- A **single, central “database”** for all app/global state
- Components **subscribe** to state slices, and **dispatch actions** to request changes
- **Reducers** are the only things allowed to generate new store data, ensuring a predictable state change flow

**This is the foundation for how Redux enables maintainable, predictable state updates in large React applications!**

### Super basic redux

# Code Summary: Redux Fundamentals Implementation

## Changes Made

I have implemented a basic Redux counter example that demonstrates the core Redux workflow in vanilla JavaScript.

## What Was Built

**File**: index.js - A complete Redux counter implementation showing all fundamental Redux concepts

## Code Implementation Steps

### 1. **Reducer Function Created** (Step 2)
```javascript
// OLD: No reducer existed

// NEW: Pure reducer function handling state transitions
const counterReducer = (state = { counter: 0 } , action) => {
    if (action.type === 'increment') {
        return { counter: state.counter + 1 }
    }
    if (action.type === 'decrement') {
        return { counter: state.counter - 1 }
    }
    return state
}
```
**Why it matters**: The reducer is the heart of Redux - it's a pure function that takes current state and an action, then returns new state. Default state is `{ counter: 0 }`.

### 2. **Store Creation** (Step 1)
```javascript
// OLD: No store

// NEW: Centralized store with reducer
const store = redux.createStore(counterReducer);
```
**Why it matters**: Creates the single source of truth for application state.

### 3. **Subscription Setup** (Steps 3 & 4)
```javascript
// NEW: Subscriber function that logs state changes
const counterSubscriber = () => {
    const latestState = store.getState();
    console.log(latestState);   
}
store.subscribe(counterSubscriber);
```
**Why it matters**: Subscribers get notified whenever state changes, enabling reactive updates.

### 4. **Action Dispatching** (Step 5)
```javascript
// NEW: Dispatching actions to modify state
store.dispatch({ type: 'increment' })  // Output: { counter: 1 }
store.dispatch({ type: 'decrement' })  // Output: { counter: 0 }
```
**Why it matters**: Actions are the only way to trigger state changes in Redux.

### # Redux with react-redux

# Redux with react-redux
even tho this is depreacted, it good to see that happen at the ground up, later , we will use toolkit

## Changes Explained

I've implemented the core Redux functionality for the counter feature, transitioning from placeholder component logic to a fully functional Redux-powered state management system. This involved creating the Redux store with a counter reducer and connecting the `Counter` component to that store using React-Redux hooks.

## Code Changes

### 1. **Created Redux Store** (index.js)

**New Implementation:**
```javascript
// Created initial state
const initalState = {
  counter: 0,
  showCounter: true,
};

// Created reducer function with 4 action types
const counterReducer = (state = initalState, action) => {
  if (action.type === "increment") {
    return { counter: state.counter + 1, showCounter: state.showCounter };
  }
  if (action.type === "decrement") {
    return { counter: state.counter - 1, showCounter: state.showCounter };
  }
  if (action.type === "increase") {
    return { counter: state.counter + action.amount, showCounter: state.showCounter };
  }
  if (action.type === "toggle") {
    return { counter: state.counter, showCounter: !state.showCounter };
  }
  return state;
};

const store = createStore(counterReducer);
```

**Why it matters:** This establishes the single source of truth for counter state. The reducer handles four actions: increment (+1), decrement (-1), increase (by custom amount), and toggle (show/hide counter display).

**Key comments extracted:**
- "redux will not merge state updates for us" - I must return the entire state object, not just the changed slice
- "I must never mutate the existing state" - Always return new state objects to maintain Redux's immutability principle

### 2. **Connected Counter Component** (Counter.js)

**Old code (placeholder):**
```javascript
const toggleCounterHandler = () => {
  // Empty placeholder function
};
```

**New code (Redux-connected):**
```javascript
import { useSelector, useDispatch } from "react-redux";

const Counter = () => {
  const dispatch = useDispatch();
  
  const incrementHandler = () => {
    dispatch({ type: "increment" });
  };

  const increaseHandler = () => {
    dispatch({ type: "increase", amount: 5 });
  };

  const decrementHandler = () => {
    dispatch({ type: "decrement" });
  };

  const counter = useSelector((state) => state.counter);
  const show = useSelector((state) => state.showCounter);
  
  const toggleCounterHandler = () => {
    dispatch({ type: "toggle" });
  };
  
  return (
    // ...JSX with button handlers connected
  );
};
```

**Why it matters:** 
- `useSelector` extracts counter value and visibility state from Redux store, automatically subscribing to updates
- `useDispatch` provides the dispatch function to trigger state changes
- Event handlers now dispatch actions instead of being empty placeholders

**Key comments extracted:**
- "useSelector will automatically set up a subscription to the redux store" - Component re-renders when selected state changes
- "the function receives the entire redux store state as an argument" - I select specific slices from the full state

### 3. **Wrapped App with Redux Provider** (index.js)

**Old code:**
```javascript
root.render(<App />);
```

**New code:**
```javascript
import { Provider } from "react-redux";
import store from './store/index.js';

root.render(
  <Provider store={store}>
    <App />
  </Provider>
);
```

**Why it matters:** The `Provider` component makes the Redux store available to all components in the app through React context. This is required for `useSelector` and `useDispatch` hooks to work.

## Summary

I successfully implemented Redux state management for the counter feature using classic Redux patterns. The implementation follows Redux best practices: immutable state updates, action-based state changes, and React-Redux hooks for component integration. The counter now fully functions with increment, decrement, increase by 5, and toggle visibility features—all managed through the centralized Redux store.

### Redux toolkit

In this step, we're identifying some **limitations with the classic React-Redux setup** before moving on to better solutions (like Redux Toolkit). Here’s what we’re doing:

1. **Action type clutter:**  
   - As your app grows, you end up with lots of action type strings (identifiers like `"INCREMENT"`, `"DECREMENT"`), which can cause typos or inconsistencies and become hard to manage.

2. **Complex state management:**  
   - With more data and different pieces of state, your single Redux state object gets bigger and more complicated.
   - Updating one slice (e.g., counter) means you must remember to copy and preserve the rest of the state (`return { ...state, changedPart: newValue }`), which is repetitive and error-prone.
   - Your reducer function starts short, but as you add more state and actions, it becomes a huge, hard-to-maintain file.

3. **Bloated reducer logic:**  
   - Using many `if/else` or `switch` statements for each action type makes your reducer function long and messy.

**Preview of next step:**  
These are some pain points that Redux Toolkit helps solve! Moving forward, we’ll see how Redux Toolkit can simplify action creation, reducer logic, and state management, making Redux code much cleaner and more maintainable.

### Migrate to redux tool kit



## Migration to Redux Toolkit

I have 've **migrated from classic Redux to Redux Toolkit**, which is the modern, recommended approach for Redux state management. This involved refactoring the store structure, splitting state into separate slices, and implementing the authentication feature. The migration simplifies the code by eliminating boilerplate and leveraging Redux Toolkit's built-in helpers like `createSlice` and `configureStore`.

---

## Code Changes

### 1. **Store Refactored to Use Redux Toolkit** (index.js)

**Old code (Classic Redux):**
```javascript
import { createStore } from 'redux';

const initalState = { counter: 0, showCounter: true };

const counterReducer = (state = initalState, action) => {
  if (action.type === "increment") {
    return { counter: state.counter + 1, showCounter: state.showCounter };
  }
  if (action.type === "decrement") {
    return { counter: state.counter - 1, showCounter: state.showCounter };
  }
  // ...more if statements
  return state;
};

const store = createStore(counterReducer);
```

**New code (Redux Toolkit):**
```javascript
import { configureStore } from "@reduxjs/toolkit"
import counterReducer from "./counter"
import authReducer from "./auth"

// configureStore will create the store for us
// it want an object as an argument
const store = configureStore({
  // we can have multiple reducers here if we have multiple slices
  reducer: {
    counter: counterReducer,
    auth: authReducer
  }
});
```

**Why it matters:** `configureStore` replaces `createStore` and automatically sets up Redux DevTools extension and good default middleware. The store now combines multiple slice reducers instead of one monolithic reducer.

**Key comments extracted:**
- "configureStore will create the store for us"
- "we can have multiple reducers here if we have multiple slices"

---

### 2. **Counter Logic Split into Slice** (counter.js - NEW FILE)

**New implementation:**
```javascript
import { createSlice } from "@reduxjs/toolkit"

const initalCounterState = {
  counter: 0,
  showCounter: true,
};

// createSlice wwant an obj as aargument
// when we have diff stata that a re not related, we can create multiple slices
const counterSlice = createSlice({
  name: "counter",
  initialState: initalCounterState,
  reducers: {
    //we do have to write our own if checks for action types anymore
    increment(state) {
      //we are allowed to mutate the state here because under the hood,  
      //  toolkit use immerjs is used to make sure that the state is not mutated
      state.counter++;
    },
    decrement(state) {
      state.counter--;
    },
    increase(state, action){
      // defaultly, the action object will have a payload property
      state.counter = state.counter + action.payload;
    },
    toggleCounter(state) {
      state.showCounter = !state.showCounter;
    }
  }
})

export const counterActions = counterSlice.actions;
export default counterSlice.reducer;
```

**Why it matters:** 
- Eliminates string action types - `createSlice` auto-generates action creators
- Allows "mutating" syntax (`state.counter++`) thanks to Immer.js under the hood
- Exports `counterActions` object containing all action creators
- Much less boilerplate than classic Redux

**Key comments extracted:**
- "we do have to write our own if checks for action types anymore"
- "we are allowed to mutate the state here because under the hood, toolkit use immerjs"
- "defaultly, the action object will have a payload property"

---

### 3. **Authentication Slice Created** (auth.js - NEW FILE)

**New implementation:**
```javascript
import { createSlice } from "@reduxjs/toolkit";

const initalAuthState = {
  isAuthenticated: false
}

// creeate another slice for auth
const authSlice = createSlice({
  name: "auth",
  initialState: initalAuthState,
  reducers: {
    login(state){
      state.isAuthenticated = true;
    },
    logout(state){
      state.isAuthenticated = false;
    }
  }
});

export const authActions = authSlice.actions;
export default authSlice.reducer;
```

**Why it matters:** Separate slice for authentication state follows Redux best practice of organizing state by feature. Provides `login()` and `logout()` action creators.

**Key comments extracted:**
- "creeate another slice for auth"

---

### 4. **Counter Component Updated** (Counter.js)

**Old code (Classic Redux):**
```javascript
dispatch({ type: "increment" });
dispatch({ type: "increase", amount: 5 });
```

**New code (Redux Toolkit):**
```javascript
import { counterActions } from "../store/counter";

const incrementHandler = () => {
  dispatch(counterActions.increment());
};

const increaseHandler = () => {
  dispatch(counterActions.increase(5)); // payload automatically wrapped
};

// since we weork iwth multiple slices, we need to specify the slice we want first
const counter = useSelector((state) => state.counter.counter);
const show = useSelector((state) => state.counter.showCounter);
```

**Why it matters:** 
- No more string action types - uses type-safe action creators
- Payload automatically wrapped: `counterActions.increase(5)` creates `{ type: 'counter/increase', payload: 5 }`
- State access changed from `state.counter` to `state.counter.counter` (slice name + property)

**Key comments extracted:**
- "since we weork iwth multiple slices, we need to specify the slice we want first"

---

### 5. **Header Component Implemented** (Header.js)

**Old code:**
```javascript
// Empty component, no Redux integration
```

**New code:**
```javascript
import { authActions } from "../store/auth";
import { useSelector, useDispatch } from "react-redux";

const Header = () => {
  const isAuth = useSelector((state) => state.auth.isAuthenticated);
  const dispatch = useDispatch()
  
  const logoutHandler = () => {
    // Dispatch logout action here
    dispatch(authActions.logout());
  }
  
  return (
    <header className={classes.header}>
      <h1>Redux Auth</h1>
      {isAuth && (
        <nav>
          {/* Navigation links */}
          <button onClick={logoutHandler}>Logout</button>
        </nav>
      )}
    </header>
  );
};
```

**Why it matters:** Header now conditionally renders navigation based on auth state and handles logout functionality.

**Key comments extracted:**
- "Dispatch logout action here"

---

### 6. **Auth Component Implemented** (Auth.js)

**Old code:**
```javascript
const loginHandler = (event) => {
  event.preventDefault();
  // Empty placeholder
}
```

**New code:**
```javascript
import { authActions } from '../store/auth';

const loginHandler = (event) => {
  event.preventDefault();
  // Dispatch login action here
  dispatch(authActions.login());
}
```

**Why it matters:** Login form now dispatches the login action when submitted.

**Key comments extracted:**
- "Dispatch login action here"

---

### 7. **App Component Enhanced** (App.js)

**New code:**
```javascript
const isAuth = useSelector((state) => state.auth.isAuthenticated);

return (
  <Fragment>
    <Header />
    {isAuth && <UserProfile />}
    {!isAuth && <Auth />}
    <Counter />
  </Fragment>
);
```

**Why it matters:** App now conditionally renders `UserProfile` or `Auth` component based on authentication state, creating a complete login/logout flow.

---

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qPRDvHTjjC5wu9q0fTENv2DlbdcWI7QYhkryR" alt="redux auth.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

## Summary

### Key Learnings

**In summary:**  
This project gave hands-on experience with both classic Redux and modern Redux Toolkit, focusing on efficient global state management, best practices for modularity, and clean integration between Redux and React using hooks and slices.

### Unexpected Insights

One thing I didn’t expect in this project was just how much **Redux Toolkit** would simplify and modernize state management compared to classic Redux. I knew it would reduce boilerplate, but seeing features like `createSlice` (with autogenerated actions and reducers), the use of Immer under the hood for safe "mutating" syntax, and the built-in TypeScript support in practice really highlighted how much easier and more maintainable it makes Redux code—even as your state grows or you split logic into separate slices.

Another unexpected aspect was experiencing firsthand how quickly classic Redux with many actions and a single big reducer can become unmanageable, making the value of toolkits, modular slices, and best-practice patterns immediately clear. The ergonomic improvement was bigger than I anticipated!

### Time Investment

3 hours.

### Motivation

Cuz I am soo cool.
