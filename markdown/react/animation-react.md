
# Animating a React App

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

## Table of Contents

1. Project Setup & Overview
2. Animating with CSS Transitions
3. Animating with CSS Animations
4. Introducing Framer Motion
5. Framer Motion Basics & Fundamentals
6. Animating Between Conditional Values
7. Adding Entry Animations
8. Animating Element Disappearances / Removal
9. Making Elements “Pop” With Hover Animations
10. Reusing Animation States
11. Nested Animations & Variants
12. Animating Staggered Lists
13. Animating Colors & Working with Keyframes
14. Imperative Animations
15. Animating Layout Changes
16. Orchestrating Multi-Element Animations
17. Combining Animations With Layout Animations
18. Animating Shared Elements
19. Re-triggering Animations via Keys
20. Scroll-based Animations
21. Summary & Key Learnings

---

## Project Setup & Overview

- **Goal:** Learn to animate a React app for better user engagement.
- **Starting Point:** Static React 19 + Vite challenge tracker app using React Router and Context API.
- **Features:** Create, view, and manage challenges (title, description, deadline, image, status).
- **Routing:** Welcome page (`/`) and Challenges page (`/challenges`).
- **State Management:** Context API (`ChallengesContext`).
- **Status:** `'active'`, `'completed'`, or `'failed'`.
- **ID Generation:** `Math.random().toString()` (for demo only).

![Starting Animation](https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qHZhRnDxc7W6mCxeoYTjKwzbhDUXrgQi2GRZI)

---

## Animating with CSS Transitions

- **Objective:** Add smooth rotation to the details icon using CSS transitions.
- **CSS:**
  ```css
  .challenge-item-details-icon {
    transition: transform 0.3s ease;
  }
  .challenge-item-details.expanded .challenge-item-details-icon {
    transform: rotate(180deg);
  }
  ```
- **React:**
  ```jsx
  <div className={`challenge-item-details ${isExpanded ? 'expanded' : ''}`}>
    <button onClick={onViewDetails}>
      View Details <span className="challenge-item-details-icon">&#9650;</span>
    </button>
    ...
  </div>
  ```
- **Result:** Icon rotates on expand/collapse.

![Expand CSS Animation](https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qfERUmA5Dxo48VaHKMDyTZCRwkeB6cpPb7jJ1)
![View Details Button](https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qJstLOQcS4CcwjaphiUOfP6SmHtINxXzdEeYn)

---

## Animating with CSS Animations

- **Objective:** Animate modal entrance using CSS keyframes.
- **CSS:**
  ```css
  .modal {
    animation: slide-up-fade-in 0.3s ease-out forwards;
  }
  @keyframes slide-up-fade-in {
    0% { transform: translateY(30px); opacity: 0; }
    100% { transform: translateY(0); opacity: 1; }
  }
  ```
- **Result:** Modal animates in on mount (no exit animation).

![New Challenges](https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qyRutMB2N25h6fmu1vGkXCBc39rE4jgSLqTel)

---

## Introducing Framer Motion

- **Objective:** Move beyond CSS for more complex, interactive animations.
- **Demo:** Animated box using Framer Motion.
- **Steps:**
  1. Import `motion` from Framer Motion.
  2. Replace `<div>` with `<motion.div>`.
  3. Use `animate` and `transition` props for smooth movement.

---

## Framer Motion Basics & Fundamentals

- **Example:** Animate icon rotation in `ChallengeItem.jsx`.
- **Code:**
  ```jsx
  import { motion } from 'motion/react';
  <motion.span
    animate={{ rotate: isExpanded ? 180 : 0 }}
    className="challenge-item-details-icon">&#9650;</motion.span>
  ```
- **Result:** Icon rotates smoothly on expand/collapse.

---


## Animating Between Conditional Values

- **Objective:** Animate UI changes based on state.
- **Example:** Icon rotation using Framer Motion’s `animate` prop.

```jsx
// Animate icon rotation based on expanded state
<motion.span
  animate={{ rotate: isExpanded ? 180 : 0 }}
  className="challenge-item-details-icon"
>
  &#9650;
</motion.span>
```

- **Result:** Declarative, state-driven animation.

---

## Adding Entry Animations

- **Objective:** Animate modal entrance using Framer Motion.
- **Code:**
  ```jsx
  <motion.dialog
    open
    className="modal"
    animate={{ opacity: 1, y: 0 }}
    initial={{ opacity: 0, y: 30 }}
  >
    ...
  </motion.dialog>
  ```
- **Result:** Modal fades in and slides up.

---

## Animating Element Disappearances / Removal

- **Objective:** Animate modal exit using Framer Motion’s `AnimatePresence`.
- **Modal.jsx:**
  ```jsx
  <motion.dialog
    open
    className="modal"
    animate={{ opacity: 1, y: 0 }}
    initial={{ opacity: 0, y: 30 }}
    exit={{ opacity: 0, y: 30 }}
  >
    ...
  </motion.dialog>
  ```
- **Header.jsx:**
  ```jsx
  <AnimatePresence>
    {isCreatingNewChallenge && <NewChallenge onDone={handleDone} />}
  </AnimatePresence>
  ```
- **Result:** Modal animates out smoothly on close.

---

## Making Elements “Pop” With Hover Animations

- **Objective:** Animate button scale on hover.
- **Code:**
  ```jsx
  <motion.button
    whileHover={{ scale: 1.1 }}
    transition={{ type: "spring", stiffness: 400, mass: 0.5 }}
    className="button"
  >
    Add Challenge
  </motion.button>
  ```
- **Result:** Button pops out on hover.

---

## Reusing Animation States

- **Objective:** Use Framer Motion’s `variants` for reusable animation states.
- **Code:**
  ```jsx
  <motion.dialog
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
    ...
  </motion.dialog>
  ```
- **Result:** Cleaner, maintainable animation logic.

---

## Nested Animations & Variants

- **Objective:** Inherit animation states in nested components.
- **Modal.jsx:** Variants set on ancestor.
- **NewChallenge.jsx:** Child `<motion.li>` uses variants.
  ```jsx
  <motion.li
    variants={{
      hidden: { opacity: 0, scale: 0.5 },
      visible: { opacity: 1, scale: 1, transition: { type: "spring" } }
    }}
    key={image.alt}
    ...
  >
    <img {...image} />
  </motion.li>
  ```
- **Result:** Nested elements animate in sync with parent.

---

## Animating Staggered Lists

- **Objective:** Animate list items with staggered entry.
- **Code:**
  ```jsx
  <motion.ul
    variants={{
      visible: { transition: { staggerChildren: 0.1 } }
    }}
    id="new-challenge-images"
  >
    ...
  </motion.ul>
  ```
- **Result:** List items animate one after another.

---

## Animating Colors & Working with Keyframes

- **Objective:** Animate color and scale with keyframes.
- **Button:**
  ```jsx
  whileHover={{
    scale: 1.1,
    backgroundColor: "#4b5563",
  }}
  ```
- **Image List:**
  ```jsx
  <motion.li
    variants={{
      hidden: { opacity: 0, scale: 0.5 },
      visible: { opacity: 1, scale: [0.8, 1.3, 1] },
      transition: { type: "spring" },
    }}
  >
  ```
- **Result:** Dynamic color and scale effects.

---

## Imperative Animations

- **Objective:** Trigger animations in response to user actions.
- **Code:**
  ```js
  const [scope, animate] = useAnimate();
  if (invalid) {
    animate(
      'input, textarea',
      { x: [0, -10, 10, -10, 10, 0] },
      { type: 'keyframes', duration: 0.2, delay: stagger(0.05) }
    );
    return;
  }
  <form ref={scope}>...</form>
  ```
- **Result:** Inputs shake on invalid submission.

---

## Animating Layout Changes

- **Objective:** Animate list item removal and tab switching.
- **Challenges.jsx:**
  ```jsx
  <AnimatePresence mode="wait">
    {displayedChallenges.length > 0 && (
      <motion.ol className="challenge-items" key="list">
        <AnimatePresence>
          {displayedChallenges.map((challenge) => (
            <ChallengeItem ... />
          ))}
        </AnimatePresence>
      </motion.ol>
    )}
    {displayedChallenges.length === 0 && (
      <motion.p
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        exit={{ opacity: 0, y: -20 }}
        key="fallback"
      >
        No challenges found.
      </motion.p>
    )}
  </AnimatePresence>
  ```
- **ChallengeItem.jsx:**
  ```jsx
  <motion.li layout exit={{ y: -30, opacity: 0 }}>
    <article className="challenge-item">...</article>
  </motion.li>
  ```
- **Result:** Smooth transitions for adding/removing items.

---

## Orchestrating Multi-Element Animations

- **Objective:** Animate tabs, badges, and challenge items together.
- **Tabs & Badges:**
  ```jsx
  <Badge key={badgeCaption} caption={badgeCaption}></Badge>
  {isSelected && <motion.div layout="tab-indicator" className="active-tab-indicator" />}
  ```
- **Result:** Shared layout and badge animations.

---


## Combining Animations With Layout Animations

- **Objective:** Use Framer Motion’s layout features for shared transitions.

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

- **Result:** Smooth movement of tab indicators and challenge items.

---


## Animating Shared Elements

- **Objective:** Animate elements that move between layouts (e.g., tab indicator).

```jsx
// Use layoutId for shared element transitions
<motion.div layoutId="tab-indicator" className="active-tab-indicator" />
```

- **Result:** Seamless transitions for shared UI elements.

---


## Re-triggering Animations via Keys

- **Objective:** Use React keys to re-trigger badge animations.

```jsx
// Use key to re-trigger badge animation when value changes
<Badge key={badgeCaption} caption={badgeCaption} />
```

- **Result:** Badge animates each time its value changes.

---

## Scroll-based Animations

- **Objective:** Animate landing page elements based on scroll position.
- **Code:**
  ```jsx
  const { scrollY } = useScroll();
  const opacityCity = useTransform(scrollY, [0, 200, 300, 500], [1, 0.5, 0.5, 0]);
  const yCity = useTransform(scrollY, [0, 200], [0, -100]);
  const yHero = useTransform(scrollY, [0, 300], [0, -50]);
  const opacityHero = useTransform(scrollY, [0, 300, 500], [1, 0.76, 0]);
  const scaletext = useTransform(scrollY, [0, 300], [1, 1.5]);
  const yText = useTransform(scrollY, [0, 200, 300, 500], [0, 50, 50, 300]);

  <motion.div id="welcome-header-content" style={{ scale: scaletext, y: yText }}>
    <h1>Ready for a challenge?</h1>
    <Link id="cta-link" to="/challenges">Get Started</Link>
  </motion.div>
  <motion.img style={{ opacity: opacityCity, y: yCity }} src={cityImg} ... />
  <motion.img style={{ y: yHero, opacity: opacityHero }} src={heroImg} ... />
  ```
- **Result:** Parallax effect and dynamic landing page.

![Main Page](https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qa22O4htdpoa0Rj23UWQDuHKPwhigkJ7OfnNC)
![City Disappear](https://xjoit2fax3.ufs.sh/f/xY7L9K0z7b4qVE7NZmiJkX2Nb0tMKUHmf86soRSCTWVOnPrc)

---

## Summary & Key Learnings

### Key Concepts

- **React Animation Fundamentals:** CSS transitions, Framer Motion, Context API, React Router.
- **Framer Motion:** Motion components, variants, hooks (`useAnimate`, `useScroll`, `useTransform`).
- **Imperative vs. Declarative Animation:** When to use each approach.
- **Layout & Shared Element Animation:** Smooth transitions for tabs, lists, and items.
- **Best Practices:** Avoid flicker, use keys and layout features for timing.

### Unexpected Insights

- Small details (order of AnimatePresence, keys, transition props) can cause subtle animation bugs.
- Debugging animation glitches often requires deep dives into documentation.

### Time Investment

- Many hours spent practicing and refining UI animation.

### Motivation

- For a job interview.

---

**Final Code:** [GitHub - animation-react/tree/animation](https://github.com/vvduth/animation-react/tree/animation)  
**Start Code:** [GitHub - animation-react/tree/main](https://github.com/vvduth/animation-react/tree/main)

---
