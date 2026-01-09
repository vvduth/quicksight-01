import React, { useRef } from "react";
import { useImperativeHandle } from "react";
const ResultModal = ({ targetTime, result, ref,
  remainingTime,
  onReset
 }) => {
  
    const dialog = useRef();
    const userLost = remainingTime <= 0;
    const formatedRemainingTime = (remainingTime / 1000).toFixed(2);
    const score = Math.round((1 -  remainingTime / (targetTime*1000))*100)
    useImperativeHandle(ref, () => {
        return {
            open() {
                dialog.current.showModal();
            }
        }
    })
    return (
    <dialog ref={dialog} className="result-modal">
      {userLost ? <h2>You lost!</h2> : <h2>You won!</h2>}
      {!userLost && <p>Your score is {score} points!</p>}
      <p>
        The target time was <strong>{targetTime} seconds.</strong>
      </p>
      <p>
        You stopped the timer with <strong>{formatedRemainingTime} seconds left.</strong>
      </p>

      <form method="dialog" 
        onSubmit={onReset}
      >
        <button>Close</button>
      </form>
    </dialog>
  );
};

export default ResultModal;
