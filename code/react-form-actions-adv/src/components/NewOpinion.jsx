import { use } from "react";
import { useActionState } from "react";
import { OpinionsContext } from "../store/opinions-context";
import {useFormStatus} from "react-dom"
import Submit from "./Submit";
export function NewOpinion() {

  // we can use use() since it is react 19
  const {addOpinion} = use(OpinionsContext)

  async function shareOptinionAction(prevState , formData) {
    const title = formData.get("title");
    const body = formData.get("body");
    const userName = formData.get("userName");

    let errors = []
    if (title.trim().length < 5) {
      errors.push("Title must be at least 5 characters long.")
    }
    if (body.trim().length < 10) {
      errors.push("Opinion body must be at least 10 characters long.")
    }
    if (!userName.trim()) {
      errors.push("Plaease provide your name.")
    }

    if (errors.length) {
      return { errors,
        enteredValues: {
          title,
          body,
          userName
        }
       }
    }

    // submit to bbackend
    await addOpinion({
      title,
      body,
      userName,
    })
    return {
      errors: null
    }

  }

  const [formState, formAction,pending] = useActionState(shareOptinionAction, {
    errors: null,
  });
  return (
    <div id="new-opinion">
      <h2>Share your opinion!</h2>
      <form action={formAction}>
        <div className="control-row">
          <p className="control">
            <label htmlFor="userName">Your Name</label>
            <input type="text" id="userName" name="userName"
            defaultValue={formState.enteredValues?.userName}
            />
          </p>

          <p className="control">
            <label htmlFor="title">Title</label>
            <input type="text" id="title" name="title" 
            defaultValue={formState.enteredValues?.title}
            />
          </p>
        </div>
        <p className="control">
          <label htmlFor="body">Your Opinion</label>
          <textarea id="body" name="body" rows={5}
          defaultValue={formState.enteredValues?.body}
          ></textarea>
        </p>

        {formState.errors && (
          <ul className="errors">
            {formState.errors.map((err, idx) => (
              <li key={idx}>{err}</li>
            ))}
          </ul>
        )}
        <Submit />

        
      </form>
    </div>
  );
}
