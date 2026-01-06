## what are refs in react?
* it is a speacical kind of object that react provides to allow us to access DOM elements direcrtly
* you can connect ref to jsx element via ref attribute
* ref alwasy have current property that points to the actual DOM element
* ref hae all the power of direct DOM manipulation but should be used sparingly

## Simllyfi component by usign ref

* original:

import { useState } from "react";

export default function Player() {
  const [enteredPlayerName, setEnteredPlayerName] = useState('')
  const [submitted, setSubmitted] = useState(false)

  function handleChange(event) {
    setEnteredPlayerName(event.target.value)
  }

  function handleClick()  {
    setSubmitted(true)
  }
  return (
    <section id="player">
      <h2>Welcome {submitted ? enteredPlayerName : "unknown entity"}</h2>
      <p>
        <input type="text"
          placeholder="Enter your name"
          value={enteredPlayerName}
          onChange={handleChange}
        />
        <button onClick={handleClick}>Set Name</button>
      </p>
    </section>
  );
}
