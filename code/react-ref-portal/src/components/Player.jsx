import { useState, useRef } from "react";

export default function Player() {
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
