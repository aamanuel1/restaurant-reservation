import React from 'react';
import { Field, reduxForm } from 'redux-form';


const renderField = ({ input, label, type, meta: { touched, error } }) => (
    <div>
      <label>
        {type === 'checkbox' ? (
          <input {...input} type={type} />
        ) : (
          <input {...input} type={type} placeholder={label} />
        )}
        {label}
      </label>
      {touched && error && <span style={{ color: 'red' }}>{error}</span>}
    </div>
  );


  const ReservationForm = props => {
    const { handleSubmit } = props;
  
    return (
      <div>
        <p>Reservation Section</p>
        <form onSubmit={handleSubmit}>
          <Field
            name="food"
            type="text"
            component={renderField}
            label="Food name"
          />
          <Field
            name="shortWait"
            type="checkbox"
            component={renderField}
            label="Short wait time"
          />
          <button type="submit">Submit</button>
        </form>
      </div>
    );
  };
export default reduxForm({
  form: 'reservationForm'
})(ReservationForm);