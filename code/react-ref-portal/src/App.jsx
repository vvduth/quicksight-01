import Player from './components/Player.jsx';
import TimerChallenges from './components/TimerChallenges.jsx';

function App() {
  return (
    <>
      <Player />
      <div id="challenges">
        <TimerChallenges title="Easy" targetTime={1} />
        <TimerChallenges title="Medium" targetTime={5} />
        <TimerChallenges title="Hard" targetTime={10} />
        <TimerChallenges title="Extreme" targetTime={15} />
      </div>
    </>
  );
}

export default App;
