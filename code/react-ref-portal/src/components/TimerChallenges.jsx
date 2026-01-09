import React, { useRef, useState } from "react";
import ResultModal from "./ResultModal";

const TimerChallenges = ({ title, targetTime }) => {
  const timer = useRef(null);

  const dialogRef = useRef(null);
  const [timeRemaining, setTimeRemaining] = useState(targetTime* 1000)
  const timerIsActive = timeRemaining  > 0 && timeRemaining< targetTime * 1000;

  if (timeRemaining <= 0) {
    clearInterval(timer.current);
    dialogRef.current.open();
  }
  function handleReset() {
    setTimeRemaining(targetTime * 1000);
  }

  const handleStart = () => {
    timer.current = setInterval(() => {
      setTimeRemaining(prevTimeRemaining =>  prevTimeRemaining - 10);
    }, 10);
  };

  const handleStop = () => {
    dialogRef.current.open();
    clearInterval(timer.current);
  };
  return (
    <>
      <ResultModal 
      remainingTime={timeRemaining}  
      ref={dialogRef}
      targetTime={targetTime} result={"lost"}
        onReset={handleReset }
      />
      <section className="challenge">
        <h2>{title}</h2>
        <p className="challenge-time">
          {targetTime} second{targetTime > 1 ? "s" : ""}
        </p>
        <p>
          <button onClick={timerIsActive ? handleStop : handleStart}>
            {timerIsActive ? "Stop" : "Start "} challenge
          </button>
        </p>
        <p className={timerIsActive ? "active" : undefined}>
          {timerIsActive ? "Time is running" : "Timer inactive"}
        </p>
      </section>
    </>
  );
};

export default TimerChallenges;
