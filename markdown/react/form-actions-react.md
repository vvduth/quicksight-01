# Form Actions in React

---

## Introduction to Form Actions

The Form Actions feature is a new capability built into React that simplifies form submission handling. This feature provides a more streamlined approach to managing form data and validation.

---

## Requirements

To use Form Actions, you need:
- React version 19 or higher
- Check your `package.json` file to verify your React version

```json
{
  "dependencies": {
    "react": "^19.0.0"
  }
}
```

If you are using an older version of React, you cannot use this feature and must rely on traditional form handling methods.

---

## Traditional Form Handling

In previous React versions, you would handle form submissions using the `onSubmit` prop:

```jsx
function handleSubmit(event) {
  event.preventDefault();
  // Handle the form submission logic here
}

<form onSubmit={handleSubmit}>
  {/* Form fields go here */}
</form>
```

**Note:** You can still use this approach. There is nothing wrong with it.

---

## Using the Action Prop

### What Changed

If you are using React version 19 or higher, you can use the `action` prop on the form element instead of `onSubmit`.

```jsx
<form action={handleSubmit}>
  {/* Form fields go here */}
</form>
```

### Understanding the Action Prop

The `action` prop is not really a new prop. Form elements have always supported the action attribute, whether you are using React or not.

- **Without React:** The action attribute defines the path or URL to which the browser sends the form data when the form is submitted.
- **With React:** You were preventing this default behavior by calling `event.preventDefault()`.

### Key Difference

When using the `action` prop with React 19+, the function signature changes:

**Before:**
```jsx
function handleSubmit(event) {
  event.preventDefault();
  // Handle the form submission logic here
}
```

**After:**
```jsx
function handleSubmit(formData) {
  // formData is a FormData object created by React
  // It contains all the form fields and their values
}
```

**Important:** Make sure you add the `name` prop to all your form fields (input, select, textarea, etc.).

---

## Validation with Form Actions

There are many different ways to validate form data in React. Let's try a simple approach using an error array to store error messages.

### Basic Validation Example

Inside the `handleSubmit` function:

```jsx
function handleSubmit(formData) {
  let errors = [];
  
  if (!formData.get('username')) {
    errors.push('Username is required.');
  }
  
  // Add more validation rules here
  
  if (errors.length > 0) {
    return { errors: errors };
  }
  
  return { errors: null };
}
```

---

## Using useActionState Hook

To access the errors returned from the action function, you can use the `useActionState` hook from React.

### Hook Setup

```jsx
const [formState, formAction, isPending] = useActionState(signUpActions, { errors: null });
```

### Parameters

The `useActionState` hook takes 2 parameters:
1. The action function (e.g., `signUpActions`)
2. The initial state (e.g., `{ errors: null }`)

### Return Values

`useActionState` returns an array with 3 elements:
- **formState:** In our case, this will be `{ errors: [...] }`
- **formAction:** The action function enhanced with more features
- **isPending:** A boolean indicating whether the form submission is in progress

### Updating the Form

You should set the `formAction` returned by `useActionState` as the action prop of the form element.

**Before:**
```jsx
<form action={signUpActions}>
```

**After:**
```jsx
<form action={formAction}>
```

---

## Displaying Error Messages

To display validation errors to the user:

```jsx
{formState.errors && (
  <ul className="errors">
    {formState.errors.map((error, index) => (
      <li key={index}>{error}</li>
    ))}
  </ul>
)}
```

---

## Troubleshooting Common Errors

### Error: formData.get is not a function

When you first try to submit an empty form, you might encounter this error:

```
Signup.jsx:10 Uncaught TypeError: formData.get is not a function
  at signUpActions (Signup.jsx:10:37)
```

### Why This Error Occurs

`useActionState` calls the action function in a different way:

- **Without useActionState:** The action function receives `formData` as the first parameter.
- **With useActionState:** The action function receives `prevFormState` as the first parameter and `formData` as the second parameter.

### Solution

Update your function signature:

```jsx
const signUpActions = (prevFormState, formData) => {
  // Now formData is the second parameter
  let errors = [];
  
  if (!formData.get('username')) {
    errors.push('Username is required.');
  }
  
  // Rest of your validation logic
};
```

---

## Improving User Experience

### Preserving Valid Input Values

Instead of only returning errors, you can return the entered values along with errors to repopulate the form. This prevents users from losing their valid input when validation fails.

```jsx
const signUpActions = (prevFormState, formData) => {
  let errors = [];
  
  const enteredEmail = formData.get('email');
  const firstName = formData.get('firstName');
  const lastName = formData.get('lastName');
  const role = formData.get('role');
  const acquisitionChannel = formData.get('acquisition');
  const term = formData.get('terms');
  const password = formData.get('password');
  const confirmPassword = formData.get('confirmPassword');
  
  // Validation logic here
  
  if (errors.length > 0) {
    // Return entered values along with errors to repopulate the form
    return {
      errors: errors,
      enteredValues: {
        email: enteredEmail,
        firstName: firstName,
        lastName: lastName,
        role: role,
        acquisition: acquisitionChannel,
        terms: term ? true : false,
        password: password,
        confirmPassword: confirmPassword,
      },
    };
  }
  
  return { errors: null };
};
```

### Repopulating Form Fields

Change the input fields to use `defaultValue` to set the initial value:

```jsx
<input 
  id="email" 
  type="email" 
  name="email" 
  defaultValue={formState.enteredValues?.email}
/>
```

This approach ensures that valid input is not lost when the form is revalidated.

---

## Summary

### Key Takeaways

1. **Form Actions** are available in React 19+ and provide a streamlined way to handle form submissions.
2. Use the `action` prop instead of `onSubmit` to leverage this new feature.
3. The action function receives a `FormData` object directly, not an event object.
4. The `useActionState` hook helps manage form state and validation errors.
5. When using `useActionState`, the action function signature changes to `(prevFormState, formData)`.
6. Always add `name` props to your form fields.
7. Return entered values along with errors to improve user experience by repopulating the form with valid data.
8. Use `defaultValue` on input fields to maintain user input during revalidation.

### Benefits

- Cleaner code structure
- Built-in form data handling
- Easier state management
- Better user experience with preserved valid input 

