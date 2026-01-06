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

* using ref:
* export default function Player() {
  const playerNameRef = useRef(null);
  const [enteredPlayerName, setEnteredPlayerName] = useState("");

  function handleClick() {
    setEnteredPlayerName(playerNameRef.current.value);
  }
  return (
    <section id="player">
      <h2>
        Welcome {enteredPlayerName ? enteredPlayerName : "unknown entity"}
      </h2>
      <p>
        <input type="text" ref={playerNameRef} placeholder="Enter your name" />
        <button onClick={handleClick}>Set Name</button>
      </p>
    </section>
  );
}

=>REF CAN BE REALLY USEFUL
## some more action:

// clear input field after setting name
    playerNameRef.current.value = "";
* in react we should let react handle all the dom interaction, so the code above imight not be the best practice


## one more example of ref usage:

import React, { useState } from "react";

const TimerChallenges = ({ title, targetTime }) => {
  const [timerStarted, setTimerStarted] = useState(false);
  const [timerExpired, setTimerExpired] = useState(false);

  const handleStart = () => {
    setTimerStarted(true);
    setTimeout(() => {
      setTimerExpired(true);
    }, targetTime * 1000);
  };

  const handleStop = () => {
    
  }
  return (
    <section className="challenge">
      <h2>{title}</h2>
      {timerExpired && <p>You lost</p>}
      <p className="challenge-time">
        {targetTime} second{targetTime > 1 ? "s" : ""}
      </p>
      <p>
        <button onClick={handleStart}>
            {timerStarted ? "Stop" : "Start "} challenge
        </button>
      </p>
      <p className={timerStarted ? "active" : undefined}>
        {timerStarted ? "Time is running" : "Timer inactive"}
      </p>
    </section>
  );
};

export default TimerChallenges;


=> how can you stope timer using handleStop function? where timer is running handleStart function?

const TimerChallenges = ({ title, targetTime }) => {
  const timer = useRef(null);
  const [timerStarted, setTimerStarted] = useState(false);
  const [timerExpired, setTimerExpired] = useState(false);

  const handleStart = () => {
    setTimerStarted(true);
    timer.current = setTimeout(() => {
      setTimerExpired(true);
    }, targetTime * 1000);
  };

  const handleStop = () => {
    clearTimeout(timer.current);
  };
  return (
    <section className="challenge">
      <h2>{title}</h2>
      {timerExpired && <p>You lost</p>}
      <p className="challenge-time">
        {targetTime} second{targetTime > 1 ? "s" : ""}
      </p>
      <p>
        <button onClick={timerStarted ? handleStop : handleStart}>
          {timerStarted ? "Stop" : "Start "} challenge
        </button>
      </p>
      <p className={timerStarted ? "active" : undefined}>
        {timerStarted ? "Time is running" : "Timer inactive"}
      </p>
    </section>
  );
};

export default TimerChallenges;
=> we use ref to store the timer id across renders without causing re-renders
* if we just store timer id in a state variable, every time we set the timer id, it would cause a re-render which is unnecessary