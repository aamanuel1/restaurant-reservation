import React from 'react';
import { Field, reduxForm } from 'redux-form';


const validateEmail = value =>
  /\S+@\S+\.\S+/.test(value) ? undefined : 'Invalid email address';

const renderField = ({ input, label, type, meta: { touched, error } }) => (
<div>
    <label>{label}</label>
    <div>
    <input {...input} type={type} placeholder={label} />
    {touched && (error && <span style={{ color: 'red' }}>{error}</span>)}
    </div>
</div>
);


const EmailForm = props => {
  const { handleSubmit } = props;

  return (
    <form onSubmit={handleSubmit}>
      <Field
        name="email"
        type="text"
        component={renderField}
        label="Email"
        validate={[validateEmail]}
      />
      <button type="submit">Submit</button>
    </form>
  );
};

export default reduxForm({
  form: 'emailForm'
})(EmailForm);
