import { use, useActionState, useOptimistic } from "react";
import { OpinionsContext } from "../store/opinions-context";
export function Opinion({ opinion: { id, title, body, userName, votes } }) {
  const { upvoteOpinion, downvoteOpinion } = use(OpinionsContext);

  // use optimistic need to update votes immediately
  // the 2nd arg is a function that returns the new value
  // mod is set by me ,
  const [optimisticVote, setVotesOptimistic] = useOptimistic(
    votes,
    (prevVotes, mode) => (mode === "up" ? prevVotes + 1 : prevVotes - 1)
  );

  // no need for formdata here since no data is being sent
  async function upVoteAction() {
    // call optimistic update nefore server call
    setVotesOptimistic("up");
    await upvoteOpinion(id);
  }

  async function downVoteAction() {
    setVotesOptimistic("down");
    await downvoteOpinion(id);
  }

  // no need for intial formstate becasue no data being sent
  const [upvoteFormState, upVoteFormAction, upVoteFormPending] =
    useActionState(upVoteAction);
  const [downvoteFormState, downVoteFormAction, downVoteFormPending] =
    useActionState(downVoteAction);
  return (
    <article>
      <header>
        <h3>{title}</h3>
        <p>Shared by {userName}</p>
      </header>
      <p>{body}</p>
      <form className="votes">
        <button disabled={upVoteFormPending} formAction={upVoteFormAction}>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <rect width="18" height="18" x="3" y="3" rx="2" />
            <path d="m16 12-4-4-4 4" />
            <path d="M12 16V8" />
          </svg>
        </button>

        <span>{optimisticVote}</span>

        <button disabled={downVoteFormPending} formAction={downVoteFormAction}>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <rect width="18" height="18" x="3" y="3" rx="2" />
            <path d="M12 8v8" />
            <path d="m8 12 4 4 4-4" />
          </svg>
        </button>
      </form>
    </article>
  );
}
