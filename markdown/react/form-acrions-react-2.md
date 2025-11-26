
# Working with Async Form Actions in React

## Table of Contents
- [Working with Async Form Actions in React](#working-with-async-form-actions-in-react)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Setting Up Context for Backend Submission](#setting-up-context-for-backend-submission)
  - [Accessing Context in Components](#accessing-context-in-components)
  - [Using useFormStatus Hook](#using-useformstatus-hook)
    - [Important Constraints](#important-constraints)
    - [Implementation](#implementation)
  - [Registering Multiple Form Actions](#registering-multiple-form-actions)
    - [Setting Form Actions on Buttons](#setting-form-actions-on-buttons)
    - [Creating Action Functions](#creating-action-functions)
  - [Adding Backend Integration](#adding-backend-integration)
    - [Before](#before)
    - [After](#after)
  - [Improving User Experience with Disabled States](#improving-user-experience-with-disabled-states)
    - [Using useActionState](#using-useactionstate)
    - [Disabling Buttons](#disabling-buttons)
  - [Adding Optimistic Updates](#adding-optimistic-updates)
    - [Implementation](#implementation-1)
    - [Display Optimistic Vote](#display-optimistic-vote)
    - [How It Works](#how-it-works)
  - [Summary](#summary)
    - [Key Takeaways](#key-takeaways)
    - [Benefits](#benefits)
    - [Best Practices](#best-practices)

---

## Introduction

This guide covers working with asynchronous form actions in React, including submitting data to a backend, managing form state, and implementing optimistic updates for better user experience.

---

## Setting Up Context for Backend Submission

First, prepare a context to handle data submission to the backend. Here's an example with an Opinions context:

```jsx
import { createContext, useEffect, useState } from 'react';

export const OpinionsContext = createContext({
  opinions: null,
  addOpinion: (opinion) => {},
  upvoteOpinion: (id) => {},
  downvoteOpinion: (id) => {},
});

export function OpinionsContextProvider({ children }) {
  const [opinions, setOpinions] = useState();

  useEffect(() => {
    async function loadOpinions() {
      const response = await fetch('http://localhost:3000/opinions');
      const opinions = await response.json();
      setOpinions(opinions);
    }

    loadOpinions();
  }, []);

  async function addOpinion(enteredOpinionData) {
    const response = await fetch('http://localhost:3000/opinions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(enteredOpinionData),
    });

    if (!response.ok) {
      return;
    }

    const savedOpinion = await response.json();
    setOpinions((prevOpinions) => [savedOpinion, ...prevOpinions]);
  }

  function upvoteOpinion(id) {
    setOpinions((prevOpinions) => {
      return prevOpinions.map((opinion) => {
        if (opinion.id === id) {
          return { ...opinion, votes: opinion.votes + 1 };
        }
        return opinion;
      });
    });
  }

  function downvoteOpinion(id) {
    setOpinions((prevOpinions) => {
      return prevOpinions.map((opinion) => {
        if (opinion.id === id) {
          return { ...opinion, votes: opinion.votes - 1 };
        }
        return opinion;
      });
    });
  }

  const contextValue = {
    opinions: opinions,
    addOpinion,
    upvoteOpinion,
    downvoteOpinion,
  };

  return <OpinionsContext value={contextValue}>{children}</OpinionsContext>;
}
```

---

## Accessing Context in Components

Access the context in `Opinion.jsx` using the `use()` hook available in React 19:

```jsx
import { use } from 'react';
import { OpinionsContext } from './store/OpinionsContext';

// Inside your component
const { addOpinion, upvoteOpinion, downvoteOpinion } = use(OpinionsContext);
```

---

## Using useFormStatus Hook

You can use the `pending` state to check form submission status, but React DOM provides a better solution with `useFormStatus`.

### Important Constraints

- This hook **cannot** be used in the component that contains the form
- It can only be used in child components of the form component

### Implementation

Create a new component called `Submit.jsx`:

1. Cut the submit button from `NewOpinion.jsx`
2. Paste it in `Submit.jsx`

```jsx
import { useFormStatus } from "react-dom";

export default function Submit() {
  const { pending } = useFormStatus();
  
  return (
    <p className="actions">
      <button type="submit" disabled={pending}>
        {pending ? 'Submitting...' : 'Submit'}
      </button>
    </p>
  );
}
```

---

## Registering Multiple Form Actions

In `Opinion.jsx`, aside from adding opinions, we also want to handle upvote and downvote actions. We have 2 buttons in a form like this:

```jsx
<form className="votes">
  <button>
    upvote
  </button>

  <span>{votes}</span>

  <button>
    downvote
  </button>
</form>
```

### Setting Form Actions on Buttons

Form actions can be set inside the button element, not only on the form itself:

```jsx
<button formAction={upVoteAction}>
  upvote
</button>
<button formAction={downVoteAction}>
  downvote
</button>
```

### Creating Action Functions

We don't need `formData` here since no data is being sent:

```jsx
function upVoteAction() {
  // Action logic here
}

function downVoteAction() {
  // Action logic here
}
```

---

## Adding Backend Integration

Go to the context and add the code that sends requests to the backend.

### Before

```jsx
function downvoteOpinion(id) {
  setOpinions((prevOpinions) => {
    return prevOpinions.map((opinion) => {
      if (opinion.id === id) {
        return { ...opinion, votes: opinion.votes - 1 };
      }
      return opinion;
    });
  });
}
```

### After

```jsx
async function upvoteOpinion(id) {
  const response = await fetch(`http://localhost:3000/opinions/${id}/upvote`, {
    method: 'POST',
  });
  
  if (!response.ok) {
    return;
  }
  
  setOpinions((prevOpinions) => {
    return prevOpinions.map((opinion) => {
      if (opinion.id === id) {
        return { ...opinion, votes: opinion.votes + 1 };
      }
      return opinion;
    });
  });
}

async function downvoteOpinion(id) {
  const response = await fetch(`http://localhost:3000/opinions/${id}/downvote`, {
    method: 'POST',
  });
  
  if (!response.ok) {
    return;
  }
  
  setOpinions((prevOpinions) => {
    return prevOpinions.map((opinion) => {
      if (opinion.id === id) {
        return { ...opinion, votes: opinion.votes - 1 };
      }
      return opinion;
    });
  });
}
```

---

## Improving User Experience with Disabled States

Disable upvote and downvote buttons when the form is pending to prevent duplicate submissions.

### Using useActionState

We could move the buttons out of the form and use `useActionState` hook, but let's do it differently:

```jsx
// No need for initial formState because no data is being sent
// Even though we don't need formState, we still need to list it in the array
const [upvoteFormState, upVoteFormAction, upVoteFormPending] = useActionState(upVoteAction);
const [downvoteFormState, downVoteFormAction, downVoteFormPending] = useActionState(downVoteAction);
```

### Disabling Buttons

Set the `isPending` value to the `disabled` attribute of the button:

```jsx
<button formAction={upVoteFormAction} disabled={upVoteFormPending}>
  upvote
</button>
<button formAction={downVoteFormAction} disabled={downVoteFormPending}>
  downvote
</button>
```

---

## Adding Optimistic Updates

Use the `useOptimistic` hook from React to provide immediate feedback to users.

### Implementation

```jsx
import { useOptimistic } from 'react';

// useOptimistic updates votes immediately before server response
// The 2nd argument is a function that returns the new value
// mode is set by us
const [optimisticVote, setVotesOptimistic] = useOptimistic(
  votes,
  (prevVotes, mode) => (mode === "up" ? prevVotes + 1 : prevVotes - 1)
);

// No need for formData here since no data is being sent
async function upVoteAction() {
  // Call optimistic update before server call
  setVotesOptimistic("up");
  await upvoteOpinion(id);
}

async function downVoteAction() {
  setVotesOptimistic("down");
  await downvoteOpinion(id);
}
```

### Display Optimistic Vote

Use `optimisticVote` instead of `votes` in your JSX:

```jsx
<span>{optimisticVote}</span>
```

### How It Works

1. The votes change immediately when the button is clicked (optimistic update)
2. Wait for the server response
3. If successful, the context updates the votes again with the actual server data
4. If the server request fails, the optimistic update is automatically rolled back

---

## Summary

### Key Takeaways

1. **Context for Backend Integration:** Use React Context to manage API calls and state for operations like adding, upvoting, and downvoting opinions.

2. **useFormStatus Hook:** Access form submission status in child components to show loading states and disable submit buttons during pending operations.

3. **Multiple Form Actions:** Set different form actions on individual buttons using the `formAction` prop, enabling multiple actions within the same form.

4. **Backend Integration:** Convert synchronous state updates to asynchronous API calls with proper error handling.

5. **useActionState Hook:** Manage form action state and pending status for better control over button states during async operations.

6. **Optimistic Updates:** Use the `useOptimistic` hook to provide immediate UI feedback before server confirmation, improving perceived performance.

### Benefits

- Better user experience with immediate feedback
- Proper loading states prevent duplicate submissions
- Cleaner separation of concerns with Context API
- Optimistic updates make the app feel faster
- Multiple actions can coexist in the same form

### Best Practices

- Always handle error cases in async operations
- Provide visual feedback during pending states
- Use optimistic updates for actions that are likely to succeed
- Keep action functions focused and simple
- Leverage React 19 features like `use()` and `useOptimistic` for cleaner code