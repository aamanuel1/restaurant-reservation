import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Field, formValues, reduxForm } from 'redux-form';
import EmailForm from './forms/EmailForm';
import { Spinner } from './Spinner';
import { useGetCustomerByEmailQuery } from '../api/usersSlice';

const useCustomerByEmail = () => {
  const navigate = useNavigate();
  
  const handleEmailSubmit = (formValues) => {
    console.log('Email Submitted:', formValues);
    navigate(`/customer/${formValues.email}`)
  };

  return { handleEmailSubmit };
};

export const Home = () => {
  const { handleEmailSubmit } = useCustomerByEmail();
  
    return (
      <div>
        <h2>
      Welcome to the Restaurant Reservation System!
        </h2>
        <EmailForm onSubmit={handleEmailSubmit} />
      </div>
    )
  }