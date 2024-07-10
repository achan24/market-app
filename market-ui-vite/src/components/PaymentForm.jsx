import React from 'react'
import { useParams } from 'react-router-dom'

const PaymentForm = () => {

  const { id } = useParams()
  return (
    <div>
      <h1>
      PaymentForm Page
      </h1>
      <p>
        Payment form id: {id}
      </p>

    </div>
  )
}

export default PaymentForm