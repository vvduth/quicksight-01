# Animating React App

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

## Table of Contents

1. [Overview](#overview)
2. [Project Setup & Overview](#project-setup--overview)
3. [Animating with CSS Transitions](#animating-with-css-transitions)
4. [Animating with CSS Animations](#animating-with-css-animations)
5. [Introducing Framer Motion](#introducing-framer-motion)
6. [Framer Motion Basics & Fundamentals](#framer-motion-basics--fundamentals)
7. [Animating Between Conditional Values](#animating-between-conditional-values)
8. [Adding Entry Animations](#adding-entry-animations)
9. [Animating Element Disappearances / Removal](#animating-element-disappearances--removal)
10. [Making Elements "Pop" With Hover Animations](#making-elements-pop-with-hover-animations)
11. [Reusing Animation States](#reusing-animation-states)
12. [Nested Animations & Variants](#nested-animations--variants)
13. [Animating Staggered Lists](#animating-staggered-lists)
14. [Animating Colors & Working with Keyframes](#animating-colors--working-with-keyframes)
15. [Imperative Animations](#imperative-animations)
16. [Animating Layout Changes](#animating-layout-changes)
17. [Orchestrating Multi-Element Animations](#orchestrating-multi-element-animations)
18. [Combining Animations With Layout Animations](#combining-animations-with-layout-animations)
19. [Animating Shared Elements](#animating-shared-elements)
20. [Re-triggering Animations via Keys](#re-triggering-animations-via-keys)
21. [Scroll-based Animations](#scroll-based-animations)
22. [Summary](#summary)

---

## Overview

Today, my goal is to learn how to animate a React app to improve its look and feel, making it more engaging for users. I'll begin with a static, functioning app that looks decent but doesn't have any animations yet. My plan is to first enhance the UI with basic CSS animations and see how far that gets me. After exploring the limitations of CSS, I'll move on to using Framer Motion to build more complex animations and interactions. This step-by-step approach will help me understand both the fundamentals and advanced techniques for animating React applications.

**Final code:** https://github.com/vvduth/animation-react/tree/animation  
**Start code:** https://github.com/vvduth/animation-react/tree/main

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qHZhRnDxc7W6mCxeoYTjKwzbhDUXrgQi2GRZI" alt="starting animation.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Project Setup & Overview

In this step, I'm reviewing the starting project—a React 19 + Vite challenge tracker app that uses React Router for navigation. The app allows users to create, view, and manage personal challenges, each with a title, description, deadline, image, and status. There are two main pages: the Welcome page (`/`) and the Challenges page (`/challenges`), with routing defined in `src/App.jsx` using `createBrowserRouter`. The page layouts are located in `src/pages/`, while business logic is handled within `src/components/`.

### Project Architecture

For state management, the app uses the Context API, specifically the `ChallengesContext` in `src/store/challenges-context.jsx`. The context provider wraps only the `/challenges` route (set up in `pages/Challenges.jsx`), which keeps global state handling focused and efficient. Each challenge has a status of either `'active'`, `'completed'`, or `'failed'`, and IDs are generated with `Math.random().toString()`—which works for this learning project but wouldn't be safe for production. This overview helps me understand the current architecture and areas where animation could enhance user experience.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qrL5kwP6gjiQSLxEToX4su7hyD5dKwYVn9RPO" alt="challenge app.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Animating with CSS Transitions

In this step, I started with straightforward CSS-based animation to enhance the user experience. To enable a smooth rotation effect for the details icon, I added specific CSS transition rules in `index.css`.

### CSS Implementation

First, I defined a transition for the `.challenge-item-details-icon` class so that changes to the `transform` property would animate smoothly:

```css
.challenge-item-details-icon {
  transition: transform 0.3s ease;
}
```

Then, I added a rule for the expanded state, which uses the `expanded` class on the parent to trigger a rotation:

```css
.challenge-item-details.expanded .challenge-item-details-icon {
  transform: rotate(180deg);
}
```

### React Component Integration

With these styles, the details icon rotates when expanding or collapsing the challenge details, providing a visually pleasing cue to the user. This approach keeps things simple — there are no updates needed to the React component's logic for handling the animation; everything is done through CSS.

Inside `ChallengeItem.jsx`, I updated the class name dynamically based on the `isExpanded` prop:

```jsx
<div className={`challenge-item-details ${isExpanded ? 'expanded' : ''}`}>
  <p>
    <button onClick={onViewDetails}>
      View Details{' '}
      <span className="challenge-item-details-icon">&#9650;</span>
    </button>
  </p>
  ...
</div>
```

### Result

This setup ensures that the icon's rotation animation triggers only when the details section is expanded, leveraging CSS transitions for a simple and clean effect.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qfERUmA5Dxo48VaHKMDyTZCRwkeB6cpPb7jJ1" alt="expand css animation.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qJstLOQcS4CcwjaphiUOfP6SmHtINxXzdEeYn" alt="view details button.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Animating with CSS Animations

In this step, I added a CSS animation to the `.modal` class in index.css to create a smooth entrance effect for modal dialogs.

### CSS Keyframes Implementation

I added a CSS animation to the `.modal` class:

```css
.modal {
  top: 10%;
  border-radius: 6px;
  padding: 1.5rem;
  width: 30rem;
  max-width: 90%;
  z-index: 10;
  animation: slide-up-fade-in 0.3s ease-out forwards;
}

@keyframes slide-up-fade-in {
  0% {
    transform: translateY(30px);
    opacity: 0;
  }
  100% {
    transform: translateY(0);
    opacity: 1;
  }
}
```

### Code Review

- **Correctness:** The animation is applied directly to the modal dialog via the `.modal` class. When the modal mounts, it animates from slightly below and transparent to its final position and full opacity.
- **Style:** The animation is concise and leverages CSS keyframes for a smooth entrance effect. The use of `forwards` ensures the modal stays in its final state after the animation.
- **Performance:** The animation is lightweight and should not impact performance for a single modal.
- **Limitations:** The modal animates only on mount (open). There is no exit animation when the modal closes/unmounts. If I want a closing animation, I would need to manage the modal's visibility with state and delay unmounting until the animation completes.

### User Experience Impact

The animation makes modal interactions feel less abrupt and more modern, aligning with best practices for dialog presentation in web apps. No changes were made to the React component logic in Modal.jsx or NewChallenge.jsx regarding animation.

### Result

I added a CSS entrance animation to the modal dialog. This causes both the Modal and NewChallenge form to animate smoothly into view when opened, enhancing the app's visual polish and user experience. No exit animation is present.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qyRutMB2N25h6fmu1vGkXCBc39rE4jgSLqTel" alt="new challenges.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Introducing Framer Motion

In this step, I shifted focus away from my main project to experiment with Framer Motion in a simple demo outside the app's usual architecture. The original `App.jsx` featured a box controlled by three input values: x (left/right), y (up/down), and rotate (rotation angle), all managed with React state.

### Implementation Steps

To introduce animation, I made the following updates:

1. **Imported Framer Motion:** I added `import { motion } from 'framer-motion';` at the top of the file.

2. **Replaced the standard div with motion.div:** The box element (`<div id="box" />`) is now `<motion.div id="box" />`, enabling Framer Motion's animation capabilities.

3. **Added animation props:** I applied the `animate` prop to the box, connecting x, y, and rotate to their respective state values, so changes are smoothly animated.

4. **Configured the transition:** I included a `transition` prop to specify timing and easing, making the movement responsive and fluid.

### Result

With these changes, the demo box animates its position and rotation dynamically whenever the input values change—showcasing the ease and flexibility of Framer Motion for handling interactive UI animations in React.

---

## Framer Motion Basics & Fundamentals

I updated ChallengeItem.jsx to animate the expand/collapse icon using the `motion` library.

### What Was Changed

**Imported `motion` from `motion/react`:**

```jsx
import { motion } from 'motion/react';
```

This enables declarative animations in React components.

**Replaced static icon with animated icon:**

```jsx
<motion.span
  animate={{
    rotate: isExpanded ? 180 : 0, 
  }}
  className="challenge-item-details-icon">&#9650;</motion.span>
```

The icon now rotates 180 degrees when the details are expanded (`isExpanded` is `true`), and rotates back to 0 degrees when collapsed.

### Why I Made These Changes

- To provide a visual cue for the expand/collapse action, making the UI more interactive and intuitive.
- To leverage modern animation libraries for smooth, declarative UI transitions.

### Step-by-Step Implementation

1. **Install and Import Motion Library**
   - Ensure the `motion` library is installed in your project.
   - Import `motion` in ChallengeItem.jsx:
     ```jsx
     import { motion } from 'motion/react';
     ```

2. **Update the Details Icon**
   - Replace the static `<span>` for the expand/collapse icon with a `<motion.span>`.
   - Use the `animate` prop to rotate the icon based on the `isExpanded` prop:
     ```jsx
     <motion.span
       animate={{
         rotate: isExpanded ? 180 : 0, 
       }}
       className="challenge-item-details-icon">&#9650;</motion.span>
     ```

3. **Maintain BEM-style Class Naming**
   - The icon retains the `challenge-item-details-icon` class for consistent styling.

4. **No Changes to CSS Required**
   - The rotation is handled by the `motion` library, so no additional CSS transitions are needed for the icon.

### Summary

| File                | Change Made                                   | Effect on Animation Behavior                  |
|---------------------|-----------------------------------------------|-----------------------------------------------|
| ChallengeItem.jsx   | Animated details icon using `motion` library   | Icon rotates when details are expanded/collapsed |

I enhanced the expand/collapse interaction in ChallengeItem.jsx by animating the details icon with a rotation effect using the `motion` library. This improves the UI's responsiveness and user experience, following the project's conventions and architecture.

---

## Animating Between Conditional Values

In this step, I'm animating UI changes based on conditional state values using Framer Motion's `animate` prop.
I updated ChallengeItem.jsx to animate the expand/collapse icon using the `motion` library.

### Implementation

```jsx
// Animate icon rotation based on expanded state
<motion.span
  animate={{ rotate: isExpanded ? 180 : 0 }}
  className="challenge-item-details-icon"
>
  &#9650;
</motion.span>
```

### Result

This pattern lets you declaratively animate between states in React using Framer Motion. The icon rotation smoothly transitions based on the `isExpanded` state, providing clear visual feedback to users.

---

## Adding Entry Animations

I updated the `Modal` component to use the `motion` library for animating the modal dialog.

### Old Code

Previously, the modal dialog was rendered as a static `<dialog>` element without animation:

```jsx
import { createPortal } from "react-dom";
export default function Modal({ title, children, onClose }) {
  return createPortal(
    <>
      <div className="backdrop" onClick={onClose} />
      <dialog open className="modal">
        <h2>{title}</h2>
        {children}
      </dialog>
    </>,
    document.getElementById("modal")
  );
}
```

### New Code

I replaced the static dialog with a `motion.dialog` element and added animation props:

```jsx
import { createPortal } from "react-dom";
import { motion } from "motion/react";
export default function Modal({ title, children, onClose }) {
  return createPortal(
    <>
      <div className="backdrop" onClick={onClose} />
      <motion.dialog
        open
        className="modal"
        animate={{ opacity: 1, y: 0 }}
        initial={{ opacity: 0, y: 30 }}
      >
        <h2>{title}</h2>
        {children}
      </motion.dialog>
    </>,
    document.getElementById("modal")
  );
}
```

### Step-by-Step Changes

1. **Imported `motion` from `motion/react`.**
2. **Replaced `<dialog>` with `<motion.dialog>`** for declarative animation.
3. **Added `initial` and `animate` props** for entrance animation.

### Effect on Modal and NewChallenge Animation Behavior

- **Modal:** The modal now animates smoothly into view (fades in and slides up) when opened, instead of appearing instantly.
- **NewChallenge:** Since `NewChallenge` uses the `Modal` component, the new challenge form benefits from the same entrance animation.
- **No extra state or props needed:** Animation is handled by the `motion` library's props.

### Summary

| File         | Change Made                                 | Effect on Animation Behavior                |
|--------------|---------------------------------------------|---------------------------------------------|
| Modal.jsx    | Replaced `<dialog>` with `<motion.dialog>`  | Modal fades in and slides up on open        |
|              | Added `initial` and `animate` props         | No exit animation (modal closes instantly)  |

I enhanced the modal dialog with a smooth entrance animation using the `motion` library, improving user experience for both the modal and the new challenge form.

---

## Animating Element Disappearances / Removal

In this step, I'm focusing on animating the exit state of the modal dialog using Framer Motion.

### Changes in Modal.jsx

I updated the modal to use Framer Motion's `motion.dialog` and added animation props for the initial, animate, and exit states. This allows the modal to smoothly fade in and slide up when opened, and then fade out and slide down when closed. I used the `AnimatePresence` component to ensure the exit animation plays before the modal is removed from the DOM.

```jsx
// AnimatePresence is a special component from framer motion
// that helps to control the exit animation of a component

// notice the pattern here?
// initial for the initial state of the animation
// animate for the final state of the animation
// exit for the exit state of the animation
export default function Modal({ title, children, onClose }) {
  // we have no prop or state in here, we don't need them because framer
  // has some props to handle animation without state
  return createPortal(
    <>
      <div className="backdrop" onClick={onClose} />
      <motion.dialog
        open
        className="modal"
        animate={{ opacity: 1, y: 0 }} // animate prop to set the final state of the animation
        initial={{ opacity: 0, y: 30 }} // initial prop to set the initial state of the animation
        exit={{ opacity: 0, y: 30 }} // exit prop to set the exit state of the animation
      >
        <h2>{title}</h2>
        {children}
      </motion.dialog>
    </>,
    document.getElementById("modal")
  );
}
```

### Changes in Header.jsx

I wrapped the conditional rendering of `<NewChallenge />` in `AnimatePresence`. Without this, React would instantly remove the modal on exit, skipping the animation. `AnimatePresence` works around this by letting Framer Motion control the unmounting process, so the exit animation can play out.

```jsx
{/* if we do not use AnimatePresence, on the exit, new challenge still disappears instantly

react instantly removes the component from the dom

framer motion has a way to work around that, gives us special component to disable or control this function called AnimatePresence */}
<AnimatePresence>
  {isCreatingNewChallenge && <NewChallenge onDone={handleDone} />}
</AnimatePresence>
```

### Impact Analysis

- This update doesn't affect the underlying functionality or performance.
- All structural and logic aspects remain unchanged.
- The code is cleaner and more consistent.

### Summary

I've updated both Modal.jsx and Header.jsx to support animated exit transitions for modals using Framer Motion's `AnimatePresence`. The app now provides a polished modal experience where both the entrance and exit animations play smoothly, creating a modern and user-friendly dialog interaction.

---

## Making Elements "Pop" With Hover Animations

This time let's make the "Add challenge" button pop a little bit more when user hovers over it.

### Explanation of the Changes

I enhanced the "Add Challenge" button by switching it from a standard `<button>` to a Framer Motion `<motion.button>`. This enables a spring-based scale animation on hover, making the button visually pop out.

### Old Code

```jsx
import { AnimatePresence } from "motion/react";
import { useState } from "react";

import NewChallenge from "./NewChallenge.jsx";

export default function Header() {
  const [isCreatingNewChallenge, setIsCreatingNewChallenge] = useState();

  function handleStartAddNewChallenge() {
    setIsCreatingNewChallenge(true);
  }

  function handleDone() {
    setIsCreatingNewChallenge(false);
  }

  return (
    <>
      <AnimatePresence>
        {isCreatingNewChallenge && <NewChallenge onDone={handleDone} />}
      </AnimatePresence>

      <header id="main-header">
        <h1>Your Challenges</h1>
        <button onClick={handleStartAddNewChallenge} className="button">
          Add Challenge
        </button>
      </header>
    </>
  );
}
```

### New Code

```jsx
import { AnimatePresence, motion } from "motion/react";
import { useState } from "react";

import NewChallenge from "./NewChallenge.jsx";

export default function Header() {
  const [isCreatingNewChallenge, setIsCreatingNewChallenge] = useState();

  function handleStartAddNewChallenge() {
    setIsCreatingNewChallenge(true);
  }

  function handleDone() {
    setIsCreatingNewChallenge(false);
  }

  return (
    <>
      <AnimatePresence>
        {isCreatingNewChallenge && <NewChallenge onDone={handleDone} />}
      </AnimatePresence>

      <header id="main-header">
        <h1>Your Challenges</h1>
        <motion.button 
          onClick={handleStartAddNewChallenge}
          // I will not use animate here
          // because it would mean I would have to listen to 
          // onHoverStart and onHoverEnd event for the button to pop out a bit
          // that's a lot of work for a small effect

          // use whileHover instead for simplicity
          whileHover={{
            scale: 1.1
          }}
          // transition will control all the animation behavior applied on this element
          transition={{
            type: "spring",
            stiffness: 400,
            mass: 0.5,
          }}
          className="button"
        >
          Add Challenge
        </motion.button>
      </header>
    </>
  );
}
```

### Design Decisions

- I avoided using the `animate` prop for hover effects to keep the code simple.
- I explained that `whileHover` is more straightforward for this use case.
- I described how the `transition` prop customizes the spring animation.

### Summary

- **Button Enhancement:** The "Add Challenge" button now animates with a spring scale effect on hover, improving user experience.
- **No Functional Changes:** The core logic for challenge creation remains unchanged.

---

## Reusing Animation States

I refactored the modal animation logic to use Framer Motion's `variants` prop. This makes animation states reusable and more maintainable.

### Old Code

```jsx
<motion.dialog
  open
  className="modal"
  animate={{ opacity: 1, y: 0 }}
  initial={{ opacity: 0, y: 30 }}
  exit={{ opacity: 0, y: 30 }}
>
  <h2>{title}</h2>
  {children}
</motion.dialog>
```

### New Code

```jsx
<motion.dialog
  // useful for defining and reusing animation states
  variants={{
    hidden: { opacity: 0, y: 30 },
    visible: { opacity: 1, y: 0 }
  }}
  open
  className="modal"
  animate="visible"
  initial="hidden"
  exit="hidden"
>
  <h2>{title}</h2>
  {children}
</motion.dialog>
```

### Summary

- Switched to using `variants` for animation states.
- The code is now cleaner and easier to extend.
- Added a helpful comment for context.

---

## Nested Animations & Variants

In this step, variants can also be used to trigger animation deep inside the component tree by just setting animation to a certain variant on an ancestor component.

### Explanation

I refactored modal and image list animation logic to use Framer Motion's `variants` prop for both the modal and its child image list items. I added comments explaining how variants are inherited by children, and clarified why transition and exit props are set in specific ways to avoid flicker and backdrop delays.

```jsx
// modal is an ancestor component here that wraps children components
// in my case it will be NewChallenge component
// all the variants set in Modal component will be inherited by children components
// so in NewChallenge we don't need to set animate, initial, exit again
export default function Modal({ title, children, onClose }) {
  return createPortal(
    <>
      <div className="backdrop" onClick={onClose} />
      <motion.dialog
        // useful for defining and reusing animation states
        variants={{
          hidden: { opacity: 0, y: 30 },
          visible: { opacity: 1, y: 0 }
        }}
        open
        className="modal"
        animate="visible"
        initial="hidden"
        exit="hidden"
      >
        <h2>{title}</h2>
        {children}
      </motion.dialog>
    </>,
    document.getElementById("modal")
  );
}
```

### Old Modal Code

```jsx
<motion.dialog
  open
  className="modal"
  animate={{ opacity: 1, y: 0 }}
  initial={{ opacity: 0, y: 30 }}
  exit={{ opacity: 0, y: 30 }}
>
  <h2>{title}</h2>
  {children}
</motion.dialog>
```

### New Modal Code

```jsx
<motion.dialog
  // useful for defining and reusing animation states
  variants={{
    hidden: { opacity: 0, y: 30 },
    visible: { opacity: 1, y: 0 }
  }}
  open
  className="modal"
  animate="visible"
  initial="hidden"
  exit="hidden"
>
  <h2>{title}</h2>
  {children}
</motion.dialog>
```

### Old NewChallenge Image List Code

```jsx
{images.map((image) => (
  <li key={image.alt} ...>
    <img {...image} />
  </li>
))}
```

### New NewChallenge Image List Code

```jsx
{images.map((image) => (
  <motion.li
    // just have to define variants, no need to define animate, initial, exit again
    variants={{
      hidden: { opacity: 0, scale: 0.5 },
      visible: { opacity: 1, scale: 1, transition: { type: "spring" } }
    }}
    key={image.alt}
    onClick={() => handleSelectImage(image)}
    className={selectedImage === image ? "selected" : undefined}
  >
    <img {...image} />
  </motion.li>
))}
```

### Important Notes

- I just have to define variants, no need to define animate, initial, exit again
- Add exit visible to prevent flicker when modal closes
- It overrides the exit in Modal component
- If we don't do this, when modal closes, it will wait for all the items in li to exit first, then modal exits, causing flicker effect
- I must not use variant name in this case because in Modal component we already set animate, initial, exit with variant names
- Have to use value directly: `exit={{ opacity: 1, scale: 1 }}`

**UPDATE:** In the later version of motion, if we keep the exit and `transition: { type: 'spring' }` to the whole motion.li, after closing the modal my backdrop would not go away.

Setting the transition attribute in the motion.li element means that element will enter AND EXIT using those transition properties. The added bounce that is created by the "spring" animation (which looks great on entry) is what is causing the delay on the backdrop's disappearance. We can't see it happening, but Framer Motion is waiting for the children elements to finish bouncing before removing the backdrop.

So instead of adding `transition: { type: 'spring' }` to the whole motion.li attribute, simply add it to the visible variant (as shown in the code above). That means motion.li elements will only "spring" on entry. Then I can simply delete the `exit={{ opacity: 1, scale: 1 }}` which for some reason is breaking the backdrop.

### Summary

Variants let you control animation of nested components just by setting animation states on an ancestor. The modal's variants are inherited automatically, so you don't have to repeat animate/initial/exit setup in deep children. I only attach transition to entry, not exit, in motion.li for smoother closing and fixing backdrop flickers/delays.

---

## Animating Staggered Lists

In this step, I'm animating a staggered list of images—basically making each image in the list animate one after the other instead of all at once.

### Implementation

To achieve this, I added Framer Motion's `staggerChildren` property to the `visible` variant of the parent `<motion.ul>`:

```jsx
{/* use staggerChildren to animate list items */}
<motion.ul
  variants={{
    visible: {
      transition: {
        staggerChildren: 0.1,
      },
    },
  }}
  id="new-challenge-images"
>
  ...
</motion.ul>
```

### Result

With this setup, when the image list becomes visible, each list item's animation gets triggered with a slight delay (`0.1` seconds) after the previous one—creating a smooth, step-by-step entry effect for the images.

---

## Animating Colors & Working with Keyframes

In this step, I'm animating the color and scaling effect of the "Add Challenges" button using Framer Motion. For simplicity, I used the `whileHover` prop on the button, like this:

### Button Animation

```jsx
// use whileHover instead for simplicity
whileHover={{
  scale: 1.1,
  backgroundColor: "#4b5563",
}}
```

This makes the button smoothly scale up and changes its background color to dark grey when hovered.

### Image List Keyframes

Inside the NewChallenge component, I also updated `<motion.li>` for the image list to showcase keyframe animations using an array of values:

```jsx
<motion.li
  // just have to define variants, no need to define animate, initial, exit again
  variants={{
    hidden: { opacity: 0, scale: 0.5 },
    // we can use array as value to define keyframes
    visible: { opacity: 1, scale: [0.8, 1.3, 1] },
    transition: { type: "spring" },
  }}
>
```

### Result

With these changes, the scaling effect (keyframes) applies as soon as a list item enters. The updated button and animated images add a fun and dynamic feeling to the UI.

---

## Imperative Animations

In this step, I'm animating the form imperatively using Framer Motion's `useAnimate` hook—perfect for those cases where I want to trigger animation in response to specific user actions, rather than just on mount or hover.

### Implementation

I imported and declared `useAnimate` inside `NewChallenge.jsx`:

```js
// useAnimate is a low-level animation hook that gives you full control over animations in your components.
// scope is a ref that you can attach to any element you want to animate
// animate is a function that you can use to trigger animations on the scoped element or its children
const [scope, animate] = useAnimate();
```

In the `handleSubmit` function, right after checking if any required fields are missing, I triggered a shake animation on the input and textarea fields:

```js
if (
  !challenge.title.trim() ||
  !challenge.description.trim() ||
  !challenge.deadline.trim() ||
  !challenge.image
) {
  // use css selector to select input and textarea inside the scope element
  animate(
    'input, textarea',
    { x: [0, -10, 10, -10, 10, 0] }, // shake keyframes
    { type: 'keyframes', duration: 0.2, delay: stagger(0.05) } // transition settings
  );

  return;
}
```

And of course, I attached the scope ref to the form element:

```js
<form id="new-challenge" onSubmit={handleSubmit} ref={scope}>
```

### Result

With this setup, every time the user tries to submit the form without proper input, the input fields and textarea shake a little bit, giving instant feedback in a fun way. This is a cool example of how you can use `useAnimate` for imperative, targeted animations right inside your component logic.

---

## Animating Layout Changes

In this step, I'm working on animated challenges viewing—so when users view all active challenges, mark them as completed or failed, or switch tabs between completed and failed challenges, the UI transitions feel smooth instead of instantly snapping.

First, I updated the way active challenges are rendered in `Challenges.jsx`. The idea is, when a user marks a challenge as completed or failed, the challenge item "snaps out" of the list, but with smoother motion thanks to Framer Motion's exit animations.

### Old Code (List Rendering)

```jsx
<ol className="challenge-items">
  {displayedChallenges.map((challenge) => (
    <ChallengeItem
      key={challenge.id}
      challenge={challenge}
      onViewDetails={() => handleViewDetails(challenge.id)}
      isExpanded={expanded === challenge.id}
    />
  ))}
</ol>
```

### New Code (Animated List Rendering)

```jsx
<AnimatePresence
  // add mode wait to ensure exit animations complete before entering animations start 
  mode="wait"
>
  {displayedChallenges.length > 0 && (
    <motion.ol className="challenge-items"
      // add a key to help framer motion to tell the component apart when the list changes 
      key="list"
    >
      <AnimatePresence>
        {displayedChallenges.map((challenge) => (
          <ChallengeItem
            key={challenge.id}
            challenge={challenge}
            onViewDetails={() => handleViewDetails(challenge.id)}
            isExpanded={expanded === challenge.id}
          />
        ))}
      </AnimatePresence>
    </motion.ol>
  )}

  {displayedChallenges.length === 0 && <motion.p 
    initial={{ opacity: 0, y: -20 }}
    animate={{ opacity: 1, y: 0 }}
    exit={{ opacity: 0, y: -20 }}
    key={"fallback"}>No challenges found.</motion.p>}
</AnimatePresence>
```

### Old ChallengeItem Code

```jsx
<li>
  <article className="challenge-item">
    ...
  </article>
</li>
```

### New ChallengeItem Code

```jsx
// add layout prop to enable layout animations
<motion.li layout exit={{
  y: -30, opacity: 0
}}>
  <article className="challenge-item">
    ...
  </article>
</motion.li>
```

### Key Points

- Add mode wait to ensure exit animations complete before entering animations start
- Add a key to help framer motion to tell the component apart when the list changes
- Add layout prop to enable layout animations

### Result

With these changes, when I mark a challenge as completed or failed, the list item animates out smoothly (yanking up and fading out), and switching tabs between challenge states gets a polished transition rather than a sudden snap. All thanks to AnimatePresence, the layout prop, and proper exit animations!

---

## Orchestrating Multi-Element Animations

In this step, I updated the Challenge Tabs, Challenge List, and Challenge Items to use enhanced animations and layout transitions with Framer Motion.

### Explanation

I improved the UI by combining Framer Motion's animation tools with layout animations, shared element transitions, and key-based animation re-triggers. The tabs system now has an animated active indicator using the shared `layout` prop, badges re-animate whenever their count changes thanks to their `key`, and the challenge list and items use layout and exit animations for smoother transitions when items are added, removed, or re-ordered.

### Old Tab & Badge Code

```jsx
<button
  className={isSelected ? 'selected' : undefined}
  onClick={onSelect}
>
  {children}
  <Badge caption={badgeCaption}></Badge>
</button>
```

### New Tab & Badge Code

```jsx
<button
  className={isSelected ? 'selected' : undefined}
  onClick={onSelect}
>
  {children}
  {/* key here will act as a unique identifier for the Badge component */}
  {/* when the value of the key changes, react will destroy the old component and render a new one
      triggering the animation each time the badgeCaption changes
  */}
  <Badge key={badgeCaption} caption={badgeCaption}></Badge>
</button>
{isSelected && <motion.div layout="tab-indicator" className="active-tab-indicator" />}
```

### Old ChallengeItem Code

```jsx
<li>
  <article className="challenge-item">
    ...
  </article>
</li>
```

### New ChallengeItem Code

```jsx
// add layout prop to enable layout animations
<motion.li layout exit={{
  y: -30, opacity: 0
}}>
  <article className="challenge-item">
    ...
  </article>
</motion.li>
```

### Key Points

- Key here will act as a unique identifier for the Badge component
- When the value of the key changes, react will destroy the old component and render a new one triggering the animation each time the badgeCaption changes
- Add layout id for framer motion to enable shared layout animations
- Add layout prop to enable layout animations

### Summary

- Combined Framer Motion's animation and layout features for smooth, modern UI transitions.
- Used `layout` for the shared tab indicator to animate its movement.
- Re-triggered badge animation when its value changes by adjusting its `key`.
- Enabled challenge item layout and exit animations for polished list updates.
- Added comments in the code clarifying animation logic and best practices.

---

## Combining Animations With Layout Animations

In this step, I'm combining Framer Motion's animation capabilities with layout animations for seamless transitions.

### Implementation

```jsx
// Animate tab indicator position with layout prop
{isSelected && (
  <motion.div layout className="active-tab-indicator" />
)}

// Animate challenge item layout changes
<motion.li layout exit={{ y: -30, opacity: 0 }}>
  <article className="challenge-item">...</article>
</motion.li>
```

### Result

The layout prop enables smooth transitions when elements move, appear, or disappear. Smooth movement of tab indicators and challenge items.

---

## Animating Shared Elements

In this step, I'm using Framer Motion's shared layout feature to animate elements that move between different positions or layouts.

### Implementation

```jsx
// Use layoutId for shared element transitions
<motion.div layoutId="tab-indicator" className="active-tab-indicator" />
```

### Result

Shared elements with the same layoutId animate smoothly between locations. Seamless transitions for shared UI elements like the tab indicator.

---

## Re-triggering Animations via Keys

In this step, I'm using React keys to force component remounts, which re-triggers their animations.

### Implementation

```jsx
// Use key to re-trigger badge animation when value changes
<Badge key={badgeCaption} caption={badgeCaption} />
```

### Result

Changing the key forces React to remount the component, triggering its animation again. The badge animates each time its value changes.

---

## Scroll-based Animations

In this step, I'm trying out **Scroll-Based Animation** on the Welcome page.

### Explanation

I implemented scroll-based animation using Framer Motion's `useScroll` and `useTransform` hooks to create a parallax effect. This means images and text on the landing page now move and fade dynamically as users scroll—making the landing experience more lively and interactive.

### Old Code (Static Images and Text)

```jsx
<header id="welcome-header">
  <h1>Ready for a challenge?</h1>
  <Link id="cta-link" to="/challenges">Get Started</Link>
  <img src={cityImg} alt="A city skyline" id="city-image" />
  <img src={heroImg} alt="A superhero" id="hero-image" />
</header>
```

### New Code (Animated with Scroll)

```jsx
const { scrollY } = useScroll();

const opacityCity = useTransform(scrollY, [0, 200, 300, 500], [1, 0.5, 0.5, 0]);
const yCity = useTransform(scrollY, [0, 200], [0, -100]);
const yHero = useTransform(scrollY, [0, 300], [0, -50]);
const opacityHero = useTransform(scrollY, [0, 300, 500], [1, 0.76, 0]);
const scaletext = useTransform(scrollY, [0, 300], [1, 1.5]);
const yText = useTransform(scrollY, [0, 200, 300, 500], [0, 50, 50, 300]);

<motion.div id="welcome-header-content"
  style={{ scale: scaletext, y: yText }}>
  <h1>Ready for a challenge?</h1>
  <Link id="cta-link" to="/challenges">Get Started</Link>
</motion.div>
<motion.img style={{ opacity: opacityCity, y: yCity }} src={cityImg} ... />
<motion.img style={{ y: yHero, opacity: opacityHero }} src={heroImg} ... />
```

### Key Concepts

- `useScroll` hooks help us track scroll position for potential animations
- Use transform to create a parallax effect on the city image
- `useTransform` takes three arguments: the value to transform, an array of input ranges, and an array of output ranges
- Scale down the city image slightly as we scroll down
- Move the city image up as we scroll down
- Move the hero image up slightly as we scroll down
- Slightly fade the hero image as we scroll down
- Scale up the text as we scroll down
- Move the text down as we scroll down

### Result

With these changes, the Welcome page gets a smooth parallax animation, with multiple elements reacting to scroll for an energetic, modern landing effect.

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qa22O4htdpoa0Rj23UWQDuHKPwhigkJ7OfnNC" alt="main page.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

<img src="https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qVE7NZmiJkX2Nb0tMKUHmf86soRSCTWVOnPrc" alt="city disappear.png" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Summary

### Key Learnings

Some of the key services and concepts I learned in this project:

- **React animation fundamentals:** How to improve user engagement and experience by incorporating animations into a React app, starting with simple CSS transitions and moving up to powerful libraries.
- **Framer Motion:** Using its motion components, variants, and hooks (`useAnimate`, `useScroll`, `useTransform`) to handle complex UI animation, exit states, imperative triggers, staggered lists, keyframe effects, scroll-based parallax, and more.
- **Context API:** Managing shared state for challenges with React Context, giving structure to the app's data flow.
- **React Router:** Implementing SPA navigation for seamless transitions between views (welcome page, challenges list, etc).
- **Imperative vs. Declarative Animation:** Choosing between pure CSS/reactive approaches and using hooks/functions to trigger animations on specific user interactions, like error feedback or scroll effects.
- **Layout and Shared Element Animation:** Making smooth transitions for tabs, lists, and items by combining Framer Motion's layout prop and shared layout IDs.
- **Best Practices in UI Animation:** Avoiding issues like flicker and backdrop delay, and understanding how to use keys and layout features to control animation timing.

Overall, this project helped me combine React fundamentals with modern animation techniques to create a more polished, interactive, and user-friendly web application.

### Unexpected Insights

One thing I didn't expect in this project was just how subtle and tricky UI animation bugs could be—especially when mixing exit animations with complex layouts or nested components. For example, sometimes the backdrop wouldn't disappear as expected, or list items would flicker if transition and exit settings weren't carefully managed. 

It surprised me how much small details, like the order of AnimatePresence, keys, or where to put the transition prop, could totally change the animation feel and even cause odd bugs. Debugging those nuanced animation glitches forced me to dig way deeper into Framer Motion's docs than I thought I'd need!

### Time Investment

A lot of hours, it's hard to document when practicing with UI.

### Motivation

For a job interview.
