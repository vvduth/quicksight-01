import React from 'react'
import {useFormStatus} from 'react-dom'
const Submit = () => {
    const {data,pending} = useFormStatus()
  return (
    <p className="actions">
          <button type="submit"
           disabled={pending}>
            {pending ? 'Submitting...' : 'Submit Opinion'}
          </button>
        </p>
  )
}

export default Submit
