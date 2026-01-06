import React, { useRef, useState } from "react";

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
